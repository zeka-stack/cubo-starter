package dev.dong4j.zeka.starter.dict.dao;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.mybatis.base.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p> 字典值表 Dao 接口  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Mapper
public interface DictionaryValueMapper extends

    BaseDao<DictionaryValue> {

}
