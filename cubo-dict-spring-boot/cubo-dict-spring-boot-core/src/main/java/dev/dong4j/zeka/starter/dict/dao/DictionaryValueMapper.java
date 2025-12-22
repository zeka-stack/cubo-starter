package dev.dong4j.zeka.starter.dict.dao;

import org.apache.ibatis.annotations.Mapper;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.mybatis.base.BaseDao;

/**
 * 字典值数据访问接口
 * <p> 用于操作字典值相关的数据库操作, 继承自基础数据访问接口 BaseDao, 提供对 DictionaryValue 实体的增删改查等基础数据访问功能.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Mapper
public interface DictionaryValueMapper extends BaseDao<DictionaryValue> {

}
