package dev.dong4j.zeka.starter.logsystem.plugin;

import dev.dong4j.zeka.kernel.common.util.StringPool;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * 日志系统输出追加器
 *
 * 该类提供默认的日志输出追加功能，用于在日志输出中添加应用类型标识。
 * 主要功能包括：
 * 1. 提供默认的应用类型输出格式
 * 2. 支持在日志输出中追加应用类型标识
 * 3. 提供统一的输出格式规范
 *
 * 使用场景：
 * - 日志输出格式的统一化
 * - 应用类型标识的默认输出
 * - 日志系统的扩展点
 *
 * 设计意图：
 * 通过提供默认的输出追加器，确保日志输出格式的一致性，
 * 同时为自定义输出提供基础模板。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.0.0
 */
@UtilityClass
public class LogSystemOutputAppender {

    /**
     * 追加应用类型标识
     *
     * 在指定的字符串构建器中追加默认的应用类型标识。
     * 输出格式为 "AT: " + 空字符串，用于标识应用类型。
     *
     * @param toAppendTo 要追加内容的字符串构建器
     * @since 1.0.0
     */
    public static void append(@NotNull StringBuilder toAppendTo) {
        toAppendTo.append("AT: " + StringPool.NULL_STRING);
    }
}
