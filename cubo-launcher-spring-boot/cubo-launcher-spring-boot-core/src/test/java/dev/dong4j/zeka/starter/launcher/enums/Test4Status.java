package dev.dong4j.zeka.starter.launcher.enums;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.28 14:35
 * @since 1.5.0
 */
@Getter
@AllArgsConstructor
public enum Test4Status implements SerializeEnum<String> {

    XXXX("1", "xxxx"),
    YYYY("2", "yyyy"),
    ZZZZ("3", "zzzz"),
    OOOO("3", "oooo");

    private final String value;
    /** Desc */
    private final String desc;
}

