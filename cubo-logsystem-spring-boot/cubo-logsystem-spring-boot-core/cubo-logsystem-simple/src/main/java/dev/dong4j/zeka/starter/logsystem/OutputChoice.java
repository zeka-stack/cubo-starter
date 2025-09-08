package dev.dong4j.zeka.starter.logsystem;

import java.io.PrintStream;
import org.jetbrains.annotations.Contract;

/**
 * 输出选择封装类
 *
 * 该类封装了用户选择的输出目标，用于SimpleLogger的日志输出配置。
 * 支持多种输出方式，包括标准输出、标准错误输出和文件输出。
 *
 * 主要功能包括：
 * 1. 封装输出目标的选择
 * 2. 支持标准输出、标准错误输出和文件输出
 * 3. 提供输出流的获取方法
 * 4. 支持缓存的输出流
 *
 * 使用场景：
 * - SimpleLogger的日志输出配置
 * - 日志输出目标的选择和管理
 * - 输出流的统一封装
 * - 日志输出方式的灵活配置
 *
 * 设计意图：
 * 通过封装类提供统一的输出目标管理，支持多种输出方式的灵活配置，
 * 简化日志输出目标的选择和管理。
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 11:50
 * @since 1.0.0
 */
class OutputChoice {

    /**
     * 输出选择类型枚举
     *
     * 定义了可用的输出选择类型，包括标准输出、标准错误输出和文件输出，
     * 支持缓存和非缓存两种模式。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.08 11:50
     * @since 1.0.0
     */
    enum OutputChoiceType {
        /** 标准输出类型 */
        SYS_OUT,
        /** 缓存的标准输出类型 */
        CACHED_SYS_OUT,
        /** 标准错误输出类型 */
        SYS_ERR,
        /** 缓存的标准错误输出类型 */
        CACHED_SYS_ERR,
        /** 文件输出类型 */
        FILE
    }

    /** Output choice type */
    private final OutputChoiceType outputChoiceType;
    /** Target print stream */
    private final PrintStream targetPrintStream;

    /**
     * 构造输出选择对象（非文件类型）
     *
     * 创建指定输出类型的输出选择对象，不支持文件类型。
     * 对于缓存类型，会预先设置目标输出流。
     *
     * @param outputChoiceType 输出选择类型，不能为FILE类型
     * @throws IllegalArgumentException 如果输出类型为FILE时抛出异常
     * @since 1.0.0
     */
    @Contract(pure = true)
    OutputChoice(OutputChoiceType outputChoiceType) {
        if (outputChoiceType == OutputChoiceType.FILE) {
            throw new IllegalArgumentException();
        }
        this.outputChoiceType = outputChoiceType;
        // 为缓存类型预先设置目标输出流
        if (outputChoiceType == OutputChoiceType.CACHED_SYS_OUT) {
            this.targetPrintStream = System.out;
        } else if (outputChoiceType == OutputChoiceType.CACHED_SYS_ERR) {
            this.targetPrintStream = System.err;
        } else {
            this.targetPrintStream = null;
        }
    }

    /**
     * 构造输出选择对象（文件类型）
     *
     * 创建文件类型的输出选择对象，使用指定的打印流。
     *
     * @param printStream 文件输出流
     * @since 1.0.0
     */
    @Contract(pure = true)
    OutputChoice(PrintStream printStream) {
        this.outputChoiceType = OutputChoiceType.FILE;
        this.targetPrintStream = printStream;
    }

    /**
     * 获取目标打印流
     *
     * 根据输出选择类型返回相应的打印流。
     * 对于标准输出和标准错误输出，直接返回系统流；
     * 对于缓存类型和文件类型，返回预先设置的目标流。
     *
     * @return 目标打印流
     * @throws IllegalArgumentException 如果输出类型不支持时抛出异常
     * @since 1.0.0
     */
    PrintStream getTargetPrintStream() {
        switch (this.outputChoiceType) {
            case SYS_OUT:
                return System.out;
            case SYS_ERR:
                return System.err;
            case CACHED_SYS_ERR:
            case CACHED_SYS_OUT:
            case FILE:
                return this.targetPrintStream;
            default:
                throw new IllegalArgumentException();
        }
    }

}
