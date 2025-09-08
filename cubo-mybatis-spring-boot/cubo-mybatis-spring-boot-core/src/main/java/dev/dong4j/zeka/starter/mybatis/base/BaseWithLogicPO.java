package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import java.io.Serializable;

/**
 * 带逻辑删除的基础持久化对象抽象类
 *
 * 该抽象类继承自 BasePO，并实现了 LogicDelete 接口，为数据库实体提供
 * 逻辑删除功能，实现软删除机制。
 *
 * 主要功能：
 * 1. 提供逻辑删除功能，避免物理删除数据
 * 2. 自动管理删除状态字段（deleted）
 * 3. 继承基础 PO 的主键管理功能
 * 4. 支持链式调用的 setter 方法
 *
 * 逻辑删除配置：
 * - 使用 @TableLogic 注解配置逻辑删除行为
 * - 未删除值：0（正常状态）
 * - 已删除值：当前记录的 ID（确保删除后的唯一性）
 *
 * 使用说明：
 * 1. 新增记录时，deleted 字段会自动设置为 0
 * 2. 删除操作会将 deleted 字段设置为记录的 ID 值
 * 3. 查询时会自动过滤已删除的记录
 * 4. 子类不建议使用 @Builder 模式，可能导致字段丢失
 *
 * 适用场景：
 * - 需要逻辑删除功能但不需要时间审计的实体
 * - 简单的业务数据表
 * - 对性能要求较高，字段较少的实体
 *
 * @param <T> 主键类型，必须实现 Serializable 接口
 * @param <M> 模型类型，继承自 Model
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.22 22:49
 * @since 1.0.0
 */
public abstract class BaseWithLogicPO<T extends Serializable, M extends Model<M>> extends BasePO<T, M> implements LogicDelete {

    /** serialVersionUID */
    private static final long serialVersionUID = 7951121625400869460L;

    /** 逻辑删除标识: 逻辑已删除值(1); 逻辑未删除值(0) 默认为 0 */
    @TableLogic(value = "0", delval = "id")
    @TableField(value = DELETED)
    private DeletedEnum deleted;

    /**
     * 获取删除状态
     *
     * 该方法实现了 LogicDelete 接口的 getDeleted 方法，
     * 用于获取数据记录的逻辑删除状态。
     *
     * 返回值说明：
     * - DeletedEnum.N：数据未被删除（正常状态）
     * - DeletedEnum.Y：数据已被逻辑删除
     *
     * @return DeletedEnum 删除状态枚举
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
     * 注意：手动设置删除状态可能会影响数据的一致性，请谨慎使用。
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

}
