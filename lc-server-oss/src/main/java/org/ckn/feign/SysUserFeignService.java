package org.ckn.feign;

import org.ckn.util.ApiResult;
import org.ckn.util.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author ckn
 * @date 2023/2/24
 */
@FeignClient(name = "lc-server", path = "/sys-user")
public interface SysUserFeignService {
    @RequestMapping("/permissions/{userId}")
    ApiResult<List<String>> selectUserPermissions(@PathVariable("userId") Long id);

    @RequestMapping("/user/{username}")
    ApiResult<User> selectUser(@PathVariable("username") String username);
}
