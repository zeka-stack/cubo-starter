package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEventHandler;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactory;
import dev.dong4j.zeka.starter.logsystem.storage.ILogStorage;
import dev.dong4j.zeka.starter.logsystem.util.LogRecordUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>Description: 异步监听日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:08
 * @since 1.0.0
 */
@Slf4j
@Component
public class SystemLogHandler extends BaseEventHandler<SystemLogEvent> {
    /** Log storage factory */
    @Resource
    private LogStorageFactory logStorageFactory;

    /**
     * Handler *
     *
     * @param event event
     * @since 1.0.0
     */
    @Async
    @Order
    @Override
    @EventListener
    public void handler(@NotNull SystemLogEvent event) {
        Map<String, Object> source = event.getSource();
        SystemLog systemLog = (SystemLog) source.get(EventEnum.EVENT_LOG.getName());
        LogRecordUtils.addOtherInfoToLog(systemLog);
        ILogStorage<SystemLog> logStorage = this.logStorageFactory.getSystemLogStorage();
        LogRecordUtils.save(systemLog, logStorage);
    }
}
