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
 * <p>Description: 异常信息事件发送 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 09:26
 * @since 1.0.0
 */
public class ErrorLogPublisher {

    /**
     * Publish event *
     *
     * @param error error
     * @since 1.0.0
     */
    public static void publishEvent(Throwable error) {
        HttpServletRequest request = WebUtils.getRequest();
        ErrorLog errorLog = ErrorLog.builder().build();
        errorLog.setRequestUri(UrlUtils.getPath(request.getRequestURI()));
        if (ObjectUtils.isNotEmpty(error)) {
            errorLog.setStackTrace(Exceptions.getStackTraceAsString(error));
            errorLog.setExceptionName(error.getClass().getName());
            errorLog.setMessage(error.getMessage());
            StackTraceElement[] elements = error.getStackTrace();
            if (ObjectUtils.isNotEmpty(elements)) {
                StackTraceElement element = elements[0];
                errorLog.setMethodName(element.getMethodName());
                errorLog.setMethodClass(element.getClassName());
                errorLog.setFileName(element.getFileName());
                errorLog.setLineNumber(element.getLineNumber());
            }
        }
        LogRecordUtils.addRequestInfoToLog(request, errorLog);

        Map<String, Object> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), errorLog);
        event.put(EventEnum.EVENT_REQUEST.getName(), request);
        SpringContext.publishEvent(new ErrorLogEvent(event));
    }

}
