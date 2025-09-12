package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 带时间字段的基础持久化对象抽象类
 * <p>
 * 该抽象类继承自 BasePO，并实现了 AuditTime 接口，为数据库实体提供
 * 自动时间字段管理功能。
 * <p>
 * 主要功能：
 * 1. 自动管理创建时间字段（create_time）
 * 2. 自动管理更新时间字段（update_time）
 * 3. 配合 TimeMetaObjectHandler 实现时间字段的自动填充
 * 4. 支持链式调用的 setter 方法
 * <p>
 * 字段填充策略：
 * - 创建时间：仅在插入时填充（FieldFill.INSERT）
 * - 更新时间：在插入和更新时都填充（FieldFill.INSERT_UPDATE）
 * <p>
 * 使用说明：
 * 1. 新增记录时，时间字段会自动生成，无需手动设置
 * 2. 更新记录时，更新时间会自动刷新
 * 3. 子类不建议使用 @Builder 模式，可能导致字段丢失
 *
 * @param <T> 主键类型，必须实现 Serializable 接口
 * @param <M> 模型类型，继承自 Model
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 22:49
 * @since 1.0.0
 */
public abstract class BaseWithTimePO<T extends Serializable, M extends Model<M>> extends BasePO<T, M> implements AuditTime {
    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = -8444534935163656524L;

    /** 创建时间 (公共字段) */
    @TableField(value = CREATE_TIME, fill = FieldFill.INSERT)
    private Date createTime;
    /** 最后更新时间 (公共字段) */
    @TableField(value = UPDATE_TIME, fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 获取创建时间
     * <p>
     * 该方法实现了 AuditTime 接口的 getCreateTime 方法，
     * 用于获取数据记录的创建时间。
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
     * <p>
     * 该方法用于设置数据记录的创建时间，支持链式调用。
     * 通常情况下不需要手动调用，会由 TimeMetaObjectHandler 自动填充。
     *
     * @param createTime 要设置的创建时间
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (M) this;
    }

    /**
     * 设置更新时间
     * <p>
     * 该方法用于设置数据记录的更新时间，支持链式调用。
     * 通常情况下不需要手动调用，会由 TimeMetaObjectHandler 自动填充。
     *
     * @param updateTime 要设置的更新时间
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (M) this;
    }

    /**
     * 获取更新时间
     * <p>
     * 该方法实现了 AuditTime 接口的 getUpdateTime 方法，
     * 用于获取数据记录的最后更新时间。
     *
     * @return Date 更新时间
     * @since 1.0.0
     */
    @Override
    public Date getUpdateTime() {
        return this.updateTime;
    }

}
