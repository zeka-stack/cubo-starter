package dev.dong4j.zeka.starter.dict.entity.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import dev.dong4j.zeka.kernel.common.mapstruct.ExtendConverter;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryValueDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryValueForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;

/**
 * 数据字典值转换器接口
 * <p> 用于将数据字典值表单对象转换为数据字典值 DTO 对象, 并提供相应的转换方法
 * <p> 该接口实现了 ExtendConverter 接口, 定义了从 DictionaryValueForm 到 DictionaryValueDTO 的转换逻辑
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Mapper
public interface DictionaryValueConverter extends ExtendConverter<DictionaryValueForm, DictionaryValueDTO, DictionaryValue> {
    /** 字典值表实体转换器的实例 */
    DictionaryValueConverter INSTANCE = Mappers.getMapper(DictionaryValueConverter.class);
}
