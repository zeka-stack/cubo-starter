package dev.dong4j.zeka.starter.dict.dao;

import org.apache.ibatis.annotations.Mapper;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.mybatis.base.BaseDao;

/**
 * 字典类型数据访问接口
 * <p> 提供字典类型数据的数据库操作功能, 继承自基础数据访问接口, 用于实现字典类型数据的增删改查等操作
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Mapper
public interface DictionaryTypeMapper extends BaseDao<DictionaryType> {

}
