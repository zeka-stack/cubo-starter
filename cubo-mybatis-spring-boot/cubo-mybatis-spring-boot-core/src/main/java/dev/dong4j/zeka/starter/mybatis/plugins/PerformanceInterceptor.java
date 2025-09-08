package dev.dong4j.zeka.starter.mybatis.plugins;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import dev.dong4j.zeka.kernel.common.context.ExpandIds;
import dev.dong4j.zeka.kernel.common.context.ExpandIdsContext;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.event.SqlExecuteTimeoutEvent;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.mybatis.util.SqlUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * SQL 性能监控拦截器
 *
 * 该拦截器用于监控 SQL 执行性能，记录每条 SQL 语句的执行时间，
 * 并在超过阈值时发出警告或记录到日志文件中。
 *
 * 主要功能：
 * 1. 记录 SQL 执行时间，精确到毫秒
 * 2. 格式化输出 SQL 语句，便于调试
 * 3. 设置 SQL 执行超时阈值，超时时发出警告
 * 4. 限制 SQL 输出长度，避免日志过长
 * 5. 发布 SQL 执行超时事件，支持异步处理
 * 6. 支持多种数据库连接池的 SQL 提取
 *
 * 拦截方法：
 * - StatementHandler.query：查询操作
 * - StatementHandler.update：更新操作
 * - StatementHandler.batch：批量操作
 *
 * 配置参数：
 * - format：是否格式化 SQL 输出
 * - maxTime：SQL 执行超时阈值（毫秒）
 * - maxLength：SQL 输出最大长度
 *
 * 使用场景：
 * - 开发和测试环境的 SQL 性能监控
 * - 慢查询识别和优化
 * - SQL 执行情况的统计分析
 *
 * 注意：建议仅在非生产环境使用，避免影响性能
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:40
 * @since 1.0.0
 */
@Slf4j
@Intercepts(value = {
    @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class PerformanceInterceptor implements Interceptor {

    /** DRUID_POOLED_PREPARED_STATEMENT */
    private static final String DRUID_POOLED_PREPARED_STATEMENT = "com.alibaba.druid.pool.DRUID_POOLED_PREPARED_STATEMENT";
    /** T4C_PREPARED_STATEMENT */
    private static final String T4C_PREPARED_STATEMENT = "oracle.jdbc.driver.T4C_PREPARED_STATEMENT";
    /** ORACLE_PREPARED_STATEMENT_WRAPPER */
    private static final String ORACLE_PREPARED_STATEMENT_WRAPPER = "oracle.jdbc.driver.ORACLE_PREPARED_STATEMENT_WRAPPER";
    /** DELEGATE */
    private static final String DELEGATE = "delegate";
    /** SQL 执行最大时长,超过自动停止运行,有助于发现问题. */
    @Setter
    private long maxTime;
    /** SQL 是否格式化 */
    @Setter
    private boolean format;
    /** 输出的最长 sql */
    @Setter
    private int maxLength;
    /** Oracle get original sql method */
    private Method oracleGetOriginalSqlMethod;
    /** Druid get sql method */
    private Method druidGetSqlMethod;

    /**
     * Intercept object
     *
     * @param invocation invocation
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("checkstyle:NestedIfDepth")
    public Object intercept(@NotNull Invocation invocation) throws Throwable {
        Statement statement;
        Object firstArg = invocation.getArgs()[0];
        if (Proxy.isProxyClass(firstArg.getClass())) {
            statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
        } else {
            statement = (Statement) firstArg;
        }
        MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);
        try {
            statement = (Statement) stmtMetaObj.getValue("stmt.statement");
        } catch (Exception e) {
            // do nothing
        }
        if (stmtMetaObj.hasGetter(DELEGATE)) {
            // Hikari
            try {
                statement = (Statement) stmtMetaObj.getValue(DELEGATE);
            } catch (Exception ignored) {
                // nothing to do
            }
        }

        String originalSql = null;
        String stmtClassName = statement.getClass().getName();
        if (DRUID_POOLED_PREPARED_STATEMENT.equals(stmtClassName)) {
            try {
                if (this.druidGetSqlMethod == null) {
                    Class<?> clazz = Class.forName(DRUID_POOLED_PREPARED_STATEMENT);
                    this.druidGetSqlMethod = clazz.getMethod("getSql");
                }
                Object stmtSql = this.druidGetSqlMethod.invoke(statement);
                if (stmtSql instanceof String) {
                    originalSql = (String) stmtSql;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else if (T4C_PREPARED_STATEMENT.equals(stmtClassName)
            || ORACLE_PREPARED_STATEMENT_WRAPPER.equals(stmtClassName)) {
            try {
                if (this.oracleGetOriginalSqlMethod != null) {
                    Object stmtSql = this.oracleGetOriginalSqlMethod.invoke(statement);
                    if (stmtSql instanceof String) {
                        originalSql = (String) stmtSql;
                    }
                } else {
                    Class<?> clazz = Class.forName(stmtClassName);
                    this.oracleGetOriginalSqlMethod = this.getMethodRegular(clazz, "getOriginalSql");
                    if (this.oracleGetOriginalSqlMethod != null) {
                        // ORACLE_PREPARED_STATEMENT_WRAPPER is not a public class, need set this.
                        this.oracleGetOriginalSqlMethod.setAccessible(true);
                        if (null != this.oracleGetOriginalSqlMethod) {
                            Object stmtSql = this.oracleGetOriginalSqlMethod.invoke(statement);
                            if (stmtSql instanceof String) {
                                originalSql = (String) stmtSql;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                // nothing to do
            }
        }
        if (originalSql == null) {
            originalSql = statement.toString();
        }
        // 计算执行 SQL 耗时
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;
        final String formatSql = format(originalSql);
        String displayedSql = formatSql.length() > maxLength ? formatSql.substring(0, maxLength) + "... [完整SQL已截断]" : originalSql;
        return this.format(invocation, displayedSql, result, timing);
    }

    /**
     * Format object
     *
     * @param invocation  invocation
     * @param originalSql original sql
     * @param result      result
     * @param timing      timing
     * @return the object
     * @since 1.0.0
     */
    @Contract("_, _, _, _ -> param3")
    private Object format(@NotNull Invocation invocation, String originalSql, Object result, long timing) {
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        StringBuilder formatSql = new StringBuilder()
            .append(" Time: ").append(timing)
            .append(" ms - ID: ").append(ms.getId())
            .append(StringPool.NEWLINE).append("Execute SQL: ")
            .append(SqlUtils.sqlFormat(originalSql, this.format)).append(StringPool.NEWLINE);
        if (this.maxTime >= 1 && timing > this.maxTime) {
            log.error("耗时 SQL, 请优化: {}", formatSql);
            Map<String, Object> map = new HashMap<>(8);
            map.put("sql", originalSql);
            map.put("duration", timing);
            map.put("statementId", ms.getId());
            map.put("traceId", Trace.context().get());
            map.put("applicationName", ConfigKit.getAppName());
            map.put("applicationVersion", ConfigKit.getAppVersion());
            ExpandIds expandIds = ExpandIdsContext.context().get();
            if (null != expandIds) {
                map.put("clientId", expandIds.getClientId().orElse(""));
                map.put("tenantId", expandIds.getTenantId().orElse(-1L));
            } else {
                map.put("clientId", "");
                map.put("tenantId", -1L);
            }
            map.put("createTime", new Date());
            SpringContext.publishEvent(new SqlExecuteTimeoutEvent(map));
        } else {
            log.debug("{}", formatSql);
        }
        return result;
    }

    /**
     * 格式化 SQL（去多余空格 + 截取有效部分）
     */
    private String format(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }
        sql = sql.replaceAll("\\s+", " ").trim();
        int startIndex = indexOfSqlStart(sql);
        if (startIndex > 0) {
            sql = sql.substring(startIndex);
        }
        return sql;
    }


    /**
     * Plugin object
     *
     * @param target target
     * @return the object
     * @since 1.0.0
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    /**
     * Sets properties *
     *
     * @param prop prop
     * @since 1.0.0
     */
    @Override
    public void setProperties(@NotNull Properties prop) {
        String maxTime = prop.getProperty("maxTime");
        String format = prop.getProperty("format");
        String maxLength = prop.getProperty("maxLength");
        if (StringUtils.isNotBlank(maxTime)) {
            this.maxTime = Long.parseLong(maxTime);
        }
        if (StringUtils.isNotBlank(format)) {
            this.format = Boolean.parseBoolean(format);
        }
        if (StringUtils.isNotBlank(maxLength)) {
            this.maxLength = Integer.parseInt(maxLength);
        }
    }

    /**
     * 获取此方法名的具体 Method
     *
     * @param clazz      class 对象
     * @param methodName 方法名
     * @return 方法 method regular
     * @since 1.0.0
     */
    @Nullable
    private Method getMethodRegular(Class<?> clazz, String methodName) {
        if (Object.class.equals(clazz)) {
            return null;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return this.getMethodRegular(clazz.getSuperclass(), methodName);
    }

    /**
     * 获取sql语句开头部分
     *
     * @param sql ignore
     * @return ignore int
     * @since 1.0.0
     */
    private int indexOfSqlStart(@NotNull String sql) {
        String upperCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(upperCaseSql.indexOf("SELECT "));
        set.add(upperCaseSql.indexOf("UPDATE "));
        set.add(upperCaseSql.indexOf("INSERT "));
        set.add(upperCaseSql.indexOf("DELETE "));
        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        return list.get(0);
    }
}
