package dev.dong4j.zeka.starter.logsystem.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 错误日志实体类
 *
 * 该类用于记录系统异常和错误的日志信息，继承自AbstractLog基类。
 * 主要用于记录异常发生的详细信息，包括堆栈信息、异常类型、错误位置等。
 *
 * 主要功能包括：
 * 1. 记录系统异常和错误的详细信息
 * 2. 包含完整的异常堆栈信息
 * 3. 记录异常发生的具体位置
 * 4. 提供异常分析和问题排查能力
 *
 * 特有属性：
 * - 堆栈信息：完整的异常堆栈跟踪
 * - 异常名称：异常类的完整名称
 * - 异常消息：异常的描述信息
 * - 文件名：异常发生的源文件名
 * - 行号：异常发生的具体行号
 *
 * 使用场景：
 * - 系统异常的错误日志记录
 * - 异常分析和问题排查
 * - 错误监控和告警
 * - 系统稳定性分析
 *
 * 设计意图：
 * 通过专门的错误日志实体，提供完整的异常信息记录能力，
 * 支持异常分析、问题排查和系统监控。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:27
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ErrorLog extends AbstractLog {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** 堆栈信息 */
    private String stackTrace;
    /** 异常名 */
    private String exceptionName;
    /** 异常消息 */
    private String message;
    /** 文件名 */
    private String fileName;
    /** 代码行数 */
    private Integer lineNumber;

}
