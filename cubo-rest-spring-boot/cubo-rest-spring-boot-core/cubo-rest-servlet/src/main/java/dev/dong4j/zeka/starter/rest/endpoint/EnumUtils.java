package dev.dong4j.zeka.starter.rest.endpoint;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 枚举工具类，用于将枚举转换为指定格式的JSON结构
 * <p>
 * 该工具类主要用于处理实现了 {@link SerializeEnum} 接口的枚举类，
 * 将其转换为前端友好的 {@link EnumInfo} 对象，便于通过 RESTful API 返回给前端使用。
 * 转换后的结构包含枚举类的名称、类型以及所有枚举项的详细信息（名称、描述和值）。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.11.23 11:04
 * @see SerializeEnum
 * @see EnumInfo
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class EnumUtils {

    /**
     * 将枚举转换为指定格式的枚举信息对象
     * <p>
     * 该方法会提取枚举类的名称、类型，以及所有枚举项的详细信息，
     * 并将它们封装到 {@link EnumInfo} 对象中。
     * <p>
     * 枚举类型会被转换为下划线格式，例如 "DeletedEnum" 会被转换为 "deleted_enum"。
     * 枚举项的描述和值会通过 {@link SerializeEnum} 接口的方法获取。
     *
     * @param enumClass 实现了 SerializeEnum 接口的枚举类
     * @return 包含枚举信息的 EnumInfo 对象
     */
    @SuppressWarnings("unchecked")
    public static EnumInfo toEnumInfo(Class<? extends SerializeEnum> enumClass) {
        // 获取类注释（简化处理，实际可能需要通过反射或其他方式获取）
        String name = getEnumClassDescription(enumClass);

        // 类名转下划线格式
        String type = StringUtils.humpToUnderline(enumClass.getSimpleName());

        // 获取枚举值列表
        List<EnumInfo.EnumItem> items = new ArrayList<>();

        // 需要将 SerializeEnum 转换为 Enum
        Enum<? extends SerializeEnum>[] enumConstants = (Enum<? extends SerializeEnum>[]) enumClass.getEnumConstants();

        for (Enum<? extends SerializeEnum> constant : enumConstants) {
            String itemName = constant.name();
            String desc = getEnumDesc(constant);
            Object value = getEnumValue(constant);

            items.add(new EnumInfo.EnumItem(itemName, desc, value));
        }

        return new EnumInfo(name, type, items);
    }

    /**
     * 获取枚举类描述（从类注释中提取）
     * <p>
     * 当前实现是简化处理，直接返回枚举类的简单名称加上"枚举"字样。
     * 在实际应用中，可以通过解析 JavaDoc 或其他方式获取更准确的类描述。
     *
     * @param enumClass 枚举类
     * @return 类描述
     */
    private static String getEnumClassDescription(Class<?> enumClass) {
        // 简化处理，实际可能需要解析JavaDoc
        return enumClass.getSimpleName() + "枚举";
    }

    /**
     * 获取枚举项的描述
     * <p>
     * 如果枚举常量实现了 {@link SerializeEnum} 接口，则调用其 getDesc() 方法获取描述。
     * 如果描述为 null，则返回空字符串。
     *
     * @param enumConstant 枚举常量
     * @return 描述信息，如果没有描述则返回空字符串
     */
    private static String getEnumDesc(Enum<?> enumConstant) {
        // 检查是否实现了 SerializeEnum 接口
        if (enumConstant instanceof SerializeEnum<?> serializeEnum) {
            // 直接调用接口方法获取描述
            String desc = serializeEnum.getDesc();
            return desc != null ? desc : "";
        }
        return "";
    }

    /**
     * 获取枚举项的值
     * <p>
     * 如果枚举常量实现了 {@link SerializeEnum} 接口，则调用其 getValue() 方法获取值。
     * 如果没有实现该接口，则返回 null。
     *
     * @param enumConstant 枚举常量
     * @return 枚举项的值，如果没有实现 SerializeEnum 接口则返回 null
     */
    private static Object getEnumValue(Enum<?> enumConstant) {
        // 检查是否实现了 SerializeEnum 接口
        if (enumConstant instanceof SerializeEnum<?> serializeEnum) {
            // 直接调用接口方法获取值
            return serializeEnum.getValue();
        }
        return null;
    }

}
