package dev.dong4j.zeka.starter.mybatis.dao;

import dev.dong4j.zeka.starter.mybatis.base.BaseDao;
import dev.dong4j.zeka.starter.mybatis.entity.po.Test;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>Description: sku Dao 接口  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.10.11 21:33
 * @since 1.0.0
 */
@Mapper
public interface TestDao extends BaseDao<Test> {

}
