package dev.dong4j.zeka.starter.mybatis.handler;

import dev.dong4j.zeka.kernel.common.context.ExpandIds;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 租户 ID 元数据处理器
 *
 * 该处理器用于在数据插入时自动填充租户 ID 字段，支持多租户系统的数据隔离。
 * 继承自 AbstractDataIdMetaObjectHandler，提供了租户 ID 的自动填充功能。
 *
 * 主要功能：
 * 1. 在数据插入时自动设置租户 ID（tenant_id）
 * 2. 从当前上下文中获取租户信息
 * 3. 支持多租户数据隔离
 * 4. 提供调试日志记录
 *
 * 工作原理：
 * - 从 ExpandIds 上下文中获取当前租户 ID
 * - 如果获取不到租户 ID，则设置为默认值 -1
 * - 只在插入时填充，更新时不修改租户归属
 *
 * 使用场景：
 * - SaaS 多租户系统
 * - 需要数据隔离的企业应用
 * - 多组织架构的系统
 *
 * 配合使用：
 * - 与 BasePO 及其子类配合使用
 * - 需要在实体类中定义 tenantId 字段
 * - 配合 @TableField(fill = FieldFill.INSERT) 注解使用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:16
 * @since 1.0.0
 */
@Slf4j
public class TenantIdMetaObjectHandler extends AbstractDataIdMetaObjectHandler {

    /**
     * Sets field value *
     *
     * @param metaObject meta object
     * @param expandIds  expand ids
     * @since 1.0.0
     */
    @Override
    protected void setFieldValue(MetaObject metaObject, ExpandIds expandIds) {
        if (log.isDebugEnabled()) {
            log.debug("自动写入 tenantId, originalObject: [{}], tenantId: [{}]",
                Jsons.toJson(metaObject.getOriginalObject()),
                expandIds.getTenantId().orElse(null));
        }
        this.setFieldValByName("tenantId", expandIds.getTenantId().orElse(-1L), metaObject);
    }

}
