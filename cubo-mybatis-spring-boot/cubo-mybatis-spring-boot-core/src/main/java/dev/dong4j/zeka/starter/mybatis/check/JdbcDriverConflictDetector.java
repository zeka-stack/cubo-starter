package dev.dong4j.zeka.starter.mybatis.check;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * JDBC 驱动冲突检测器
 * <p>
 * 用于检测类路径下是否存在多个适用于指定数据源的 JDBC 驱动, 避免因驱动冲突导致的运行时异常.
 * 当检测到多个驱动时, 会记录警告日志并列出所有检测到的驱动信息, 提醒用户保留所需的驱动以避免潜在问题.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
@Slf4j
public final class JdbcDriverConflictDetector {

    /**
     * 私有构造函数, 用于防止外部实例化该类
     * <p>
     * 该类的设计目的是确保只能通过静态方法或单例模式进行访问, 不允许直接创建实例
     */
    private JdbcDriverConflictDetector() {}

    /**
     * 检测类路径中是否存在多个适用于指定数据源的 JDBC 驱动
     * <p>
     * 遍历所有可用的 JDBC 驱动元数据, 检查哪些驱动类已经被加载. 如果检测到多个驱动, 则记录警告日志, 提示用户可能存在多个驱动冲突.
     *
     * @param dataSourceName 数据源名称, 用于日志输出
     * @param jdbcUrl        当前数据源使用的 JDBC URL, 用于日志输出
     */
    public static void detect(String dataSourceName, String jdbcUrl) {
        List<JdbcDriverMeta> present = new ArrayList<>();

        for (JdbcDriverMeta meta : JdbcDriverMeta.values()) {
            if (present(meta.getDriverClass())) {
                present.add(meta);
            }
        }

        if (present.size() <= 1) {
            return;
        }

        log.warn(
            """
                Multiple JDBC drivers detected on classpath for dataSource '{}'

                JDBC URL:
                  {}

                Detected drivers:
                {}

                JDBC uses ServiceLoader / DriverManager to select a driver at runtime.
                If you encounter unexpected behavior, consider keeping only the required driver.
                """,
            dataSourceName,
            jdbcUrl,
            present.stream()
                .map(d -> "  - " + d.getName() + " (" + d.getDriverClass() + ")")
                .reduce("", (a, b) -> a + b + "\n")
                );
    }

    /**
     * 检查指定的 JDBC 驱动类是否可用
     * <p>
     * 尝试加载指定的 JDBC 驱动类, 如果成功加载则返回 true, 否则返回 false.
     *
     * @param driverClass JDBC 驱动类的全限定名称
     * @return 如果驱动类存在且可以加载则返回 true, 否则返回 false
     */
    private static boolean present(String driverClass) {
        try {
            Class.forName(driverClass, false,
                          JdbcDriverConflictDetector.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
