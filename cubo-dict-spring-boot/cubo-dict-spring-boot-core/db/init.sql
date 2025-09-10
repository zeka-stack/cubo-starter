CREATE TABLE IF NOT EXISTS `sys_dictionary_type`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `code`        varchar(50)         NOT NULL COMMENT '字典类型编码',
    `name`        varchar(100)        NOT NULL COMMENT '字典类型名称',
    `description` varchar(500)        NULL COMMENT '描述',
    `state`       tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '自定义枚举:Integer:字典状态:DISABLED(0, "禁用"),:ENABLED(1, "启用");',
    `order`       int                 NOT NULL DEFAULT 0 COMMENT '排序',
    `deleted`     bigint              NOT NULL DEFAULT 0 COMMENT '通用枚举:删除状态:0: 未删除，删除就是当前数据的主键id用于代表唯一性',
    `create_time` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 (公共字段)',
    `update_time` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间 (公共字段)',
    `tenant_id`   varchar(50)         NULL COMMENT '租户ID',
    `client_id`   varchar(50)         NULL COMMENT '客户端ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_code` (`code`, `tenant_id`, `client_id`),
    KEY `idx_state` (`state`),
    KEY `idx_order` (`order`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='字典类型表';


CREATE TABLE IF NOT EXISTS `sys_dictionary_value`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `type_code`   varchar(50)         NOT NULL COMMENT '字典类型编码',
    `code`        varchar(50)         NOT NULL COMMENT '字典值编码',
    `name`        varchar(100)        NOT NULL COMMENT '字典值名称',
    `description` varchar(500)        NULL COMMENT '字典值描述',
    `order`       int                 NOT NULL DEFAULT 0 COMMENT '排序',
    `state`       tinyint(2) unsigned NOT NULL DEFAULT 1 COMMENT '自定义枚举:Integer:字典值状态:DISABLED(0, "禁用"),:ENABLED(1, "启用");',
    `deleted`     bigint              NOT NULL DEFAULT 0 COMMENT '通用枚举:删除状态:0: 未删除，删除就是当前数据的主键id用于代表唯一性',
    `create_time` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 (公共字段)',
    `update_time` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间 (公共字段)',
    `tenant_id`   varchar(50)         NULL COMMENT '租户ID',
    `client_id`   varchar(50)         NULL COMMENT '客户端ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_value_code` (`type_code`, `code`, `tenant_id`, `client_id`),
    KEY `idx_type_code` (`type_code`),
    KEY `idx_state` (`state`),
    KEY `idx_order` (`order`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='字典值表';


-- 插入字典类型数据
INSERT INTO `sys_dictionary_type` (`code`, `name`, `description`, `state`, `order`, `deleted`, `tenant_id`, `client_id`)
VALUES ('gender', '性别', '性别字典', 1, 1, 0, '0', 'default');

-- 插入字典值数据
INSERT INTO `sys_dictionary_value` (`type_code`, `code`, `name`, `description`, `state`, `order`, `deleted`, `tenant_id`, `client_id`)
VALUES ('gender', '0', '未知', '性别未知', 1, 1, 0, '0', 'default'),
       ('gender', '1', '男', '男性', 1, 2, 0, '0', 'default'),
       ('gender', '2', '女', '女性', 1, 3, 0, '0', 'default');
