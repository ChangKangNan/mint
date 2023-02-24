package org.ckn.service;

import lombok.extern.slf4j.Slf4j;
import org.ckn.feign.SysUserFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserFeignService service;

    /**
     * 加载用户的用户名
     * 基于用户名获取数据库中的用户信息
     *
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //基于feign方式获取远程数据并封装
        //1.基于用户名获取用户信息
        org.ckn.util.User user = service.selectUser(username).getData();
        if(user==null) throw new UsernameNotFoundException("用户不存在");

        //2.基于用于id查询用户权限
        List<String> permissions = service.selectUserPermissions(user.getId()).getData();
        log.debug("permissions {}",permissions.toString());

        //3.对查询结果进行封装并返回
        //3.对查询结果进行封装并返回
        User userInfo = new User(username,
                user.getPassword(),
                AuthorityUtils.createAuthorityList(permissions.toArray(new String[]{})));
        return userInfo;
        //返回给认证中心,认证中心会基于用户输入的密码以及数据库的密码做一个比对
    }
}
