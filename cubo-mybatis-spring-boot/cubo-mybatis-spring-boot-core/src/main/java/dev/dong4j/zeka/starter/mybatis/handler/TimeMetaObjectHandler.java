package dev.dong4j.zeka.starter.mybatis.handler;

import dev.dong4j.zeka.starter.mybatis.base.BasePO;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

/**
 * <p>Description: 处理新增和更新的基础数据填充,配合 BaseEntity 和 MyBatisPlusConfig 使用 </p>
 * {@link BasePO}
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:16
 * @since 1.0.0
 */
@Slf4j
public class TimeMetaObjectHandler implements MetaObjectChain {

    /**
     * 新增数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void insertFill(MetaObject metaObject, MetaObjectChain chain) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    /**
     * 更新数据执行
     *
     * @param metaObject the meta object
     * @since 1.0.0
     */
    @Override
    public void updateFill(MetaObject metaObject, MetaObjectChain chain) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

}
