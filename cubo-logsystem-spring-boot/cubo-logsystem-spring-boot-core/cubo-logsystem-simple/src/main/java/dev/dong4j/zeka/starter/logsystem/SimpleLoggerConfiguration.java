package dev.dong4j.zeka.starter.logsystem;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.helpers.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * SimpleLogger配置类
 *
 * 该类保存SimpleLogger的配置值，这些值在运行时计算。
 * 支持从配置文件和系统属性中读取配置，提供灵活的日志配置能力。
 *
 * 主要功能包括：
 * 1. 管理SimpleLogger的各种配置参数
 * 2. 支持从配置文件和系统属性读取配置
 * 3. 提供默认配置值和配置解析
 * 4. 支持输出目标的选择和配置
 *
 * 配置项包括：
 * - 日志级别配置
 * - 日期时间显示配置
 * - 线程名显示配置
 * - 日志名显示配置
 * - 输出目标配置
 *
 * 使用场景：
 * - SimpleLogger的配置管理
 * - 日志格式的配置
 * - 输出目标的配置
 * - 日志级别的动态配置
 *
 * 设计意图：
 * 通过配置类提供SimpleLogger的灵活配置能力，支持多种配置来源，
 * 简化日志系统的配置和管理。
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:48
 * @since 1.0.0
 */
@SuppressWarnings("all")
class SimpleLoggerConfiguration {

    /** CONFIGURATION_FILE */
    private static final String CONFIGURATION_FILE = "simplelogger.properties";

    /** Default log level default */
    private static final int DEFAULT_LOG_LEVEL_DEFAULT = SimpleLogger.LOG_LEVEL_INFO;
    /** Default log level */
    int defaultLogLevel = DEFAULT_LOG_LEVEL_DEFAULT;

    /** SHOW_DATE_TIME_DEFAULT */
    private static final boolean SHOW_DATE_TIME_DEFAULT = false;
    /** Show date time */
    boolean showDateTime = SHOW_DATE_TIME_DEFAULT;

    /** DATE_TIME_FORMAT_STR_DEFAULT */
    private static final String DATE_TIME_FORMAT_STR_DEFAULT = "yyyy-MM-dd HH:mm:ss:SSS";

    /** Date formatter */
    final DateFormat dateFormatter;

    /** SHOW_THREAD_NAME_DEFAULT */
    private static final boolean SHOW_THREAD_NAME_DEFAULT = true;
    /** Show thread name */
    boolean showThreadName = SHOW_THREAD_NAME_DEFAULT;

    /** SHOW_LOG_NAME_DEFAULT */
    private final static boolean SHOW_LOG_NAME_DEFAULT = true;
    /** Show log name */
    boolean showLogName = SHOW_LOG_NAME_DEFAULT;

    /** SHOW_SHORT_LOG_NAME_DEFAULT */
    private static final boolean SHOW_SHORT_LOG_NAME_DEFAULT = false;
    /** Show short log name */
    boolean showShortLogName = SHOW_SHORT_LOG_NAME_DEFAULT;

    /** LEVEL_IN_BRACKETS_DEFAULT */
    private static final boolean LEVEL_IN_BRACKETS_DEFAULT = false;
    /** Level in brackets */
    boolean levelInBrackets = LEVEL_IN_BRACKETS_DEFAULT;

    /** LOG_FILE_DEFAULT */
    private static final String LOG_FILE_DEFAULT = "System.err";
    /** Log file */
    private String logFile = LOG_FILE_DEFAULT;
    /** Output choice */
    OutputChoice outputChoice = null;

    /** CACHE_OUTPUT_STREAM_DEFAULT */
    private static final boolean CACHE_OUTPUT_STREAM_DEFAULT = false;

    /** WARN_LEVELS_STRING_DEFAULT */
    private static final String WARN_LEVELS_STRING_DEFAULT = SimpleLogger.WARN;
    /** Warn level string */
    String warnLevelString = WARN_LEVELS_STRING_DEFAULT;

    /** Properties */
    private final Properties properties = new Properties();

    /**
     * 构造SimpleLogger配置对象
     *
     * 初始化SimpleLogger的配置，从配置文件和系统属性中读取配置值。
     * 配置包括日志级别、显示选项、输出目标等。
     *
     * 初始化流程：
     * 1. 加载配置文件属性
     * 2. 设置默认日志级别
     * 3. 配置显示选项（日志名、线程名、日期时间等）
     * 4. 设置日期时间格式
     * 5. 配置输出目标和缓存选项
     *
     * @since 1.0.0
     */
    SimpleLoggerConfiguration() {
        // 加载配置文件属性
        this.loadProperties();

        // 设置默认日志级别
        String defaultLogLevelString = this.getStringProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, null);
        if (defaultLogLevelString != null) {
            this.defaultLogLevel = stringToLevel(defaultLogLevelString);
        }

        // 配置显示选项
        this.showLogName = this.getBooleanProperty(SimpleLogger.SHOW_LOG_NAME_KEY, SimpleLoggerConfiguration.SHOW_LOG_NAME_DEFAULT);
        this.showShortLogName = this.getBooleanProperty(SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, SHOW_SHORT_LOG_NAME_DEFAULT);
        this.showDateTime = this.getBooleanProperty(SimpleLogger.SHOW_DATE_TIME_KEY, SHOW_DATE_TIME_DEFAULT);
        this.showThreadName = this.getBooleanProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, SHOW_THREAD_NAME_DEFAULT);

        // 设置日期时间格式
        String dateTimeFormatStr = this.getStringProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_STR_DEFAULT);
        this.dateFormatter = new SimpleDateFormat(dateTimeFormatStr);

        // 配置其他选项
        this.levelInBrackets = this.getBooleanProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, LEVEL_IN_BRACKETS_DEFAULT);
        this.warnLevelString = this.getStringProperty(SimpleLogger.WARN_LEVEL_STRING_KEY, WARN_LEVELS_STRING_DEFAULT);

        // 配置输出目标
        this.logFile = this.getStringProperty(SimpleLogger.LOG_FILE_KEY, this.logFile);

        // 配置输出流缓存选项
        boolean cacheOutputStream = this.getBooleanProperty(SimpleLogger.CACHE_OUTPUT_STREAM_STRING_KEY, CACHE_OUTPUT_STREAM_DEFAULT);
        this.outputChoice = computeOutputChoice(this.logFile, cacheOutputStream);
    }

    /**
     * 加载配置文件属性
     *
     * 从classpath中加载simplelogger.properties配置文件。
     * 如果配置文件不存在，则使用默认配置。
     *
     * @since 1.0.0
     */
    private void loadProperties() {
        // 尝试从classpath加载simplelogger.properties配置文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_FILE);
        if (inputStream == null) {
            // 如果直接加载失败，尝试通过URL加载
            URL url = this.getClass().getClassLoader().getResource(CONFIGURATION_FILE);
            if (url != null) {
                try {
                    inputStream = url.openStream();
                } catch (IOException ignored) {
                    // 忽略IO异常，使用默认配置
                }
            }
        }

        // 如果成功获取到输入流，加载属性
        if (null != inputStream) {
            try {
                this.properties.load(inputStream);
            } catch (java.io.IOException e) {
                // 忽略IO异常，使用默认配置
            } finally {
                try {
                    inputStream.close();
                } catch (java.io.IOException ignored) {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 获取字符串属性值
     *
     * 获取指定名称的字符串属性值，如果不存在则返回默认值。
     * 优先从系统属性中获取，然后从配置文件中获取。
     *
     * @param name         属性名称
     * @param defaultValue 默认值
     * @return 属性值，如果不存在则返回默认值
     * @since 1.0.0
     */
    String getStringProperty(String name, String defaultValue) {
        String prop = this.getStringProperty(name);
        return (prop == null) ? defaultValue : prop;
    }

    /**
     * 获取布尔属性值
     *
     * 获取指定名称的布尔属性值，如果不存在则返回默认值。
     * 字符串"true"（忽略大小写）被解析为true，其他值被解析为false。
     *
     * @param name         属性名称
     * @param defaultValue 默认值
     * @return 属性值，如果不存在则返回默认值
     * @since 1.0.0
     */
    private boolean getBooleanProperty(String name, boolean defaultValue) {
        String prop = this.getStringProperty(name);
        return (prop == null) ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    /**
     * 获取字符串属性值
     *
     * 获取指定名称的字符串属性值。
     * 优先从系统属性中获取，然后从配置文件中获取。
     *
     * @param name 属性名称
     * @return 属性值，如果不存在则返回null
     * @since 1.0.0
     */
    private String getStringProperty(String name) {
        String prop = null;
        try {
            // 优先从系统属性中获取
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            // 忽略安全异常
        }
        // 如果系统属性中不存在，从配置文件中获取
        return (prop == null) ? this.properties.getProperty(name) : prop;
    }

    /**
     * 将字符串转换为日志级别
     *
     * 将字符串形式的日志级别转换为对应的整数值。
     * 支持TRACE、DEBUG、INFO、WARN、ERROR、OFF级别。
     * 如果无法识别，默认返回INFO级别。
     *
     * @param levelStr 日志级别字符串
     * @return 日志级别对应的整数值
     * @since 1.0.0
     */
    static int stringToLevel(String levelStr) {
        if (SimpleLogger.TRACE.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_TRACE;
        } else if (SimpleLogger.DEBUG.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_DEBUG;
        } else if (SimpleLogger.INFO.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_INFO;
        } else if (SimpleLogger.WARN.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_WARN;
        } else if (SimpleLogger.ERROR.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_ERROR;
        } else if (SimpleLogger.OFF.equalsIgnoreCase(levelStr)) {
            return SimpleLogger.LOG_LEVEL_OFF;
        }
        // 默认返回INFO级别
        return SimpleLogger.LOG_LEVEL_INFO;
    }

    /**
     * 计算输出选择
     *
     * 根据日志文件配置和缓存选项计算输出选择对象。
     * 支持标准输出、标准错误输出和文件输出，以及相应的缓存模式。
     *
     * @param logFile           日志文件配置
     * @param cacheOutputStream 是否缓存输出流
     * @return 输出选择对象
     * @since 1.0.0
     */
    @NotNull
    @Contract("_, _ -> new")
    private static OutputChoice computeOutputChoice(String logFile, boolean cacheOutputStream) {
        if ("System.err".equalsIgnoreCase(logFile)) {
            // 标准错误输出
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_ERR);
            } else {
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
            }
        } else if ("System.out".equalsIgnoreCase(logFile)) {
            // 标准输出
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_OUT);
            } else {
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_OUT);
            }
        } else {
            // 文件输出
            try {
                FileOutputStream fos = new FileOutputStream(logFile);
                PrintStream printStream = new PrintStream(fos);
                return new OutputChoice(printStream);
            } catch (FileNotFoundException e) {
                // 如果文件创建失败，回退到标准错误输出
                Util.report("Could not open [" + logFile + "]. Defaulting to System.err", e);
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
            }
        }
    }

}
