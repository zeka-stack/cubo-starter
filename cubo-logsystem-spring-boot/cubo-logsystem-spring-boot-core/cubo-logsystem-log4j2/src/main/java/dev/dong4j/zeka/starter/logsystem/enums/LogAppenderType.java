package dev.dong4j.zeka.starter.logsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日志输出位置类型枚举
 *
 * 该枚举定义了日志系统的不同输出位置类型，每种类型对应不同的Log4j2配置文件。
 * 主要功能包括：
 * 1. 定义日志输出的不同位置类型
 * 2. 为每种类型指定对应的Log4j2配置文件
 * 3. 支持不同环境下的日志输出策略
 * 4. 提供日志配置文件的统一管理
 *
 * 使用场景：
 * - 根据环境选择不同的日志输出方式
 * - 本地开发、生产环境、Docker环境的日志配置
 * - 日志系统的自动配置和切换
 *
 * 设计意图：
 * 通过枚举类型统一管理不同环境下的日志输出配置，
 * 简化日志系统的配置管理和环境切换。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.03 13:12
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum LogAppenderType {
    /** 控制台日志输出类型 - 用于本地开发环境 */
    CONSOLE("log4j2-console.xml"),

    /** 文件日志输出类型 - 用于生产环境 */
    FILE("log4j2-file.xml"),

    /** Docker日志输出类型 - 用于容器化环境 */
    DOCKER("log4j2-docker.xml");

    /** 对应的Log4j2配置文件名称 */
    private final String config;
}
