package dev.dong4j.zeka.starter.mybatis.handler;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 时间字段元数据处理器
 *
 * 该处理器用于自动填充时间相关字段，实现审计时间的自动管理。
 * 实现了 MetaObjectChain 接口，提供了创建时间和更新时间的自动填充功能。
 *
 * 主要功能：
 * 1. 在数据插入时自动设置创建时间（create_time）
 * 2. 在数据插入和更新时自动设置更新时间（update_time）
 * 3. 使用当前系统时间作为填充值
 * 4. 支持 Date 类型的时间字段
 *
 * 填充策略：
 * - 插入时：同时设置创建时间和更新时间
 * - 更新时：只设置更新时间，保持创建时间不变
 *
 * 使用场景：
 * - 需要审计时间字段的数据表
 * - 数据变更历史追踪
 * - 业务数据的时间戳管理
 *
 * 配合使用：
 * - 与 BasePO 及其子类配合使用
 * - 需要在实体类中定义 createTime 和 updateTime 字段
 * - 配合相应的 @TableField 注解使用
 * - 实现 AuditTime 接口的实体类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:16
 * @since 1.0.0
 */
@Slf4j
public class TimeMetaObjectHandler implements MetaObjectChain {

    /**
     * 新增数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void insertFill(MetaObject metaObject, MetaObjectChain chain) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    /**
     * 更新数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void updateFill(MetaObject metaObject, MetaObjectChain chain) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

}
