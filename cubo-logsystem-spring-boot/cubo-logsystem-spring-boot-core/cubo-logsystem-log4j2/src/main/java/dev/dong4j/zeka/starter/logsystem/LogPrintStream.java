package dev.dong4j.zeka.starter.logsystem;

import java.io.PrintStream;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 日志打印流类
 *
 * 该类用于替换系统的 System.err 和 System.out，将标准输出和错误输出重定向到日志系统。
 * 主要功能包括：
 * 1. 提供标准输出流的日志化包装
 * 2. 支持错误输出和普通输出的区分处理
 * 3. 重写PrintStream的关键方法，将输出转换为日志
 * 4. 提供静态工厂方法创建不同类型的打印流
 *
 * 使用场景：
 * - 第三方库输出重定向到日志系统
 * - 系统启动过程中的输出日志化
 * - 调试信息的统一日志管理
 * - 避免控制台输出与日志系统分离
 *
 * 设计意图：
 * 通过重定向系统输出流，实现所有输出信息的统一日志化管理，
 * 提升日志的一致性和可管理性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 12:48
 * @since 1.0.0
 */
@Slf4j
public final class LogPrintStream extends PrintStream {
    /** 是否为错误输出流标识 */
    private final boolean error;

    /**
     * 私有构造函数
     *
     * 创建日志打印流实例，根据error参数决定是错误输出还是普通输出。
     *
     * @param error true-错误输出流，false-普通输出流
     * @since 1.0.0
     */
    private LogPrintStream(boolean error) {
        super(error ? System.err : System.out);
        this.error = error;
    }

    /**
     * 创建普通输出流
     *
     * 创建一个用于普通输出的日志打印流，输出内容将作为INFO级别日志记录。
     *
     * @return 普通输出的日志打印流实例
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static LogPrintStream out() {
        return new LogPrintStream(false);
    }

    /**
     * 创建错误输出流
     *
     * 创建一个用于错误输出的日志打印流，输出内容将作为ERROR级别日志记录。
     *
     * @return 错误输出的日志打印流实例
     * @since 1.0.0
     */
    @NotNull
    @Contract(" -> new")
    public static LogPrintStream err() {
        return new LogPrintStream(true);
    }

    /**
     * 打印字符串
     *
     * 将字符串输出转换为日志记录，根据流类型选择相应的日志级别。
     *
     * @param s 要打印的字符串
     * @since 1.0.0
     */
    @Override
    public void print(String s) {
        if (error) {
            log.error(s);
        } else {
            log.info(s);
        }
    }

    /**
     * 打印空行
     *
     * 重写此方法以避免打印无用的空行，因为日志系统会自动处理换行。
     *
     * @since 1.0.0
     */
    @Override
    public void println() {
        // 空实现，避免打印无用的新行
    }

    /**
     * 打印字符串并换行
     *
     * 将字符串输出转换为日志记录，根据流类型选择相应的日志级别。
     *
     * @param x 要打印的字符串
     * @since 1.0.0
     */
    @Override
    public void println(String x) {
        if (error) {
            log.error(x);
        } else {
            log.info(x);
        }
    }

    /**
     * 格式化打印
     *
     * 使用指定的格式字符串和参数进行格式化输出，并转换为日志记录。
     *
     * @param format 格式字符串
     * @param args 格式化参数
     * @return 当前打印流实例，支持链式调用
     * @since 1.0.0
     */
    @Override
    public PrintStream printf(String format, Object... args) {
        if (error) {
            log.error(String.format(format, args));
        } else {
            log.info(String.format(format, args));
        }
        return this;
    }

    /**
     * 本地化格式化打印
     *
     * 使用指定的本地化和格式字符串进行格式化输出，并转换为日志记录。
     *
     * @param l 本地化设置
     * @param format 格式字符串
     * @param args 格式化参数
     * @return 当前打印流实例，支持链式调用
     * @since 1.0.0
     */
    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        if (error) {
            log.error(String.format(l, format, args));
        } else {
            log.info(String.format(l, format, args));
        }
        return this;
    }
}
