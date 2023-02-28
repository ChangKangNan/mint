package org.ckn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ckn.entity.GatewayRoute;
import org.ckn.entity.RouteDeleteParam;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

/**
 * <p>
 * 网关路由 服务类
 * </p>
 *
 * @author ckn
 * @since 2023-02-22
 */
public interface GatewayRouteService extends IService<GatewayRoute> {
    /**
     * 查询启用的路由列表
     *
     * @return
     */
    List<RouteDefinition> listActive();

    /**
     * 新增路由
     *
     * @param routeDefinition
     */
    void add(RouteDefinition routeDefinition);

    /**
     * 修改路由
     *
     * @param routeDefinition
     */
    void update(RouteDefinition routeDefinition);

    /**
     * 删除路由
     *
     * @param routeDeleteParam
     */
    void delete(RouteDeleteParam routeDeleteParam);
}
