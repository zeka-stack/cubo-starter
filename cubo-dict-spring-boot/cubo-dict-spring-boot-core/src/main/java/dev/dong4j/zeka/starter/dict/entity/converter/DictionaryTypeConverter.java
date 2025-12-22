package dev.dong4j.zeka.starter.dict.entity.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import dev.dong4j.zeka.kernel.common.mapstruct.ExtendConverter;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryTypeDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryTypeForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;

/**
 * 字典类型转换器接口
 * <p>用于将字典类型的表单对象 (DictionaryTypeForm) 转换为数据传输对象(DictionaryTypeDTO),
 * 并支持字典类型的实体对象 (DictionaryType) 之间的转换. 该接口继承自 ExtendConverter 接口,
 * 提供了统一的转换规则和方法.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Mapper
public interface DictionaryTypeConverter extends ExtendConverter<DictionaryTypeForm, DictionaryTypeDTO, DictionaryType> {
    /** 字典类型表实体转换器的实例 */
    DictionaryTypeConverter INSTANCE = Mappers.getMapper(DictionaryTypeConverter.class);
}
