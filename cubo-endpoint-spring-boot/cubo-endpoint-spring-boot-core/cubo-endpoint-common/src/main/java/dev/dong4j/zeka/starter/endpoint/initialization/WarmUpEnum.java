package dev.dong4j.zeka.starter.endpoint.initialization;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预热请求的枚举类型定义
 *
 * 该枚举类用于定义应用预热过程中使用的标识符，
 * 实现 SerializeEnum 接口以支持序列化和反序列化操作。
 * 主要用于测试 JSON 序列化、参数验证等功能的正常工作。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:01
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum WarmUpEnum implements SerializeEnum<String> {
    /** Warm warm up enum */
    WARM("warm", ""),
    /** Me warm up enum */
    ME("me", ""),
    /** Up warm up enum */
    UP("up", "");

    /** Value */
    private final String value;
    /** Desc */
    private final String desc;
}
