package org.ckn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ckn.entity.GatewayRoute;
import org.ckn.entity.RouteDeleteParam;
import org.ckn.mapper.GatewayRouteMapper;
import org.ckn.repository.DbRouteRepository;
import org.ckn.service.GatewayRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteRefreshListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 网关路由 服务实现类
 * </p>
 *
 * @author ckn
 * @since 2023-02-22
 */
@Service
public class GatewayRouteServiceImpl extends ServiceImpl<GatewayRouteMapper, GatewayRoute> implements GatewayRouteService, ApplicationEventPublisherAware {
    @Autowired
    private DbRouteRepository dbRouteRepository;

    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * 查询启用的路由列表
     *
     * @return
     */
    @Override
    public List<RouteDefinition> listActive() {
        Flux<RouteDefinition> routeDefinitionFlux = dbRouteRepository.getRouteDefinitions();
        if (Objects.isNull(routeDefinitionFlux)) {
            return Collections.emptyList();
        }
        List<RouteDefinition> routeDefinitionList = new ArrayList<>();
        routeDefinitionFlux.collectList().subscribe(routeDefinitionList::addAll);
        return routeDefinitionList;
    }

    /**
     * 新增路由
     *
     * @param routeDefinition
     */
    @Override
    public void add(RouteDefinition routeDefinition) {
        dbRouteRepository.save(Mono.just(routeDefinition)).subscribe();
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 修改路由
     *
     * @param routeDefinition
     */
    @Override
    public void update(RouteDefinition routeDefinition) {
        dbRouteRepository.delete(Mono.just(routeDefinition.getId())).subscribe();
        dbRouteRepository.save(Mono.just(routeDefinition)).subscribe();
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 删除路由
     *
     * @param routeDeleteParam
     */
    @Override
    public void delete(RouteDeleteParam routeDeleteParam) {
        dbRouteRepository.delete(Mono.just(routeDeleteParam.getRouteId())).subscribe();
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
