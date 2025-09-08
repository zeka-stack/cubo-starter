package dev.dong4j.zeka.starter.mybatis.handler;

import java.util.Objects;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 元数据处理器责任链接口
 *
 * 该接口定义了元数据自动填充的责任链模式，用于在数据插入和更新时
 * 自动填充相关字段。通过责任链模式，可以灵活地组合多个处理器，
 * 每个处理器负责处理特定的字段填充逻辑。
 *
 * 主要功能：
 * 1. 定义插入时的字段自动填充接口
 * 2. 定义更新时的字段自动填充接口
 * 3. 提供通用的字段值设置方法
 * 4. 支持责任链模式的处理器组合
 *
 * 使用场景：
 * - 自动填充创建时间、更新时间
 * - 自动填充租户 ID、客户端 ID
 * - 自动填充操作人信息
 * - 其他需要自动填充的业务字段
 *
 * 实现类需要根据具体业务需求实现相应的填充逻辑。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.07 20:36
 * @since 1.0.0
 */
public interface MetaObjectChain {

    /**
     * 插入数据时的字段自动填充
     *
     * 该方法在数据插入时被调用，用于自动填充相关字段。
     * 实现类可以根据业务需求填充特定的字段，如创建时间、创建人等。
     *
     * 注意：该方法为默认方法，子类可以选择性重写
     *
     * @param metaObject MyBatis 元对象，包含实体的元数据信息
     * @param chain 责任链的下一个处理器，用于链式调用
     * @since 1.0.0
     */
    default void insertFill(MetaObject metaObject, MetaObjectChain chain) {
    }

    /**
     * 更新数据时的字段自动填充
     *
     * 该方法在数据更新时被调用，用于自动填充相关字段。
     * 实现类可以根据业务需求填充特定的字段，如更新时间、更新人等。
     *
     * 注意：该方法为默认方法，子类可以选择性重写
     *
     * @param metaObject MyBatis 元对象，包含实体的元数据信息
     * @param chain 责任链的下一个处理器，用于链式调用
     * @since 1.0.0
     */
    default void updateFill(MetaObject metaObject, MetaObjectChain chain) {
    }

    /**
     * 根据字段名设置字段值
     *
     * 该方法提供了一个通用的字段值设置功能，用于在元数据处理器中
     * 设置实体对象的字段值。
     *
     * 功能特点：
     * - 自动检查字段值是否为空
     * - 自动检查实体是否有对应的 setter 方法
     * - 只有在字段值不为空且存在 setter 方法时才设置值
     * - 避免了空值覆盖和反射异常
     *
     * @param fieldName 要设置的字段名称
     * @param fieldVal 要设置的字段值
     * @param metaObject MyBatis 元对象，用于访问实体的字段和方法
     * @since 1.0.0
     */
    default void setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        if (Objects.nonNull(fieldVal) && metaObject.hasSetter(fieldName)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }
}
