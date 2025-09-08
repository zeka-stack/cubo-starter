package dev.dong4j.zeka.starter.mybatis.spi;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.NetUtils;
import dev.dong4j.zeka.processor.annotation.AutoService;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Druid 数据源启动初始化器
 *
 * 该类通过 SPI 机制自动加载 Druid 数据源的默认配置，在应用启动时
 * 为 Druid 数据源提供合理的默认配置值。
 *
 * 主要功能：
 * 1. 配置 Druid 数据源的基本参数（连接池大小、超时时间等）
 * 2. 配置 Druid 监控功能（SQL 监控、Web 监控等）
 * 3. 配置 Druid 安全参数（防火墙、访问控制等）
 * 4. 提供生产环境友好的默认配置
 *
 * 配置项包括：
 * - 连接池配置：初始连接数、最大连接数、超时时间等
 * - 监控配置：SQL 统计、慢查询记录、Web 监控界面等
 * - 安全配置：访问白名单、用户认证、防火墙规则等
 *
 * 注意：该配置仅在非 JUnit 测试环境下生效
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:19
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class DruidLauncherInitiation implements LauncherInitiation {
    /**
     * 设置默认属性
     *
     * 该方法在应用启动时被调用，用于设置 Druid 数据源的默认配置。
     * 如果是 JUnit 测试环境，则不加载任何配置以避免影响测试。
     *
     * 配置的主要参数：
     * 1. 数据库驱动和连接池类型
     * 2. 连接池大小和超时配置
     * 3. 连接有效性检查配置
     * 4. SQL 监控和统计配置
     * 5. Web 监控界面配置
     * 6. 安全访问控制配置
     *
     * @param env Spring 环境配置对象
     * @param appName 应用名称
     * @param isLocalLaunch 是否为本地启动
     * @return Map<String, Object> 默认配置属性映射
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {
        if (ConfigKit.isStartedByJunit()) {
            return ChainMap.build(0);
        }
        return ChainMap.build(36)
            .put(ConfigKey.DruidConfigKey.DRIVER_CLASS, "com.mysql.cj.jdbc.Driver")
            .put(ConfigKey.DruidConfigKey.TYPE, "com.alibaba.druid.pool.DruidDataSource")
            .put(ConfigKey.DruidConfigKey.DB_TYPE, "mysql")
            .put(ConfigKey.DruidConfigKey.INITIALSIZE, 5)
            .put(ConfigKey.DruidConfigKey.MINIDLE, 5)
            .put(ConfigKey.DruidConfigKey.MAXACTIVE, 20)
            // 配置获取连接等待超时的时间
            .put(ConfigKey.DruidConfigKey.MAXWAIT, 60000)
            // 配置间隔多久才进行一次检测,检测需要关闭的空闲连接,单位是毫秒
            .put(ConfigKey.DruidConfigKey.TIMEBETWEENEVICTIONRUNSMILLIS, 60000)
            // 配置一个连接在池中最小生存的时间,单位是毫秒
            .put(ConfigKey.DruidConfigKey.MINEVICTABLEIDLETIMEMILLIS, 300000)
            // todo-dong4j : (2020.10.15 11:53) [待官方修复后开启连接可用性检查]
            .put(ConfigKey.DruidConfigKey.TESTWHILEIDLE, ConfigDefaultValue.FALSE)
            .put(ConfigKey.DruidConfigKey.TESTONBORROW, ConfigDefaultValue.FALSE)
            .put(ConfigKey.DruidConfigKey.TESTONRETURN, ConfigDefaultValue.FALSE)
            // 打开PSCache,并且指定每个连接上PSCache的大小
            .put(ConfigKey.DruidConfigKey.POOLPREPAREDSTATEMENTS, ConfigDefaultValue.TRUE)
            .put(ConfigKey.DruidConfigKey.MAXPOOLPREPAREDSTATEMENTPERCONNECTIONSIZE, 20)
            // 配置监控统计拦截的filters,去掉后监控界面sql无法统计,'wall'用于防火墙
            .put(ConfigKey.DruidConfigKey.FILTERS, "stat,wall,slf4j")
            // 通过 connectProperties 属性来打开mergeSql功能, 慢SQL记录
            .put(ConfigKey.DruidConfigKey.CONNECTIONPROPERTIES, "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000")
            .put(ConfigKey.DruidConfigKey.USUEGLOBALDATASOURCESTAT, ConfigDefaultValue.TRUE)
            .put(ConfigKey.DruidConfigKey.WEB_FILTER, ConfigDefaultValue.TRUE)
            .put(ConfigKey.DruidConfigKey.WEB_FILTER_URL_PATTERN, "/*")
            .put(ConfigKey.DruidConfigKey.WEB_FILTER_EXCLUSIONS, "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")
            .put(ConfigKey.DruidConfigKey.STAT, ConfigDefaultValue.TRUE)
            .put(ConfigKey.DruidConfigKey.STAT_URL_PATTERN, "/druid/*")
            .put(ConfigKey.DruidConfigKey.STAT_ALLOW, NetUtils.getLocalHost())
            .put(ConfigKey.DruidConfigKey.STAT_DENY, "")
            // 禁用HTML页面上的“Reset All”功能
            .put(ConfigKey.DruidConfigKey.STAT_RESET, ConfigDefaultValue.FALSE)
            .put(ConfigKey.DruidConfigKey.STAT_USERNAME, "zeka.stack")
            .put(ConfigKey.DruidConfigKey.STAT_PASSWORD, "zeka@stack");

    }

    /**
     * 获取执行顺序
     *
     * 该方法返回初始化器的执行顺序，数值越小优先级越高。
     * 设置为 HIGHEST_PRECEDENCE + 200，确保在大部分配置之前执行，
     * 但在核心框架配置之后执行。
     *
     * @return int 执行顺序值
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

    /**
     * 获取初始化器名称
     *
     * 该方法返回初始化器的唯一标识名称，用于日志记录和调试。
     * 名称格式为 "模块名/功能名"，便于识别和管理。
     *
     * @return String 初始化器名称
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "cubo-mybatis-spring-boot-starter/druid";
    }
}
