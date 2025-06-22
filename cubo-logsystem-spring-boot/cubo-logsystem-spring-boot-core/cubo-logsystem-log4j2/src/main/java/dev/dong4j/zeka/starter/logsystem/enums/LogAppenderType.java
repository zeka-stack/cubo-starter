package dev.dong4j.zeka.starter.logsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: 日志输出位置 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.03 13:12
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum LogAppenderType {
    /** Console log appender type */
    CONSOLE("log4j2-console.xml"),
    /** File log appender type */
    FILE("log4j2-file.xml");

    /** Config */
    private final String config;
}
