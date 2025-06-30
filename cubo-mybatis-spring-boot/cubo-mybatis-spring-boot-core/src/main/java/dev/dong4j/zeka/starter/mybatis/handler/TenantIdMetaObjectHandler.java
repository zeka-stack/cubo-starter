package dev.dong4j.zeka.starter.mybatis.handler;

import dev.dong4j.zeka.kernel.common.context.ExpandIds;
import dev.dong4j.zeka.kernel.common.util.JsonUtils;
import dev.dong4j.zeka.starter.mybatis.base.BasePO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

/**
 * <p>Description: 处理新增和更新的基础数据填充,配合 BaseEntity 和 MyBatisPlusConfig 使用 </p>
 * {@link BasePO}
 *
 * @author dong4j
 * @version 1.2.3
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
     * @since 1.8.0
     */
    @Override
    protected void setFieldValue(MetaObject metaObject, ExpandIds expandIds) {
        if (log.isDebugEnabled()) {
            log.debug("自动写入 tenantId, originalObject: [{}], tenantId: [{}]",
                JsonUtils.toJson(metaObject.getOriginalObject()),
                expandIds.getTenantId().orElse(null));
        }
        this.setFieldValByName("tenantId", expandIds.getTenantId().orElse(-1L), metaObject);
    }

}
