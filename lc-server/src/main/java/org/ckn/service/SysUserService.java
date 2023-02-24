package org.ckn.service;

import org.ckn.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ckn.util.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ckn
 * @since 2023-02-24
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return
     */
    List<String>  selectUserPermissions(Long userId);

    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return
     */
    User selectUser(String username);

    /**
     * 注册
     * @param user
     * @return
     */
    User save(User user);

    /**
     * 注册
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    Boolean login(String userName,String password);
}
