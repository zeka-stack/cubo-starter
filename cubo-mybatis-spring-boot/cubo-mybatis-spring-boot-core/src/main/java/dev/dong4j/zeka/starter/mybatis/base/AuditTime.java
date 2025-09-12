package dev.dong4j.zeka.starter.mybatis.base;

import java.util.Date;

/**
 * 审计时间字段接口
 * <p>
 * 该接口定义了数据库实体中审计时间字段的标准规范，包括创建时间和更新时间。
 * 实现该接口的实体类将自动支持时间字段的自动填充功能。
 * <p>
 * 主要功能：
 * 1. 定义创建时间字段的访问方法
 * 2. 定义更新时间字段的访问方法
 * 3. 提供字段名称常量，便于统一管理
 * <p>
 * 配合 TimeMetaObjectHandler 使用，可实现：
 * - 插入数据时自动设置创建时间
 * - 更新数据时自动设置更新时间
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.22 13:53
 * @since 1.0.0
 */
public interface AuditTime {
    /** CREATE_TIME */
    String CREATE_TIME = "create_time";
    /** UPDATE_TIME */
    String UPDATE_TIME = "update_time";

    /**
     * 获取创建时间
     * <p>
     * 该方法用于获取数据记录的创建时间，通常在数据插入时自动设置。
     * 创建时间一旦设置后不应再被修改，用于记录数据的首次创建时刻。
     *
     * @return Date 创建时间
     * @since 1.0.0
     */
    Date getCreateTime();

    /**
     * 获取更新时间
     * <p>
     * 该方法用于获取数据记录的最后更新时间，在数据插入和更新时都会自动设置。
     * 更新时间反映了数据的最新修改时刻，用于追踪数据变更历史。
     *
     * @return Date 更新时间
     * @since 1.0.0
     */
    Date getUpdateTime();

}
