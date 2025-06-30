package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import dev.dong4j.zeka.starter.mybatis.handler.TimeMetaObjectHandler;
import java.io.Serializable;

/**
 * <p>Description: </p>
 * 1. 新增记录时, 不需要以下 4 个公共字段, 将根据注解自动生成;
 * 2. 更新记录时, 也不需设置 updateTime, 会自动更新时间 {@link TimeMetaObjectHandler}
 * 注意: 子类不能使用 builder 模式! 子类不能使用 builder 模式! 子类不能使用 builder 模式!
 *
 * @param <T> parameter
 * @param <M> parameter
 * @author dong4j
 * @version 1.2.3
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
     * Gets deleted *
     *
     * @return the deleted
     * @since 1.0.0
     */
    @Override
    public DeletedEnum getDeleted() {
        return this.deleted;
    }

    /**
     * Sets deleted *
     *
     * @param deleted deleted
     * @return the deleted
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setDeleted(DeletedEnum deleted) {
        this.deleted = deleted;
        return (M) this;
    }

}
