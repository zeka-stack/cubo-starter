package dev.dong4j.zeka.starter.mybatis.dict;

import org.apache.ibatis.reflection.MetaObject;

/**
 * 默认空实现
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 16:28
 * @since 2024.2.0
 */
public class DefaultDataBind implements IDataBind {


    /**
     * Set meta object
     *
     * @param field      field
     * @param fieldValue field value
     * @param metaObject meta object
     * @since 2024.2.0
     */
    @Override
    public void setMetaObject(FieldBind field, Object fieldValue, MetaObject metaObject) {

    }
}
