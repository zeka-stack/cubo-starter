package dev.dong4j.zeka.starter.mybatis.dict;

import org.apache.ibatis.reflection.MetaObject;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 13:40
 * @since 2.0.0
 */
public interface IDataBind {

    /**
     * 使用反射对象MetaObject，操作目标对象Object
     *
     * @param field      field
     * @param o          原始对象
     * @param metaObject 原始对象的mybatis反射对象
     * @since 2.0.0
     */
    void setMetaObject(FieldBind field, Object o, MetaObject metaObject);
}
