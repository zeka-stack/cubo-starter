package dev.dong4j.zeka.starter.logsystem.publisher;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.kernel.common.util.Exceptions;
import dev.dong4j.zeka.kernel.common.util.ObjectUtils;
import dev.dong4j.zeka.kernel.common.util.UrlUtils;
import dev.dong4j.zeka.kernel.common.util.WebUtils;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.event.ErrorLogEvent;
import dev.dong4j.zeka.starter.logsystem.util.LogRecordUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 错误日志事件发布器
 *
 * 该类负责发布错误日志事件，用于记录系统异常和错误的详细信息。
 * 通过Spring事件机制异步处理错误日志记录，便于问题排查和系统监控。
 *
 * 主要功能包括：
 * 1. 构建错误日志实体对象
 * 2. 提取异常堆栈信息和错误位置
 * 3. 从请求中提取相关上下文信息
 * 4. 发布错误日志事件供事件处理器处理
 *
 * 使用场景：
 * - 系统异常的错误日志记录
 * - 异常分析和问题排查
 * - 错误监控和告警
 * - 系统稳定性分析
 *
 * 设计意图：
 * 通过事件发布机制实现错误日志的异步处理，提供完整的异常信息记录能力，
 * 支持异常分析、问题排查和系统监控。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 09:26
 * @since 1.0.0
 */
public class ErrorLogPublisher {

    /**
     * 发布错误日志事件
     *
     * 该方法用于发布错误日志事件，构建包含异常详细信息的日志对象，
     * 并通过Spring事件机制异步处理错误日志记录。
     *
     * 处理流程：
     * 1. 从当前请求上下文中获取HttpServletRequest对象
     * 2. 构建ErrorLog对象，设置请求URI信息
     * 3. 提取异常的堆栈信息、异常名称、错误消息等
     * 4. 从堆栈信息中获取异常发生的具体位置（文件名、行号等）
     * 5. 添加请求相关信息并发布错误日志事件
     *
     * @param error 异常对象，包含错误的详细信息
     * @since 1.0.0
     */
    public static void publishEvent(Throwable error) {
        // 获取当前HTTP请求对象
        HttpServletRequest request = WebUtils.getRequest();

        // 构建错误日志对象
        ErrorLog errorLog = ErrorLog.builder().build();
        // 设置请求URI路径
        errorLog.setRequestUri(UrlUtils.getPath(request.getRequestURI()));

        // 如果异常对象不为空，提取异常详细信息
        if (ObjectUtils.isNotEmpty(error)) {
            // 设置异常堆栈信息
            errorLog.setStackTrace(Exceptions.getStackTraceAsString(error));
            // 设置异常类名
            errorLog.setExceptionName(error.getClass().getName());
            // 设置异常消息
            errorLog.setMessage(error.getMessage());

            // 获取堆栈跟踪元素，提取异常发生的具体位置
            StackTraceElement[] elements = error.getStackTrace();
            if (ObjectUtils.isNotEmpty(elements)) {
                // 获取第一个堆栈元素（异常发生的位置）
                StackTraceElement element = elements[0];
                errorLog.setMethodName(element.getMethodName());
                errorLog.setMethodClass(element.getClassName());
                errorLog.setFileName(element.getFileName());
                errorLog.setLineNumber(element.getLineNumber());
            }
        }

        // 添加请求相关信息（IP地址、用户代理、请求参数等）
        LogRecordUtils.addRequestInfoToLog(request, errorLog);

        // 构建事件对象并发布错误日志事件
        Map<String, Object> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), errorLog);
        event.put(EventEnum.EVENT_REQUEST.getName(), request);
        SpringContext.publishEvent(new ErrorLogEvent(event));
    }

}
