package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serial;
import java.io.Serializable;

/**
 * 带渠道信息的基础持久化对象抽象类
 * <p>
 * 该抽象类继承自 BaseWithTimePO，并实现了 Channel 接口，为数据库实体提供
 * 多租户和多客户端场景下的渠道信息自动管理功能。
 * <p>
 * 主要功能：
 * 1. 自动管理租户 ID 字段（tenant_id）
 * 2. 自动管理客户端 ID 字段（client_id）
 * 3. 继承时间字段的自动填充功能
 * 4. 支持链式调用的 setter 方法
 * <p>
 * 字段填充策略：
 * - 租户 ID：仅在插入时填充（FieldFill.INSERT）
 * - 客户端 ID：仅在插入时填充（FieldFill.INSERT）
 * - 时间字段：继承自父类的填充策略
 * <p>
 * 使用说明：
 * 1. 新增记录时，渠道字段会自动生成，无需手动设置
 * 2. 更新记录时，渠道字段不会被修改，保持数据归属的稳定性
 * 3. 子类不建议使用 @Builder 模式，可能导致字段丢失
 * <p>
 * 适用场景：
 * - SaaS 多租户系统的数据隔离
 * - 多客户端应用的数据来源标识
 * - 需要同时管理时间和渠道信息的实体
 *
 * @param <T> 主键类型，必须实现 Serializable 接口
 * @param <M> 模型类型，继承自 Model
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 22:49
 * @since 1.0.0
 */
public abstract class BaseWithChannelPO<T extends Serializable, M extends Model<M>> extends BaseWithTimePO<T, M> implements Channel {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 9002400483320996799L;

    /** Tenant id */
    @TableField(value = TENANT_ID, fill = FieldFill.INSERT)
    private Long tenantId;

    /** Client id */
    @TableField(value = CLIENT_ID, fill = FieldFill.INSERT)
    private String clientId;

    /**
     * 获取租户 ID
     * <p>
     * 该方法实现了 Channel 接口的 getTenantId 方法，
     * 用于获取数据记录所属的租户标识。
     *
     * @return Long 租户 ID
     * @since 1.0.0
     */
    @Override
    public Long getTenantId() {
        return this.tenantId;
    }

    /**
     * 获取客户端 ID
     * <p>
     * 该方法实现了 Channel 接口的 getClientId 方法，
     * 用于获取数据记录的客户端标识。
     *
     * @return String 客户端 ID
     * @since 1.0.0
     */
    @Override
    public String getClientId() {
        return this.clientId;
    }

    /**
     * 设置租户 ID
     * <p>
     * 该方法用于设置数据记录的租户 ID，支持链式调用。
     * 通常情况下不需要手动调用，会由相应的元数据处理器自动填充。
     *
     * @param tenantId 要设置的租户 ID
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return (M) this;
    }

    /**
     * 设置客户端 ID
     * <p>
     * 该方法用于设置数据记录的客户端 ID，支持链式调用。
     * 通常情况下不需要手动调用，会由相应的元数据处理器自动填充。
     *
     * @param clientId 要设置的客户端 ID
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setClientId(String clientId) {
        this.clientId = clientId;
        return (M) this;
    }

}
