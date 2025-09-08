package dev.dong4j.zeka.starter.rest.converter;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.enums.serialize.EntityEnumDeserializer;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 全局枚举转换器工厂
 *
 * 该类用于在 Spring MVC 中处理枚举类型的参数转换。
 * 由于前端传入的都是 String 类型，该工厂将 String 类型转换为对应的枚举类型。
 *
 * 支持的转换值包括：
 * 1. 枚举的 value 值（优先级最高）
 * 2. 枚举的 name 名称
 * 3. 枚举的序列号（ordinal）
 *
 * 与 JSON 格式的枚举转换优先级保持一致，使用 {@link EntityEnumDeserializer} 的策略。
 *
 * 使用方式：在 {@link dev.dong4j.zeka.starter.rest.autoconfigure.servlet.ServletWebAutoConfiguration#addFormatters} 中注册。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.03 12:59
 * @since 1.0.0
 */
public class GlobalEnumConverterFactory implements ConverterFactory<String, SerializeEnum<?>> {

    /** 缓存枚举转换器，避免重复创建 */
    private static final Map<Class<?>, Converter<String, ?>> CONVERTER_MAP = Maps.newConcurrentMap();

    /**
     * 获取枚举转换器
     *
     * 为指定的枚举类型返回一个从 String 到该枚举类型的转换器。
     * 使用缓存机制避免重复创建转换器，提高性能。
     *
     * @param <T> 需要被转换的枚举类型，必须实现 SerializeEnum 接口
     * @param targetType 目标枚举类型
     * @return 对应的转换器实例
     * @since 1.0.0
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends SerializeEnum<?>> Converter<String, T> getConverter(@NotNull Class<T> targetType) {
        // 从缓存中获取转换器
        Converter<String, T> result = (Converter<String, T>) CONVERTER_MAP.get(targetType);
        if (result == null) {
            // 如果缓存中不存在，则创建新的转换器并缓存
            result = new GlobalEnumConverter<>(targetType);
            CONVERTER_MAP.put(targetType, result);
        }
        return result;
    }

    /**
     * 全局枚举转换器实现类
     *
     * 该类实现了具体的枚举转换逻辑，将 String 类型的参数转换为指定的枚举类型。
     * 转换优先级：value > name > ordinal，与 JSON 格式的枚举转换保持一致。
     *
     * @param <T> 枚举类型参数，必须实现 SerializeEnum 接口
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.01.03 13:01
     * @since 1.0.0
     */
    private static class GlobalEnumConverter<T extends SerializeEnum<?>> implements Converter<String, T> {
        /** 枚举的 getValue() 值与枚举实例的映射关系 */
        private final Map<String, T> enumMap = Maps.newHashMap();
        /** 枚举类型 */
        private final Class<? extends SerializeEnum<?>> enumType;

        /**
         * 构造方法，建立枚举值与枚举实例的映射关系
         *
         * 遍历指定枚举类型的所有定义枚举，将其 getValue() 值与枚举实例建立映射关系。
         * 由于前端传过来的都是 String 类型的参数，因此将 getValue() 转为 String 进行存储。
         *
         * @param enumType 枚举类型
         * @since 1.0.0
         */
        GlobalEnumConverter(@NotNull Class<T> enumType) {
            this.enumType = enumType;
            // 获取当前枚举类型的所有已定义的枚举实例
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                // 将 getValue() 返回值转为 String，建立映射关系
                this.enumMap.put(String.valueOf(e.getValue()), e);
            }
        }

        /**
         * 执行枚举转换逻辑
         *
         * 转换优先级：
         * 1. 优先匹配枚举的 getValue() 值
         * 2. 其次匹配枚举的 name() 名称
         * 3. 最后匹配枚举的 ordinal() 序列号
         *
         * 该优先级与 JSON 格式的枚举转换保持一致，参考 {@link EntityEnumDeserializer}。
         *
         * @param source 要转换的字符串参数
         * @return 转换后的枚举实例
         * @since 1.0.0
         */
        @Override
        @SuppressWarnings("unchecked")
        public T convert(@NotNull String source) {
            // 优先从缓存中获取对应的枚举实例
            T result = this.enumMap.get(source);

            if (result == null) {
                // 如果缓存中不存在，则通过 name 或 ordinal 进行匹配
                result = SerializeEnum.getEnumByNameOrOrder((Class<? extends Enum<?>>) this.enumType, source);
                // 将结果缓存起来，提高后续转换效率
                this.enumMap.put(source, result);
            }
            return result;
        }
    }
}
