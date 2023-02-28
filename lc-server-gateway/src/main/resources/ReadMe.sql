create table gateway_route
(
    id           bigint unsigned auto_increment comment '数据库id'
        primary key,
    route_id     varchar(32)     null comment '路由id',
    uri          varchar(128)    null comment '请求地址',
    predicates   varchar(512)    null comment '断言',
    filters      varchar(512)    null comment '拦截器',
    metadata     varchar(128)    null comment '附加参数',
    filter_order int             null comment '执行顺序,数值越小优先级越高',
    create_time  bigint unsigned null comment '创建时间',
    update_time  bigint unsigned null comment '更新时间',
    deleted      tinyint(1)      null comment '删除标志'
)
    comment '网关路由' charset = utf8;

create table undo_log
(
    id            bigint auto_increment
        primary key,
    branch_id     bigint       not null,
    xid           varchar(100) not null,
    context       varchar(128) not null,
    rollback_info longblob     not null,
    log_status    int          not null,
    log_created   datetime     not null,
    log_modified  datetime     not null,
    ext           varchar(100) null,
    constraint ux_undo_log
        unique (xid, branch_id)
);

