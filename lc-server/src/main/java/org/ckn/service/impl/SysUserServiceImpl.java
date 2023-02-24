package org.ckn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.cli.Digest;
import org.ckn.entity.*;
import org.ckn.mapper.SysUserMapper;
import org.ckn.service.*;
import org.ckn.util.User;
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
    SysRolePermissionService sysRolePermissionService;
    @Resource
    SysPermissionService permissionService;

    @Override
    public List<String> selectUserPermissions(Long userId) {
        SysUser sysUser = this.lambdaQuery().eq(SysUser::getId, userId).one();
        if (ObjectUtil.isNull(sysUser)) {
            return new ArrayList<>();
        }
        if(CollUtil.isEmpty(sysRoleService.lambdaQuery().list())){
            return new ArrayList<>();
        }
        Set<Long> roleIds = sysRoleService.lambdaQuery().list()
                .stream().map(SysRole::getId).collect(Collectors.toSet());
        List<SysUserRole> userRoles = userRoleService.lambdaQuery()
                .eq(SysUserRole::getUserId, userId)
                .notIn(SysUserRole::getRoleId, roleIds)
                .list();
        if (CollUtil.isEmpty(userRoles)) {
            return new ArrayList<>();
        }
        List<Long> uRoles = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> permissions = permissionService.lambdaQuery().list().stream().map(SysPermission::getId).collect(Collectors.toList());
        List<SysRolePermission> rolePermissions = sysRolePermissionService.lambdaQuery()
                .in(SysRolePermission::getPermissionId, permissions)
                .in(SysRolePermission::getRoleId, uRoles)
                .list();
        if (CollUtil.isEmpty(rolePermissions)) {
            return new ArrayList<>();
        }
        List<Long> permissionIds = rolePermissions.stream().map(SysRolePermission::getPermissionId).distinct().collect(Collectors.toList());
        return permissionService.lambdaQuery()
                .in(SysPermission::getId, permissionIds)
                .list()
                .stream()
                .map(SysPermission::getPermission)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public User selectUser(String username) {
        SysUser sysUser = this.lambdaQuery().eq(SysUser::getUsername, username).one();
        User user = new User();
        BeanUtil.copyProperties(sysUser,user);
        return user;
    }

    @Override
    public User save(User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new RuntimeException("用户信息不全!");
        }
        Integer count = this.lambdaQuery().eq(SysUser::getUsername, user.getUsername()).count();
        if(count>0){
            throw new RuntimeException("当前用户名已被注册!");
        }
        SysUser sysUser=new SysUser();
        BeanUtil.copyProperties(user,sysUser);
        String password = sysUser.getPassword();
        //加密
        sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        this.save(sysUser);
        return user;
    }

    @Override
    public Boolean login(String userName, String password) {
        log.info("userName:"+userName);
        log.info("password:"+password);
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
