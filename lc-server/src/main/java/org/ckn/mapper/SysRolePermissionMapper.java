package org.ckn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ckn.entity.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 角色-权限表 Mapper 接口
 * </p>
 *
 * @author ckn
 * @since 2023-02-24
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

}
