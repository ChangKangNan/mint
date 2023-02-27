package org.ckn.service;

import cn.hutool.core.collection.CollUtil;
import org.ckn.entity.AuthUser;
import org.ckn.feign.SysUserFeignService;
import org.ckn.util.ApiResult;
import org.ckn.util.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserFeignService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApiResult<UserVo> userResult = service.selectUser(username);
        if (userResult.getData() == null) {
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userResult.getData(),userVo);
        ApiResult<List<String>> apiResult = service.selectUserPermissions(userVo.getId());
        List<String> data = apiResult.getData();
        if(CollUtil.isNotEmpty(data)){
            List<SimpleGrantedAuthority> simpleGrantedAuthorities = data.stream().distinct().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            grantedAuthorities.addAll(simpleGrantedAuthorities);
        }
        AuthUser user = new AuthUser(userVo.getUsername(), userVo.getPassword(), grantedAuthorities);
        user.setId(userVo.getId());
        return user;
    }
}
