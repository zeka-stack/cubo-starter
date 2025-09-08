package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import dev.dong4j.zeka.kernel.common.base.IBaseEntity;
import java.io.Serializable;
import lombok.Builder;

/**
 * 基础持久化对象抽象类
 *
 * 该抽象类为所有数据库实体类提供基础功能，继承自 MyBatis Plus 的 Model 类，
 * 支持 Active Record 模式的数据库操作。
 *
 * 主要功能：
 * 1. 提供统一的主键 ID 字段定义和访问方法
 * 2. 实现 Active Record 模式的基础方法
 * 3. 支持泛型化的主键类型
 * 4. 提供链式调用的 setter 方法
 *
 * Active Record 模式要求：
 * 1. 必须重写 pkVal() 方法返回主键值
 * 2. 必须存在对应的 Dao 接口
 *
 * 使用注意事项：
 * - 子类不能使用 @Builder 注解，会导致字段丢失
 * - 建议使用链式调用的 setter 方法
 * - 主键字段会自动映射到数据库的 ID 字段
 *
 * @param <T> 主键类型，必须实现 Serializable 接口
 * @param <M> 模型类型，继承自 Model
 * @author dong4j
 * @version 1.0.0
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
     * 获取主键值
     *
     * 该方法是 Active Record 模式的核心方法之一，用于返回当前实体的主键值。
     * MyBatis Plus 通过该方法获取主键值来执行相关的数据库操作。
     *
     * Active Record 模式的使用要求：
     * 1. 必须重写此方法返回正确的主键值
     * 2. 必须存在对应的 Dao 接口（如 UserDao）
     * 3. 主键值不能为 null（执行数据库操作时）
     *
     * @return T 主键值
     * @since 1.0.0
     */
    @Override
    public T pkVal() {
        return this.getId();
    }

    /**
     * 获取主键 ID
     *
     * 该方法用于获取实体的主键 ID 值，是对主键字段的标准访问方法。
     * 所有继承该类的实体都可以通过此方法获取主键值。
     *
     * @return T 主键 ID 值
     * @since 1.0.0
     */
    @Override
    public T getId() {
        return this.id;
    }

    /**
     * 设置主键 ID
     *
     * 该方法用于设置实体的主键 ID 值，支持链式调用。
     * 使用 @Builder 注解确保在对象转换时主键值不会丢失。
     *
     * 注意事项：
     * - 返回当前实例以支持链式调用
     * - 在使用 MapStruct 等转换工具时，该注解确保字段正确映射
     * - 子类继承时会自动获得该功能
     *
     * @param id 要设置的主键 ID 值
     * @return M 当前实例，支持链式调用
     * @since 1.0.0
     */
    @Builder
    @SuppressWarnings("unchecked")
    public M setId(T id) {
        this.id = id;
        return (M) this;
    }
}
