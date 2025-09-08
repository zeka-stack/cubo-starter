package dev.dong4j.zeka.starter.rest.converter;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.util.DateUtils;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

/**
 * 字符串到日期转换器
 *
 * 该转换器专门用于在 Spring MVC Controller 层将字符串类型的参数转换为 Date 类型。
 * 主要应用于基础字段转换（非实体类型），实体类的日期转换由 Jackson 处理。
 *
 * 功能特点：
 * 1. 支持多种日期格式的自动识别和转换
 * 2. 可通过系统属性配置自定义日期格式
 * 3. 处理空字符串时返回 null，避免转换异常
 * 4. 与 Jackson 日期配置保持一致
 *
 * 使用场景：
 * - URL 路径参数中的日期字符串转换
 * - 查询参数中的日期字符串转换
 * - 表单数据中的日期字符串转换
 *
 * 配置方式：
 * 可通过 zeka-stack.json.date-format 系统属性自定义日期格式。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.24 02:06
 * @since 1.0.0
 */
public class StringToDateConverter implements Converter<String, Date> {

    /** 默认日期时间格式模式 */
    public static final String PATTERN = DateUtils.PATTERN_DATETIME;

    /**
     * 将字符串转换为日期对象
     *
     * 该方法是 Spring Converter 接口的核心实现，负责将前端传递的字符串
     * 转换为 Java Date 对象。支持自定义日期格式配置。
     *
     * 转换逻辑：
     * 1. 检查源字符串是否为空，空字符串返回 null
     * 2. 优先使用系统属性中配置的日期格式
     * 3. 如果未配置则使用默认的日期时间格式
     * 4. 使用 DateUtils 工具类进行实际的字符串解析
     *
     * @param source 待转换的字符串，不能为 null
     * @return 转换后的 Date 对象，输入为空字符串时返回 null
     * @throws RuntimeException 当字符串格式不符合预期时抛出
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
