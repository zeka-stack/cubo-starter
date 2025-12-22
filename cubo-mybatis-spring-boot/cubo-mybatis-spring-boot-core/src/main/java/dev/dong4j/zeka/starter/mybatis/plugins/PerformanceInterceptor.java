package dev.dong4j.zeka.starter.mybatis.plugins;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import dev.dong4j.zeka.kernel.common.context.ExpandIds;
import dev.dong4j.zeka.kernel.common.context.ExpandIdsContext;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.event.SqlExecuteTimeoutEvent;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.mybatis.util.SqlUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SQL 性能监控拦截器
 * <p>
 * 该拦截器用于监控 SQL 执行性能，记录每条 SQL 语句的执行时间，
 * 并在超过阈值时发出警告或记录到日志文件中。
 * <p>
 * 主要功能：
 * 1. 记录 SQL 执行时间，精确到毫秒
 * 2. 格式化输出 SQL 语句，便于调试
 * 3. 设置 SQL 执行超时阈值，超时时发出警告
 * 4. 限制 SQL 输出长度，避免日志过长
 * 5. 发布 SQL 执行超时事件，支持异步处理
 * 6. 支持多种数据库连接池的 SQL 提取
 * <p>
 * 拦截方法：
 * - StatementHandler.query：查询操作
 * - StatementHandler.update：更新操作
 * - StatementHandler.batch：批量操作
 * <p>
 * 配置参数：
 * - format：是否格式化 SQL 输出
 * - maxTime：SQL 执行超时阈值（毫秒）
 * - maxLength：SQL 输出最大长度
 * <p>
 * 使用场景：
 * - 开发和测试环境的 SQL 性能监控
 * - 慢查询识别和优化
 * - SQL 执行情况的统计分析
 * <p>
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
@SuppressWarnings("PMD.UndefineMagicConstantRule")
public class PerformanceInterceptor implements Interceptor {
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
    /** Druid get sql method */
    private Method druidGetSqlMethod;

    /**
     * 拦截方法, 用于监控 SQL 执行性能
     * <p> 该方法在 SQL 执行前后进行时间记录, 并计算执行耗时, 随后调用 record 方法记录相关信息.
     *
     * @param invocation 调用上下文, 用于执行 SQL 操作并获取相关参数
     * @return SQL 操作的执行结果
     * @throws Throwable 如果 SQL 执行过程中发生异常
     */
    @Override
    @SuppressWarnings(value = {"checkstyle:NestedIfDepth", "D"})
    public Object intercept(@NotNull Invocation invocation) throws Throwable {
        // 计算执行 SQL 耗时
        long start = SystemClock.now();

        Object result = invocation.proceed();

        long timing = SystemClock.now() - start;
        this.record(invocation, timing);
        return result;
    }

    /**
     * 记录 SQL 执行信息
     * <p> 该方法用于记录 SQL 语句的执行时间,ID 和具体内容, 并在执行时间超过设定阈值时进行日志记录或事件发布.
     *
     * @param invocation 调用上下文, 用于获取 SQL 语句和相关元数据
     * @param timing     SQL 执行所花费的时间 (毫秒)
     */
    private void record(@NotNull Invocation invocation, long timing) {
        try {
            // 获取被代理对象
            Object target = PluginUtils.realTarget(invocation.getTarget());
            // 获取被代理对象的元数据
            MetaObject metaObject = SystemMetaObject.forObject(target);
            // 获取被代理对象的MappedStatement
            MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            String originalSql = getSql(invocation, metaObject, ms);
            String outputSql = applyMaxLength(SqlUtils.sqlFormat(originalSql, this.format));
            // 构建格式化的SQL语句
            StringBuilder formatSql = new StringBuilder()
                .append(" Time: ").append(timing)
                .append(" ms - ID: ").append(ms.getId())
                .append(" Execute SQL: ")
                .append(StringPool.NEWLINE)
                .append(outputSql)
                .append(StringPool.NEWLINE);

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
                try {
                    SpringContext.publishEvent(new SqlExecuteTimeoutEvent(map));
                } catch (Exception ignored) {
                }
            } else {
                log.debug("{}", formatSql);
            }
        } catch (Exception e) {
            log.error("SQL 执行时间监控异常: {}", e.getMessage());
        }
    }

    /**
     * 从调用上下文中提取原始 SQL 语句
     * <p> 该方法尝试从 Statement 对象中提取实际执行的 SQL 语句. 如果无法从 Statement 中提取, 则尝试从 BoundSql 中获取 SQL.
     * 若两者均无法获取, 则返回空字符串.
     *
     * @param invocation      调用上下文, 用于获取参数
     * @param metaObject      用于访问目标对象的元数据对象
     * @param mappedStatement 与 SQL 语句相关联的 MappedStatement 对象
     * @return 提取到的原始 SQL 语句, 若无法获取则返回空字符串
     */
    private String getSql(@NotNull Invocation invocation, MetaObject metaObject, MappedStatement mappedStatement) {
        String rawSql = null;
        try {
            Statement statement = extractTargetStatement(invocation);
            if (statement != null) {
                rawSql = extractRawSqlFromStatement(statement);
            }
        } catch (Exception e) {
            log.debug("无法从 Statement 中提取 SQL: {}", e.getMessage());
        }

        if (StringUtils.isBlank(rawSql)) {
            rawSql = extractSqlFromBoundSql(metaObject, mappedStatement);
        }

        // 格式化 SQL（去除多余空格 + 定位 SQL 开始位置）
        return formatSql(rawSql);
    }

    /**
     * 从 Invocation 中提取最内层的 Statement 对象
     * <p> 由于 Statement 对象可能经过多层代理, 该方法会穿透代理层, 获取实际的 Statement 实例.
     *
     * @param invocation 调用上下文, 用于获取参数
     * @return 提取到的最内层 Statement 对象
     */
    private Statement extractTargetStatement(Invocation invocation) {
        // 获取第一个参数
        Object firstArg = invocation.getArgs()[0];
        Statement statement;
        // 判断第一个参数是否是代理对象
        if (Proxy.isProxyClass(firstArg.getClass())) {
            // 如果是代理对象，获取被代理对象
            MetaObject metaObject = SystemMetaObject.forObject(firstArg);
            if (metaObject.hasGetter("h.statement")) {
                Object target = metaObject.getValue("h.statement");
                statement = (Statement) target;
            } else if (metaObject.hasGetter("statement")) {
                Object target = metaObject.getValue("statement");
                statement = (Statement) target;
            } else {
                statement = (Statement) firstArg;
            }
        } else {
            // 如果不是代理对象，直接获取第一个参数
            statement = (Statement) firstArg;
        }
        return unwrapStatement(statement);
    }

    private Statement unwrapStatement(Statement statement) {
        Statement current = statement;
        for (int i = 0; i < 3; i++) {
            MetaObject metaObject = SystemMetaObject.forObject(current);
            Statement next = null;

            if (metaObject.hasGetter("stmt")) {
                Object stmtObj = metaObject.getValue("stmt");
                if (stmtObj instanceof Statement) {
                    next = (Statement) stmtObj;
                } else if (stmtObj != null) {
                    MetaObject stmtMeta = SystemMetaObject.forObject(stmtObj);
                    if (stmtMeta.hasGetter("statement")) {
                        Object inner = stmtMeta.getValue("statement");
                        if (inner instanceof Statement) {
                            next = (Statement) inner;
                        }
                    }
                }
            }

            if (next == null && metaObject.hasGetter("statement")) {
                Object inner = metaObject.getValue("statement");
                if (inner instanceof Statement) {
                    next = (Statement) inner;
                }
            }

            if (next == null && metaObject.hasGetter(DELEGATE)) {
                Object inner = metaObject.getValue(DELEGATE);
                if (inner instanceof Statement) {
                    next = (Statement) inner;
                }
            }

            if (next == null || next == current) {
                break;
            }
            current = next;
        }
        return current;
    }


    /**
     * 从 Statement 中提取原始 SQL
     * <p> 该方法尝试从 Druid 的 Statement 对象中获取实际执行的 SQL 语句. 如果无法获取, 则返回 Statement 的字符串表示.
     *
     * @param statement 要提取 SQL 的 Statement 对象
     * @return 提取到的原始 SQL 语句, 若无法获取则返回 Statement 的 toString() 结果
     */
    private String extractRawSqlFromStatement(Statement statement) {
        if (statement == null) {
            return "";
        }
        String className = statement.getClass().getName();

        // Druid 场景：调用 getSql() 方法
        if (className.contains("DruidPooledPreparedStatement")) {
            try {
                if (druidGetSqlMethod == null) {
                    druidGetSqlMethod = statement.getClass().getMethod("getSql");
                }
                Object sqlObj = druidGetSqlMethod.invoke(statement);
                if (sqlObj instanceof String) {
                    return (String) sqlObj;
                }
            } catch (Exception e) {
                log.warn("无法从 Druid Statement 中提取 SQL: {}", e.getMessage());
            }
        }

        // 默认回退：toString()
        return statement.toString();
    }

    /**
     * 格式化 SQL 语句(去除多余空格并截取有效部分)
     * <p>该方法用于清理 SQL 语句中的多余空格, 并截取从 SQL 关键字 (如 SELECT,UPDATE 等) 开始的有效部分.
     *
     * @param sql 需要格式化的原始 SQL 语句
     * @return 格式化后的 SQL 语句, 若输入为空或 null 则返回空字符串
     */
    private String formatSql(String sql) {
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

    private String extractSqlFromBoundSql(MetaObject metaObject, MappedStatement mappedStatement) {
        if (metaObject == null || mappedStatement == null) {
            return "";
        }
        try {
            BoundSql boundSql;
            if (metaObject.hasGetter("boundSql")) {
                boundSql = (BoundSql) metaObject.getValue("boundSql");
            } else if (metaObject.hasGetter("delegate.boundSql")) {
                boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            } else {
                return "";
            }
            return buildSql(mappedStatement.getConfiguration(), boundSql);
        } catch (Exception e) {
            log.debug("无法从 BoundSql 中提取 SQL: {}", e.getMessage());
            return "";
        }
    }

    private String buildSql(Configuration configuration, BoundSql boundSql) {
        if (boundSql == null || configuration == null) {
            return "";
        }
        String sql = boundSql.getSql();
        if (StringUtils.isBlank(sql)) {
            return "";
        }
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterObject == null || CollectionUtils.isEmpty(parameterMappings)) {
            return sql;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        String parsedSql = sql.replaceAll("\\s+", " ").trim();
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            return replaceFirstPlaceholder(parsedSql, parameterObject);
        }
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() == ParameterMode.OUT) {
                continue;
            }
            String propertyName = parameterMapping.getProperty();
            Object value;
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (metaObject.hasGetter(propertyName)) {
                value = metaObject.getValue(propertyName);
            } else {
                value = null;
            }
            parsedSql = replaceFirstPlaceholder(parsedSql, value);
        }
        return parsedSql;
    }

    private String replaceFirstPlaceholder(String sql, Object value) {
        if (StringUtils.isBlank(sql)) {
            return "";
        }
        return sql.replaceFirst("\\?", Matcher.quoteReplacement(formatParameterValue(value)));
    }

    private String formatParameterValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof Date) {
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value) + "'";
        }
        return String.valueOf(value);
    }

    private String applyMaxLength(String sql) {
        if (StringUtils.isBlank(sql) || this.maxLength <= 0 || sql.length() <= this.maxLength) {
            return sql == null ? "" : sql;
        }
        return sql.substring(0, this.maxLength) + "...";
    }


    /**
     * 插件对象
     * <p> 该方法用于将当前拦截器包装成插件对象, 以便 MyBatis 能够识别并应用该拦截器.
     *
     * @param target 要包装的目标对象
     * @return 包装后的插件对象, 若目标对象不是 StatementHandler 则直接返回原对象
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
     * 设置拦截器属性
     * <p> 该方法用于从配置中读取属性并设置到拦截器实例上, 包括 SQL 执行超时阈值, 是否格式化 SQL 以及 SQL 输出最大长度.
     *
     * @param prop 包含配置属性的 Properties 对象
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
     * 获取 SQL 语句的起始位置
     * <p>该方法用于查找 SQL 语句中关键字 (如 SELECT,UPDATE,INSERT,DELETE) 的起始位置, 并返回最早出现的位置.
     * 若未找到任何关键字, 则返回 -1.
     *
     * @param sql 需要分析的 SQL 语句
     * @return SQL 关键字的起始位置, 若未找到则返回 -1
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
