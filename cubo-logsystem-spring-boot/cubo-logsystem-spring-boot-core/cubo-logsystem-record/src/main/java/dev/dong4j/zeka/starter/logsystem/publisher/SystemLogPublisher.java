package dev.dong4j.zeka.starter.logsystem.publisher;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.kernel.common.util.WebUtils;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;
import dev.dong4j.zeka.starter.logsystem.enums.OperationAction;
import dev.dong4j.zeka.starter.logsystem.event.SystemLogEvent;
import dev.dong4j.zeka.starter.logsystem.util.LogRecordUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 系统日志信息事件发送
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:04
 * @since 1.0.0
 */
@Slf4j
public class SystemLogPublisher {

    /**
     * Publish event *
     *
     * @param operationAction operation action
     * @param operationName   operationName
     * @since 1.0.0
     */
    public static void publishEvent(@NotNull OperationAction operationAction, String operationName) {
        HttpServletRequest request = WebUtils.getRequest();
        SystemLog systemLog = SystemLog.builder().operationName(operationName).build();
        systemLog.setOperationAction(operationAction.getCode());
        LogRecordUtils.addRequestInfoToLog(request, systemLog);

        Map<String, Object> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), systemLog);
        event.put(EventEnum.EVENT_REQUEST.getName(), request);
        log.debug("发送保存操作日志事件. [{}]", systemLog);
        SpringContext.publishEvent(new SystemLogEvent(event));
    }

}
