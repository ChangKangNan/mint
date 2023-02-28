package org.ckn.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.ckn.entity.GatewayRoute;
import org.ckn.mapper.GatewayRouteMapper;
import org.ckn.util.RouteConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 数据库路由持久层
 * @Author: junqiang.lu
 * @Date: 2021/10/19
 */
@Slf4j
@Component
public class DbRouteRepository implements RouteDefinitionRepository {

    @Autowired
    private GatewayRouteMapper routeMapper;

    /**
     * 获取路由信息
     *
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<GatewayRoute> routeEntityList = routeMapper.selectList(Wrappers.emptyWrapper());
        if (CollUtil.isEmpty(routeEntityList)) {
            log.info("数据库内没有定义路由");
            return Flux.empty();
        }
        List<RouteDefinition> routeDefinitionList = new ArrayList<>();
        routeEntityList.stream().forEach(routeEntity -> {
            routeDefinitionList.add(RouteConvert.toRouteDefinition(routeEntity));
        });
        log.info("json 数据库路由列表: {}", JSONUtil.toJsonStr(routeDefinitionList));
        return  Flux.fromIterable(routeDefinitionList);
    }

    /**
     * 新增路由
     *
     * @param route
     * @return
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> {
            GatewayRoute gatewayRoute = RouteConvert.toRouteEntity(routeDefinition);
            gatewayRoute.setDeleted(false);
            gatewayRoute.setCreateTime(new Date());
            gatewayRoute.setUpdateTime(new Date());
            routeMapper.insert(gatewayRoute);
            return Mono.empty();
        });
    }

    /**
     * 删除路由
     *
     * @param routeId
     * @return
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            routeMapper.delete(Wrappers.lambdaQuery(new GatewayRoute()).eq(GatewayRoute::getRouteId, id));
            return Mono.empty();
        });
    }
}
