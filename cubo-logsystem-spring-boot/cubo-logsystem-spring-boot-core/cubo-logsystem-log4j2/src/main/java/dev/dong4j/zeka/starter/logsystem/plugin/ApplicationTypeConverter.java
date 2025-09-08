package dev.dong4j.zeka.starter.logsystem.plugin;

import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 应用类型转换器插件
 *
 * 该类是Log4j2的自定义插件，用于在日志输出中添加应用类型标识。
 * 主要功能包括：
 * 1. 扩展Log4j2的pattern转换器，支持自定义格式输出
 * 2. 在日志中输出应用类型标识（如TMS/驾驶员APP/千迅智运APP等）
 * 3. 提供全局唯一的日志ID生成功能
 * 4. 支持链路追踪ID的自动注入
 *
 * 使用方式：
 * - 在Log4j2配置中使用 '%appType' 模式
 * - 输出格式为 "AT:xxxx"，其中xxxx为应用类型或追踪ID
 * - 类似其他Log4j2模式：%d (时间), %C (类名) 等
 *
 * 技术实现：
 * - 实现 {@link LogEventPatternConverter} 接口用于扩展pattern
 * - 使用 {@link Plugin} 注解注册为Log4j2插件
 * - 通过 {@link ConverterKeys} 指定转换器关键字
 *
 * 设计意图：
 * 通过自定义Log4j2插件，在日志输出中自动添加应用类型和追踪信息，
 * 便于日志的识别、分类和问题排查。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:41
 * @since 1.0.0
 */
@Plugin(name = "ApplicationTypeConverter", category = PatternConverter.CATEGORY)
@ConverterKeys(value = {"appType"})
public class ApplicationTypeConverter extends LogEventPatternConverter {

    /**
     * 构造函数
     *
     * 创建应用类型转换器实例，初始化转换器的名称和样式。
     *
     * @param name 转换器名称
     * @param style 转换器样式
     * @since 1.0.0
     */
    protected ApplicationTypeConverter(String name, String style) {
        super(name, style);
    }

    /**
     * 创建转换器实例
     *
     * 该方法必须实现，供Log4j2框架调用。当在配置文件中使用%appType模式时，
     * Log4j2会调用此方法创建转换器实例。
     *
     * @param options 转换器选项参数（未使用）
     * @return 应用类型转换器实例
     * @since 1.0.0
     */
    @NotNull
    @Contract("_ -> new")
    public static ApplicationTypeConverter newInstance(String[] options) {
        return new ApplicationTypeConverter("appType", "appType");
    }

    /**
     * 格式化日志事件
     *
     * 将日志事件格式化为应用类型标识，输出到指定的字符串构建器中。
     * 该方法会检查日志事件是否有消息，如果有则生成并追加应用类型ID。
     *
     * @param event 日志事件对象，包含系统已存在的可选数据
     * @param toAppendTo 最终的输出字符流，用于追加格式化后的内容
     * @since 1.0.0
     */
    @Override
    public void format(@NotNull LogEvent event, StringBuilder toAppendTo) {
        Message msg = event.getMessage();
        if (msg != null) {
            toAppendTo.append(generaterLogId());
        }
    }

    /**
     * 生成业务日志全局UUID
     *
     * 生成用于业务日志的全局唯一标识符。优先使用链路追踪ID，
     * 如果链路追踪ID不存在，则生成新的UUID。
     *
     * 生成策略：
     * 1. 优先使用当前线程的链路追踪ID
     * 2. 如果链路追踪ID为空，则生成新的UUID
     *
     * @return 全局唯一的日志标识符
     * @since 1.0.0
     */
    @NotNull
    private static String generaterLogId() {
        String traceId = Trace.context().get();
        return StringUtils.isBlank(traceId) ? StringUtils.getUid() : traceId;
    }
}
