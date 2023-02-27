package org.ckn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.ckn.entity.*;
import org.ckn.mapper.SysUserMapper;
import org.ckn.service.*;
import org.ckn.util.UserVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ckn
 * @since 2023-02-24
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Resource
    SysUserRoleService userRoleService;
    @Resource
    SysRoleService sysRoleService;
    @Resource
    SysRoleMenuService sysRoleMenuService;
    @Resource
    SysMenuService sysMenuService;

    @Override
    public List<String> selectUserPermissions(Long userId) {
        SysUser sysUser = this.lambdaQuery().eq(SysUser::getId, userId).one();
        if (ObjectUtil.isNull(sysUser)) {
            return new ArrayList<>();
        }
        if (CollUtil.isEmpty(sysRoleService.lambdaQuery().list())) {
            return new ArrayList<>();
        }
        Set<Long> roleIds = sysRoleService.lambdaQuery().list()
                .stream().map(SysRole::getId).collect(Collectors.toSet());
        List<SysUserRole> userRoles = userRoleService.lambdaQuery()
                .eq(SysUserRole::getUserId, userId)
                .in(SysUserRole::getRoleId, roleIds)
                .list();
        if (CollUtil.isEmpty(userRoles)) {
            return new ArrayList<>();
        }
        List<Long> uRoles = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        List<String> auth = new ArrayList<>();
        for (Long uRole : uRoles) {
            SysRole userRole = sysRoleService.lambdaQuery().eq(SysRole::getId, uRole).one();
            List<SysRoleMenu> rolePermissions = sysRoleMenuService.lambdaQuery()
                    .eq(SysRoleMenu::getRoleId, uRole)
                    .list();
            if (CollUtil.isNotEmpty(rolePermissions) && userRole != null) {
                auth.add("ROLE_" + userRole.getRoleName());
                for (SysRoleMenu permission : rolePermissions) {
                    Long permissionId = permission.getMenuId();
                    SysMenu sysPermission = sysMenuService.lambdaQuery().eq(SysMenu::getId, permissionId).one();
                    if (sysPermission != null && StrUtil.isNotBlank(sysPermission.getMenuUrl())) {
                        auth.add(sysPermission.getMenuUrl());
                    }
                }
            }
        }
        return auth;
    }

    @Override
    public UserVo selectUser(String username) {
        SysUser sysUser = this.lambdaQuery().eq(SysUser::getUsername, username).one();
        if(sysUser ==null){
            return null;
        }
        UserVo user = new UserVo();
        BeanUtil.copyProperties(sysUser, user);
        return user;
    }

    @Override
    public UserVo save(UserVo user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new RuntimeException("用户信息不全!");
        }
        Integer count = this.lambdaQuery().eq(SysUser::getUsername, user.getUsername()).count();
        if (count > 0) {
            throw new RuntimeException("当前用户名已被注册!");
        }
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(user, sysUser);
        String password = sysUser.getPassword();
        //加密
        sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        this.save(sysUser);
        return user;
    }

    @Override
    public Boolean login(String userName, String password) {
        log.info("userName:" + userName);
        log.info("password:" + password);
        if (StrUtil.isBlank(userName) || StrUtil.isBlank(password)) {
            throw new RuntimeException("输入信息不规范!");
        }
        Integer count = this.lambdaQuery()
                .eq(SysUser::getUsername, userName)
                .eq(SysUser::getPassword, new BCryptPasswordEncoder().encode(password))
                .count();
        if (count == 0) {
            throw new RuntimeException("用户名或密码错误!");
        }
        //登录成功
        return true;
    }
}
