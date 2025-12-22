create table if not exists `sys_dict_type` (
    `id`          bigint(20) unsigned not null auto_increment comment '主键Id',
    `code`        varchar(50)         not null comment '字典类型编码',
    `name`        varchar(100)        not null comment '字典类型名称',
    `description` varchar(500)        null comment '描述',
    `state`       tinyint(2) unsigned not null default 1 comment '自定义枚举:Integer:字典状态:DISABLED(0, "禁用"),:ENABLED(1, "启用");',
    `order`       int                 not null default 0 comment '排序',
    `deleted`     bigint              not null default 0 comment '通用枚举:删除状态:0: 未删除，删除就是当前数据的主键id用于代表唯一性',
    `create_time` datetime            not null default current_timestamp comment '创建时间 (公共字段)',
    `update_time` datetime            not null default current_timestamp on update current_timestamp comment '最后更新时间 (公共字段)',
    `tenant_id`   varchar(50)         null comment '租户ID',
    `client_id`   varchar(50)         null comment '客户端ID',
    primary key (`id`),
    unique key `uk_type_code` (`code`, `tenant_id`, `client_id`),
    key `idx_state` (`state`),
    key `idx_order` (`order`)
) engine = InnoDB
    auto_increment = 1
    character set = utf8mb4
    collate = utf8mb4_general_ci
    row_format = dynamic comment ='字典类型表';


create table if not exists `sys_dict_value` (
    `id`          bigint(20) unsigned not null auto_increment comment '主键Id',
    `type_code`   varchar(50)         not null comment '字典类型编码',
    `code`        varchar(50)         not null comment '字典值编码',
    `name`        varchar(100)        not null comment '字典值名称',
    `description` varchar(500)        null comment '字典值描述',
    `order`       int                 not null default 0 comment '排序',
    `state`       tinyint(2) unsigned not null default 1 comment '自定义枚举:Integer:字典值状态:DISABLED(0, "禁用"),:ENABLED(1, "启用");',
    `deleted`     bigint              not null default 0 comment '通用枚举:删除状态:0: 未删除，删除就是当前数据的主键id用于代表唯一性',
    `create_time` datetime            not null default current_timestamp comment '创建时间 (公共字段)',
    `update_time` datetime            not null default current_timestamp on update current_timestamp comment '最后更新时间 (公共字段)',
    `tenant_id`   varchar(50)         null comment '租户ID',
    `client_id`   varchar(50)         null comment '客户端ID',
    primary key (`id`),
    unique key `uk_value_code` (`type_code`, `code`, `tenant_id`, `client_id`),
    key `idx_type_code` (`type_code`),
    key `idx_state` (`state`),
    key `idx_order` (`order`)
) engine = InnoDB
    auto_increment = 1
    character set = utf8mb4
    collate = utf8mb4_general_ci
    row_format = dynamic comment ='字典值表';


-- 插入字典类型数据
insert into `sys_dict_type` (`code`, `name`, `description`, `state`, `order`, `deleted`, `tenant_id`, `client_id`)
values ('gender', '性别', '性别字典', 1, 1, 0, '0', 'default');

-- 插入字典值数据
insert into `sys_dict_value` (`type_code`, `code`, `name`, `description`, `state`, `order`, `deleted`, `tenant_id`, `client_id`)
values ('gender', '0', '未知', '性别未知', 1, 1, 0, '0', 'default'),
       ('gender', '1', '男', '男性', 1, 2, 0, '0', 'default'),
       ('gender', '2', '女', '女性', 1, 3, 0, '0', 'default');
