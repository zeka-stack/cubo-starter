package dev.dong4j.zeka.starter.launcher.spi;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum implements SerializeEnum<Integer> {
    A(1, "1");  // 重复的 value

    private final Integer value;
    /** 枚举描述 */
    private final String desc;
}
