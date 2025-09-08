package dev.dong4j.zeka.starter.logsystem;

/**
 * 日志系统常量类
 *
 * 该类定义了日志系统中使用的所有常量，包括配置键、默认值、格式模式等。
 * 主要功能包括：
 * 1. 定义日志文件相关的常量
 * 2. 定义日志格式模式常量
 * 3. 定义配置键常量
 * 4. 提供系统属性名称常量
 *
 * 常量分类：
 * - 文件相关：文件名、路径、滚动策略等
 * - 格式相关：控制台格式、文件格式、时间格式等
 * - 配置相关：应用名称、位置显示、清理策略等
 * - 系统属性：用于Log4j2配置文件的属性名
 *
 * 使用场景：
 * - 日志系统配置处理
 * - Log4j2配置文件中的属性引用
 * - 系统属性的设置和获取
 * - 默认值的定义和引用
 *
 * 设计意图：
 * 通过统一的常量类，集中管理日志系统的所有常量，
 * 避免硬编码，提高代码的可维护性和可读性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.20 12:14
 */
public final class Constants {
    /** 默认日志文件名 */
    public static final String DEFAULT_FILE_NAME = "all.log";

    /** 废弃的文件名配置属性 */
    public static final String DEPRECATED_FILE_NAME_PROPERTY = "logging.file.name";

    /** 废弃的文件路径配置属性 */
    public static final String DEPRECATED_FILE_PATH_PROPERTY = "logging.file.path";

    /** 当前应用名称系统属性 */
    public static final String APP_NAME = "APP_NAME";

    /** 日志基础文件夹系统属性 */
    public static final String LOG_BASE_FOLDER = "LOG_BASE_FOLDER";

    /** 日志输出跳转信息系统属性，本地开发时默认开启 */
    public static final String SHOW_LOG_LOCATION = "SHOW_LOG_LOCATION";

    /** 位置信息未开启时的布局配置系统属性 */
    public static final String SHOW_LOG_LOCATION_LAYOUT = "SHOW_LOG_LOCATION_LAYOUT";

    /** 日志输出到控制台的格式系统属性 */
    public static final String CONSOLE_LOG_PATTERN = "CONSOLE_LOG_PATTERN";

    /** 日志输出到文件的格式系统属性 */
    public static final String FILE_LOG_PATTERN = "FILE_LOG_PATTERN";

    /** 输出的日志级别格式系统属性 */
    public static final String LOG_LEVEL_PATTERN = "LOG_LEVEL_PATTERN";

    /** 日志时间格式系统属性 */
    public static final String LOG_DATEFORMAT_PATTERN = "LOG_DATEFORMAT_PATTERN";

    /** 日志滚动文件名格式系统属性 */
    public static final String ROLLING_FILE_NAME_PATTERN = "ROLLING_FILE_NAME_PATTERN";

    /** 标记日志格式系统属性 */
    public static final String MARKER_PATTERN = "MARKER_PATTERN";

    /** 是否在应用启动时删除历史日志系统属性 */
    public static final String FILE_CLEAN_HISTORY_ON_START = "LOG_FILE_CLEAN_HISTORY_ON_START";

    /** 日志文件保留的最大历史天数系统属性 */
    public static final String FILE_MAX_HISTORY = "LOG_FILE_MAX_HISTORY";

    /** 日志文件最大容量系统属性 */
    public static final String FILE_MAX_SIZE = "LOG_FILE_MAX_SIZE";

    /** 日志文件总大小限制系统属性 */
    public static final String FILE_TOTAL_SIZE_CAP = "LOG_FILE_TOTAL_SIZE_CAP";
}
