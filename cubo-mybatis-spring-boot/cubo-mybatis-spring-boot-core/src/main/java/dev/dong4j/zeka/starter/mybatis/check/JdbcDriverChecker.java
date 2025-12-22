package dev.dong4j.zeka.starter.mybatis.check;

import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC 驱动检查器类
 * <p>
 * 用于检查应用程序中配置的 JDBC 数据源是否具有对应的 JDBC 驱动类, 并验证 JDBC URL 是否可识别.
 * 该类通过解析 JDBC URL 确定数据库类型, 并检查相应的驱动是否存在于类路径中.
 * 若驱动缺失或 URL 类型无法识别, 将记录警告信息并提示用户可能需要添加相关依赖.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
@Slf4j
public final class JdbcDriverChecker {

    /**
     * 数据库类型解析器
     * <p>
     * 用于解析和确定当前数据库的类型
     */
    private final DbTypeResolver dbTypeResolver;

    /**
     * 构造一个 JdbcDriverChecker 实例
     * <p>
     * 使用默认的 DbTypeResolver 初始化 JdbcDriverChecker
     */
    public JdbcDriverChecker() {
        this(new DefaultDbTypeResolver());
    }

    /**
     * 初始化 JdbcDriverChecker 实例
     * <p>
     * 使用提供的数据库类型解析器来检查 JDBC 驱动程序
     *
     * @param resolver 数据库类型解析器, 用于确定数据库类型
     */
    public JdbcDriverChecker(DbTypeResolver resolver) {
        this.dbTypeResolver = resolver;
    }

    /**
     * 检查所有 JDBC 连接 URL
     * <p>
     * 使用提供的数据源从 JdbcUrlProvider 获取所有 JDBC URL, 并对每个 URL 调用 checkOne 方法进行检查.
     *
     * @param urlProvider 用于提供 JDBC URL 的服务
     * @param environment Spring 环境, 用于获取 JDBC URL
     */
    public void check(JdbcUrlProvider urlProvider, Environment environment) {
        for (Map.Entry<String, String> entry : urlProvider.getJdbcUrls(environment).entrySet()) {
            checkOne(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 检查指定数据源的 JDBC URL 是否有效并检测驱动冲突
     * <p>
     * 该方法首先将原始 JDBC URL 规范化为多个 URL, 然后对每个 URL 解析其数据库类型.
     * 如果解析成功, 则检查对应的 JDBC 驱动是否可用; 如果解析失败, 则记录警告信息.
     * 最后, 检测指定数据源是否存在 JDBC 驱动冲突.
     *
     * @param dataSourceName 数据源名称
     * @param rawUrl         原始 JDBC URL
     */
    public void checkOne(String dataSourceName, String rawUrl) {
        List<String> urls = JdbcUrlNormalizer.normalize(rawUrl);

        for (String url : urls) {
            dbTypeResolver.resolve(url).ifPresentOrElse(
                dbType -> checkDriverPresent(dataSourceName, rawUrl, url, dbType),
                () -> warnUnknownUrl(dataSourceName, rawUrl)
                                                       );
        }

        JdbcDriverConflictDetector.detect(dataSourceName, rawUrl);
    }

    /**
     * 检查指定数据源的 JDBC 驱动是否已加载
     * <p>
     * 根据数据库类型检查对应的 JDBC 驱动类是否存在于类路径中. 如果驱动未找到, 将记录警告日志并提示可能需要添加的依赖项.
     *
     * @param ds          数据源名称
     * @param rawUrl      原始 JDBC URL
     * @param resolvedUrl 解析后的 JDBC URL
     * @param dbType      数据库类型
     */
    private void checkDriverPresent(
        String ds,
        String rawUrl,
        String resolvedUrl,
        DbType dbType
                                   ) {
        if (!driverPresent(dbType.getDriverClass())) {
            log.warn(
                """
                    检测到数据源缺少 JDBC 驱动

                    数据源名称：
                      {}

                    配置的 JDBC URL：
                      {}

                    解析后的 JDBC URL：
                      {}

                    识别到的数据库类型：
                      {}

                    当前 classpath 中未发现对应的 JDBC Driver，
                    如果这是你期望使用的数据库，请在项目中添加以下依赖：

                    {}
                    """,
                ds,
                rawUrl,
                resolvedUrl,
                dbType.getName(),
                dbType.getMavenSnippet());
        }
    }

    /**
     * 记录未识别的 JDBC URL 警告信息
     * <p>
     * 当检测到未知的 JDBC URL 时, 向日志中记录警告信息, 提示该框架无法验证此类 URL, 并确保已将所需的 JDBC 驱动程序添加到类路径中.
     *
     * @param ds     数据源标识符
     * @param rawUrl 未识别的原始 JDBC URL
     */
    private void warnUnknownUrl(String ds, String rawUrl) {
        log.warn(
            """
                检测到无法识别的 JDBC URL

                数据源名称：
                  {}

                配置的 JDBC URL：
                  {}

                当前 JDBC URL 不在本组件的可识别范围内，
                可能由其他框架（如 ShardingSphere、定制 Driver、中间件代理等）接管。

                本组件不会校验此类型 JDBC URL 的正确性，
                请自行确保对应的 JDBC 驱动已正确引入。

                如不希望看到此提示，可通过以下配置关闭 JDBC 驱动检查：

                spring:
                  xxx:
                    jdbc-check:
                      enabled: false
                """,
            ds,
            rawUrl);
    }

    /**
     * 检查指定的 JDBC 驱动类是否可用
     * <p>
     * 尝试加载指定的驱动类, 如果成功加载则返回 true, 否则返回 false.
     *
     * @param driverClass JDBC 驱动类的全限定名称
     * @return 如果驱动类存在且可加载则返回 true, 否则返回 false
     */
    private boolean driverPresent(String driverClass) {
        try {
            Class.forName(driverClass, false, JdbcDriverChecker.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
