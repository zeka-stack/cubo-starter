package dev.dong4j.zeka.starter.logsystem.entity;

import lombok.Data;

/**
 * 日志输出格式配置实体类
 *
 * 该类封装了日志输出的各种格式配置，包括控制台和文件的输出格式。
 * 主要功能包括：
 * 1. 定义日志级别、时间、标记等格式模式
 * 2. 支持控制台和文件的不同输出格式
 * 3. 提供日志文件滚动和压缩的格式配置
 * 4. 支持彩色输出和位置信息显示
 *
 * 使用场景：
 * - 日志系统格式配置的绑定
 * - 多环境下的日志格式定制
 * - 日志输出格式的统一管理
 * - 开发和生产环境的格式差异化配置
 *
 * 设计意图：
 * 通过统一的格式配置实体类，简化日志输出格式的管理和定制，
 * 提供灵活的日志格式配置能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.26 13:51
 * @since 1.0.0
 */
@Data
@SuppressWarnings("checkstyle:LineLength")
public class Pattern {
    /** 日志级别输出格式，默认为5位右对齐的级别名称 */
    private String level = "%5p";

    /** 标记日志格式，默认为消息加换行 */
    private String marker = "%m%n";

    /** 日志时间输出格式，默认为年-月-日 时:分:秒.毫秒 */
    private String dateformat = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 日志滚动文件名格式，默认为年月日-时.序号.log.gz */
    private String rollingFileName = "%d{yyyyMMdd-HH}.%i.log.gz";

    /** 输出到文件的格式，此配置只对 log4j2-file.xml 有效 */
    private String file = "%d{yyyy-MM-dd HH:mm:ss.SSS} %5p - [%15.15t] %c{1.} :: %m%n%xwEx";

    /** 输出到控制台的格式，此配置只对 log4j2-console.xml 有效，支持彩色输出和位置信息 */
    private String console = "%clr{%d{yyyy-MM-dd HH:mm:ss.SSS}}{faint} %clr{[%5p]} %clr{-}{faint} %clr{[%15.15t]}{faint} %location{.} %clr{::}{faint} %m%n%xwEx";
}
