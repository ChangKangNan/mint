package org.ckn.controller;

import org.ckn.util.ApiResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ckn
 * @date 2023/2/28
 */
@RestController
public class OauthController {
    @RequestMapping("/test")
    public ApiResult<String> catchInfo() {
        return ApiResult.success("访问成功");
    }
}
