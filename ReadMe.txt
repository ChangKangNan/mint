https://blog.csdn.net/LJ19971117/article/details/121913759

create table mint.sys_menu
(
    id          bigint auto_increment comment '菜单Id'
        primary key,
    parent_id   bigint default -1 null comment '上级Id',
    menu_name   varchar(100)      null comment '菜单名称',
    menu_url    varchar(100)      null comment '菜单链接',
    icon        varchar(10)       null comment '图标',
    create_time datetime          null comment '创建时间',
    update_time datetime          null comment '更新时间',
    deleted     tinyint(1)        null comment '删除标志'
)
    comment '菜单表' charset = utf8;

create table mint.sys_permission
(
    id          bigint auto_increment
        primary key,
    name        varchar(255) null comment '菜单权限名称',
    permission  varchar(255) null comment '权限',
    sys_menu_id bigint       null comment '菜单ID',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '更新时间',
    deleted     tinyint(1)   null comment '删除标志'
)
    comment '权限表' charset = utf8;

create table mint.sys_role
(
    id          bigint auto_increment
        primary key,
    role_name   varchar(255) null comment '角色名称',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '更新时间',
    deleted     tinyint(1)   null comment '删除标志'
)
    comment '角色表' charset = utf8;

create table mint.sys_role_permission
(
    id            bigint auto_increment
        primary key,
    role_id       bigint     null comment '角色ID',
    permission_id bigint     null comment '菜单ID',
    create_time   datetime   null comment '创建时间',
    update_time   datetime   null comment '更新时间',
    deleted       tinyint(1) null comment '删除标志'
)
    comment '角色-权限表' charset = utf8;

create table mint.sys_user
(
    id          bigint auto_increment
        primary key,
    username    varchar(55)  null comment '用户名',
    password    varchar(255) null comment '密码',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '更新时间',
    deleted     tinyint(1)   null comment '删除标志'
)
    comment '用户表' charset = utf8;

create table mint.sys_user_role
(
    id          bigint auto_increment
        primary key,
    user_id     bigint     null comment '用户ID',
    role_id     bigint     null comment '角色ID',
    create_time datetime   null comment '创建时间',
    update_time datetime   null comment '更新时间',
    deleted     tinyint(1) null comment '删除标志'
)
    comment '用户-角色表' charset = utf8;

