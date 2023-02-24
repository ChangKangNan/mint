package org.ckn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ckn.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ckn
 * @since 2023-02-24
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
