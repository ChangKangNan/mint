package org.ckn.controller;


import org.ckn.service.SysUserService;
import org.ckn.util.ApiResult;
import org.ckn.util.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ckn
 * @since 2023-02-24
 */
@RestController
@RequestMapping("/sys-user")
@CrossOrigin
public class SysUserController {
    @Resource
    SysUserService userService;

    @RequestMapping("/permissions/{userId}")
    public ApiResult<List<String>> selectUserPermissions(@PathVariable("userId") Long userId) {
        return ApiResult.success(userService.selectUserPermissions(userId));
    }

    @RequestMapping("/user/{username}")
    public ApiResult<User> selectUser(@PathVariable("username") String username) {
        return ApiResult.success(userService.selectUser(username));
    }

    @PostMapping("/user")
    public ApiResult<User> register(@RequestBody User user) {
        return ApiResult.success(userService.save(user));
    }

    @GetMapping("/user/login")
    public ApiResult<Boolean> login(String userName,String password) {
        return ApiResult.success(userService.login(userName,password));
    }

}

