package dev.dong4j.zeka.starter.rest.converter;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.util.DateUtils;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

/**
 * <p>Description: String 转 Date 的转化器, 适用于 controller 基础字段转换 (非实体), 实体由 Jackson 进行转换 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.24 02:06
 * @since 1.0.0
 */
public class StringToDateConverter implements Converter<String, Date> {

    /** PATTERN */
    public static final String PATTERN = DateUtils.PATTERN_DATETIME;

    /**
     * Convert date
     *
     * @param source source
     * @return the date
     * @since 1.0.0
     */
    @Override
    public Date convert(@NotNull String source) {
        if (source.isEmpty()) {
            return null;
        }
        return DateUtils.parse(source, System.getProperty(ConfigKey.JSON_DATE_FORMAT, PATTERN));
    }
}
