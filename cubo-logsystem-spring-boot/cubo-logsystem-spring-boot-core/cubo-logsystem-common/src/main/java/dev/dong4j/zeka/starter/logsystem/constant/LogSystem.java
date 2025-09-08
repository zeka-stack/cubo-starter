package dev.dong4j.zeka.starter.logsystem.constant;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.util.StringUtils;

/**
 * 日志系统常量类
 *
 * 该类定义了日志系统中使用的所有常量，包括模块名称、默认路径、标记类型等。
 * 主要功能包括：
 * 1. 提供日志系统的核心常量定义
 * 2. 定义日志标记类型枚举
 * 3. 提供路径判断等工具方法
 * 4. 统一管理日志系统相关的常量值
 *
 * 使用场景：
 * - 日志系统配置中的常量引用
 * - 日志标记类型的定义和使用
 * - 路径相关的工具方法调用
 * - 模块名称和标识的统一管理
 *
 * 设计意图：
 * 通过集中管理常量，避免魔法数字和字符串的散落，
 * 提高代码的可维护性和可读性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:20
 * @since 1.0.0
 */
public final class LogSystem {

    /** 日志默认状态标识 */
    public static final String LOG_NORMAL_TYPE = "1";

    /** 模块名称 */
    public static final String MODULE_NAME = "cubo-logsystem-spring-boot-starter";

    /** 默认日志存储位置 */
    public static final String DEFAULT_LOGGING_LOCATION = ConfigDefaultValue.DEFAULT_LOGGING_LOCATION;

    /** 属性标记名称 */
    public static final String MARKER_PROPERTIES = Marker.PROCESSOR.name().toLowerCase();

    /** 处理器标记名称 */
    public static final String MARKER_PROCESSOR = Marker.PROPERTIES.name().toLowerCase();

    /**
     * 日志标记类型枚举
     *
     * 定义了日志系统中使用的不同标记类型，用于区分日志的来源和用途。
     * 这些标记主要用于日志处理器的识别和分类处理。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.08 13:37
     * @since 1.0.0
     */
    public enum Marker {
        /** Banner标记 - 用于启动横幅相关的日志 */
        BANNER,
        /** 属性标记 - 用于配置属性相关的日志 */
        PROPERTIES,
        /** 处理器标记 - 用于日志处理器相关的日志 */
        PROCESSOR
    }

    /**
     * 判断是否为相对路径
     *
     * 检查给定的日志路径是否为相对路径。相对路径的判断标准：
     * 1. 路径以"./"开头
     * 2. 路径不以"/"开头（非绝对路径）
     *
     * 使用场景：
     * - 日志文件路径的验证和处理
     * - 路径类型的判断和转换
     * - 配置文件中的路径解析
     *
     * @param logPath 待检查的日志路径
     * @return true-相对路径，false-绝对路径或空路径
     */
    public static boolean isRelativePath(String logPath) {
        return StringUtils.isNotBlank(logPath) && (logPath.startsWith("./") || !logPath.startsWith("/"));
    }
}
