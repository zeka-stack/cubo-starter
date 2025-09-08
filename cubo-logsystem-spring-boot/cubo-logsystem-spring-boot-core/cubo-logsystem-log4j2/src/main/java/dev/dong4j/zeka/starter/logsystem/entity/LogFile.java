package dev.dong4j.zeka.starter.logsystem.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

/**
 * 日志文件配置实体类
 *
 * 该类封装了日志文件相关的所有配置信息，包括文件路径、名称、大小限制等。
 * 主要功能包括：
 * 1. 定义日志文件的基本属性（名称、路径）
 * 2. 配置日志文件的存储策略（大小、保留时间）
 * 3. 支持日志文件的清理和轮转配置
 * 4. 提供日志文件配置的默认值
 *
 * 使用场景：
 * - 日志系统配置文件绑定
 * - 日志文件路径和名称的动态配置
 * - 日志存储策略的个性化设置
 * - 多环境下的日志配置管理
 *
 * 设计意图：
 * 通过统一的配置实体类，简化日志文件配置的管理和使用，
 * 提供灵活的日志存储策略配置能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.26 13:51
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class LogFile {
    /** 日志文件名，此配置只对 log4j2-file.xml 有效 */
    private String name;

    /** 日志保存路径，此配置只对 log4j2-file.xml 有效 */
    private String path;

    /** 启动应用时是否清理历史日志，此配置只对 log4j2-file.xml 有效 */
    private boolean cleanHistoryOnStart = false;

    /** 历史日志最大保留时间（天），此配置只对 log4j2-file.xml 有效 */
    private Integer maxHistory = 90;

    /** 日志文件最大容量，此配置只对 log4j2-file.xml 有效 */
    private String maxSize = "50MB";

    /** 日志文件总数量限制，此配置只对 log4j2-file.xml 有效 */
    private Integer totalSizeCap = 50;

    /**
     * 带参数的构造函数
     *
     * 创建指定名称和路径的日志文件配置实例。
     *
     * @param name 日志文件名
     * @param path 日志文件路径
     * @since 1.0.0
     */
    @Contract(pure = true)
    public LogFile(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
