package dev.dong4j.zeka.starter.dict.entity.converter;

import dev.dong4j.zeka.kernel.common.mapstruct.ExtendConverter;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryValueDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryValueForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * <p> 字典值表 实体转换器 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Mapper
public interface DictionaryValueConverter extends ExtendConverter<DictionaryValueForm, DictionaryValueDTO, DictionaryValue> {
    /** INSTANCE */
    DictionaryValueConverter INSTANCE = Mappers.getMapper(DictionaryValueConverter.class);
}
