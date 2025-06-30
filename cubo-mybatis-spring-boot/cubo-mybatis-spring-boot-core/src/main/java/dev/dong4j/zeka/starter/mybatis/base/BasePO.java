package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import dev.dong4j.zeka.kernel.common.base.IBaseEntity;
import dev.dong4j.zeka.starter.mybatis.handler.TimeMetaObjectHandler;
import java.io.Serializable;
import lombok.Builder;

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
public abstract class BasePO<T extends Serializable, M extends Model<M>> extends Model<M> implements IBaseEntity<T> {

    /** serialVersionUID */
    private static final long serialVersionUID = -3685429878576720045L;

    /** Id */
    @TableId(value = ID)
    private T id;

    /**
     * Active Record 模式需要满足 2 个要求:
     * 1. 重写 pkVal();
     * 2. 存在 XxxDao;
     *
     * @return the serializable
     * @since 1.0.0
     */
    @Override
    public T pkVal() {
        return this.getId();
    }

    /**
     * Pk val serializable
     *
     * @return the serializable
     * @since 1.0.0
     */
    @Override
    public T getId() {
        return this.id;
    }

    /**
     * 必须使用 @Builder 注解, 不然在使用 wrapper 转换时 id 会丢失
     *
     * @param id id
     * @return the id
     * @since 1.0.0
     */
    @Builder
    @SuppressWarnings("unchecked")
    public M setId(T id) {
        this.id = id;
        return (M) this;
    }
}
