package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * P6spy 配置属性类
 *
 * 该类定义了 P6spy SQL 监控工具的默认配置属性，支持通过配置文件进行自定义。
 * 配置前缀为 "spring.datasource.p6spy"，主要配置项包括：
 *
 * 1. 模块配置：定义要加载的 P6spy 模块列表
 * 2. 日志配置：设置日志消息格式和输出方式
 * 3. 驱动配置：指定要代理的数据库驱动
 * 4. 过滤配置：设置要排除的日志类别
 * 5. 慢查询配置：配置慢查询检测的时间阈值
 * 6. 格式配置：设置日期和时间戳的显示格式
 *
 * 这些配置项都有合理的默认值，适用于大多数使用场景。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.15 10:03
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = P6spyProperties.P6SPY_CONFIG_PREFIX)
public class P6spyProperties {

    /** P6SPY_CONFIG_PREFIX */
    public static final String P6SPY_CONFIG_PREFIX = "spring.datasource.p6spy";
    /** Enabled */
    private boolean enabled = false;
    /** Modulelist */
    private String modulelist = "com.baomidou.mybatisplus.extension.p6spy.MybatisPlusLogFactory,com.p6spy.engine.outage.P6OutageFactory";
    /** Log message format */
    private String logMessageFormat = "com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger";
    /** Appender */
    private String appender = "dev.dong4j.zeka.starter.mybatis.logger.ZekaP6spySlf4jLogger";
    /** Excludecategories */
    private String excludecategories = "info, warn, error, result, batc, resultset, statement";
    /** 设置 p6spy driver 代理 */
    private String deregisterdrivers = "true";
    /** Autoflush */
    private String autoflush = "false";
    /** Driverlist */
    private String driverlist = "com.mysql.cj.jdbc.Driver";
    /** Dateformat */
    private String dateformat = "yyyy-MM-dd HH:mm:ss";
    /** Database dialect date format */
    private String databaseDialectDateFormat = "yyyy-MM-dd HH:mm:ss";
    /** Database dialect timestamp format */
    private String databaseDialectTimestampFormat = "yyyy-MM-dd HH:mm:ss";
    /** 是否开启慢查询 */
    private String outagedetection = "true";
    /** 慢查询时间, 单位秒 */
    private String outagedetectioninterval = "1";
}
