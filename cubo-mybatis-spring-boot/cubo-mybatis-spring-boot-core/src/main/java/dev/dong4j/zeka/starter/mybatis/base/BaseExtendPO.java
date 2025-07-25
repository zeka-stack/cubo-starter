package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description:  </p>
 *
 * @param <T> parameter
 * @param <M> parameter
 * @author dong4j
 * @version 1.3.0
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

    /**
     * Gets create time *
     *
     * @return the create time
     * @since 1.0.0
     */
    @Override
    public Date getCreateTime() {
        return this.createTime;
    }

    /**
     * Sets create time *
     *
     * @param createTime create time
     * @return the create time
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (M) this;
    }

    /**
     * Gets update time *
     *
     * @return the update time
     * @since 1.0.0
     */
    @Override
    public Date getUpdateTime() {
        return this.updateTime;
    }

    /**
     * Sets update time *
     *
     * @param updateTime update time
     * @return the update time
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public M setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (M) this;
    }
}
