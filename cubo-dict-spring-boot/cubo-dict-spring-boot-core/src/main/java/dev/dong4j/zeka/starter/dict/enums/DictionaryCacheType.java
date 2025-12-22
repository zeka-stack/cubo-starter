package dev.dong4j.zeka.starter.dict.enums;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典缓存类型枚举
 * <p> 定义系统中支持的字典缓存类型, 用于标识不同的缓存策略和行为
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DictionaryCacheType implements SerializeEnum<String> {

    /** 内存缓存 */
    MEMORY("memory", "内存缓存"),

    /** 无操作缓存 (禁用缓存) */
    NONE("none", "无操作缓存");

    /** 缓存类型对应的值 */
    private final String value;

    /**
     * 描述信息
     * <p> 用于表示该字典缓存类型的中文描述
     */
    private final String desc;
}
