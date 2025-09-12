package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serial;
import java.io.Serializable;

/**
 * 扩展渠道信息的基础持久化对象抽象类
 * <p>
 * 该抽象类继承自 BaseExtendPO，并实现了 Channel 接口，
 * 为数据库实体提供最完整的基础功能，包括：
 * - 主键管理（继承自 BasePO）
 * - 逻辑删除功能（继承自 BaseExtendPO）
 * - 审计时间字段（继承自 BaseExtendPO）
 * - 多租户和多客户端渠道信息
 * <p>
 * 主要功能：
 * 1. 自动管理租户 ID 字段（tenant_id）
 * 2. 自动管理客户端 ID 字段（client_id）
 * 3. 继承完整的基础实体功能
 * 4. 支持链式调用的 setter 方法
 * <p>
 * 字段填充策略：
 * - 租户 ID：仅在插入时填充（FieldFill.INSERT）
 * - 客户端 ID：仅在插入时填充（FieldFill.INSERT）
 * - 其他字段：继承自父类的填充策略
 * <p>
 * 适用场景：
 * - SaaS 多租户系统的完整业务实体
 * - 需要完整审计功能的数据表
 * - 企业级应用的标准实体基类
 * - 需要数据隔离和来源追踪的业务场景
 *
 * @param <T> 主键类型，必须实现 Serializable 接口
 * @param <M> 模型类型，继承自 Model
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.18 11:37
 * @since 1.0.0
 */
public abstract class BaseExtendChannelPO<T extends Serializable, M extends Model<M>> extends BaseExtendPO<T, M>
    implements Channel {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = -2716066043048753401L;
    /** Tenant id */
    @TableField(value = TENANT_ID, fill = FieldFill.INSERT)
    private Long tenantId;

    /** Client id */
    @TableField(value = CLIENT_ID, fill = FieldFill.INSERT)
    private String clientId;

    /**
     * 获取租户 ID
     * <p>
     * 该方法用于获取数据记录所属的租户标识，实现 Channel 接口的方法。
     * 租户 ID 在数据插入时由 TenantIdMetaObjectHandler 自动填充，
     * 用于多租户系统中的数据隔离和权限控制。
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
     * 该方法用于获取数据记录的客户端标识，实现 Channel 接口的方法。
     * 客户端 ID 在数据插入时由 ClientIdMetIdaObjectHandler 自动填充，
     * 用于标识数据的来源客户端，便于数据追踪和统计分析。
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
     * 该方法用于设置数据记录的租户标识，支持链式调用。
     * 通常由元数据处理器自动填充，业务代码一般不直接调用。
     * 在多租户系统中，确保数据归属的正确性至关重要。
     *
     * @param tenantId 租户 ID
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
     * 该方法用于设置数据记录的客户端标识，支持链式调用。
     * 通常由元数据处理器自动填充，业务代码一般不直接调用。
     * 客户端 ID 用于数据来源追踪和多客户端场景下的数据标识。
     *
     * @param clientId 客户端 ID
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setClientId(String clientId) {
        this.clientId = clientId;
        return (M) this;
    }
}
