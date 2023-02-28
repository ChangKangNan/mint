package org.ckn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ckn.entity.GatewayRoute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 网关路由 Mapper 接口
 * </p>
 *
 * @author ckn
 * @since 2023-02-22
 */
@Mapper
public interface GatewayRouteMapper extends BaseMapper<GatewayRoute> {

}
