package org.ckn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ckn.entity.SysUser;
import org.ckn.util.UserVo;

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
     * @return
     */
    UserVo selectUser(String username);

    /**
     * 注册
     * @param user
     * @return
     */
    UserVo save(UserVo user);

    /**
     * 注册
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    Boolean login(String userName,String password);
}
