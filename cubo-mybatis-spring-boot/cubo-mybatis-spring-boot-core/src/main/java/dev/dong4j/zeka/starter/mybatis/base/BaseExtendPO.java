package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import java.io.Serializable;
import java.util.Date;

/**
 * 扩展基础持久化对象抽象类
 *
 * 该抽象类继承自 BasePO，并实现了 LogicDelete 和 AuditTime 接口，
 * 为数据库实体提供完整的基础功能，包括逻辑删除和审计时间字段。
 *
 * 主要功能：
 * 1. 提供逻辑删除功能，支持软删除机制
 * 2. 自动管理创建时间和更新时间字段
 * 3. 继承基础 PO 的主键管理功能
 * 4. 支持链式调用的 setter 方法
 *
 * 字段配置：
 * - deleted：逻辑删除标识，使用 @TableLogic 注解配置
 * - createTime：创建时间，仅在插入时自动填充
 * - updateTime：更新时间，在插入和更新时都自动填充
 *
 * 逻辑删除配置：
 * - 未删除值：0
 * - 已删除值：当前记录的 ID（确保唯一性）
 *
 * 适用场景：
 * - 需要逻辑删除功能的业务实体
 * - 需要审计时间字段的数据表
 * - 标准的业务数据实体基类
 *
 * @param <T> 主键类型，必须实现 Serializable 接口
 * @param <M> 模型类型，继承自 Model
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.22 13:58
 * @since 1.0.0
 */
public abstract class BaseExtendPO<T extends Serializable, M extends Model<M>> extends BasePO<T, M>
    implements LogicDelete, AuditTime {

    /** serialVersionUID */
    private static final long serialVersionUID = 7951121625400869460L;

    /** 逻辑删除标识: 逻辑已删除值(1); 逻辑未删除值(0) 默认为 0 */
    @TableField(value = DELETED)
    @TableLogic(value = "0", delval = "id")
    private DeletedEnum deleted;
    /** 创建时间 (公共字段) */
    @TableField(value = CREATE_TIME, fill = FieldFill.INSERT)
    private Date createTime;
    /** 最后更新时间 (公共字段) */
    @TableField(value = UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 获取删除状态
     *
     * 该方法用于获取数据记录的逻辑删除状态，返回 DeletedEnum 枚举值。
     * 配合 @TableLogic 注解实现逻辑删除功能，避免物理删除数据。
     *
     * @return DeletedEnum 删除状态枚举（NORMAL：未删除，DELETED：已删除）
     * @since 1.0.0
     */
    @Override
    public DeletedEnum getDeleted() {
        return this.deleted;
    }

    /**
     * 设置删除状态
     *
     * 该方法用于设置数据记录的逻辑删除状态，支持链式调用。
     * 通常由 MyBatis Plus 的逻辑删除功能自动调用，业务代码一般不直接使用。
     *
     * @param deleted 删除状态枚举值
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setDeleted(DeletedEnum deleted) {
        this.deleted = deleted;
        return (M) this;
    }

    /**
     * 获取创建时间
     *
     * 该方法用于获取数据记录的创建时间，实现 AuditTime 接口的方法。
     * 创建时间在数据插入时由 TimeMetaObjectHandler 自动填充。
     *
     * @return Date 创建时间
     * @since 1.0.0
     */
    @Override
    public Date getCreateTime() {
        return this.createTime;
    }

    /**
     * 设置创建时间
     *
     * 该方法用于设置数据记录的创建时间，支持链式调用。
     * 通常由元数据处理器自动填充，业务代码一般不直接调用。
     *
     * @param createTime 创建时间
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (M) this;
    }

    /**
     * 获取更新时间
     *
     * 该方法用于获取数据记录的最后更新时间，实现 AuditTime 接口的方法。
     * 更新时间在数据插入和更新时由 TimeMetaObjectHandler 自动填充。
     *
     * @return Date 更新时间
     * @since 1.0.0
     */
    @Override
    public Date getUpdateTime() {
        return this.updateTime;
    }

    /**
     * 设置更新时间
     *
     * 该方法用于设置数据记录的最后更新时间，支持链式调用。
     * 通常由元数据处理器自动填充，业务代码一般不直接调用。
     *
     * @param updateTime 更新时间
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (M) this;
    }
}
