package dev.dong4j.zeka.starter.logsystem.aspect;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 抽象日志描述解析器
 *
 * 该类提供日志描述解析的基础功能，支持SpEL表达式动态生成日志描述。
 * 通过解析方法参数和SpEL表达式，生成包含实际参数值的日志描述。
 *
 * 主要功能包括：
 * 1. 支持SpEL表达式解析日志描述
 * 2. 自动获取方法参数名称和值
 * 3. 提供日期格式化函数支持
 * 4. 支持模板化日志描述生成
 *
 * 使用场景：
 * - 操作日志的动态描述生成
 * - 接口日志的参数化描述
 * - 业务日志的模板化输出
 * - 日志描述的国际化支持
 *
 * 设计意图：
 * 通过SpEL表达式提供灵活的日志描述生成能力，支持动态参数解析和格式化，
 * 简化日志描述的编写和维护。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.26 11:31
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractLoggerDescParser {

    /** SpEL表达式解析器 */
    private static final ExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    /** 参数名发现器 */
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new StandardReflectionParameterNameDiscoverer();

    /** evaluationContext */
    private static final StandardEvaluationContext EVALUATION_CONTEXT = new StandardEvaluationContext();

    static {
        try {
            EVALUATION_CONTEXT.registerFunction("dateFormat", AbstractLoggerDescParser.class.getDeclaredMethod("dateFormat", Date.class));
        } catch (NoSuchMethodException ignored) {
        }
    }

    /**
     * 解析描述(支持SpEL表达式)
     *
     * @param method method
     * @param args   args
     * @param spel   spel
     * @return the string
     * @since 1.0.0
     */
    protected String parseDescription(Method method, Object[] args, String spel) {
        // 从方法中获取参数名, 并把参数和值添加到计算上下文中
        String[] params = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                EVALUATION_CONTEXT.setVariable(params[i], args[i]);
            }
        }

        Expression expression = SPEL_EXPRESSION_PARSER.parseExpression(spel, new TemplateParserContext());
        return expression.getValue(EVALUATION_CONTEXT, String.class);
    }

    /**
     * 格式化为 yyyy-MM-dd HH:mm:ss
     *
     * @param date date
     * @return string string
     * @since 1.0.0
     */
    @Contract("null -> null")
    private static String dateFormat(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
