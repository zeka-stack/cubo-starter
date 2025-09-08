package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEventHandler;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactory;
import dev.dong4j.zeka.starter.logsystem.storage.ILogStorage;
import dev.dong4j.zeka.starter.logsystem.util.LogRecordUtils;
import jakarta.annotation.Resource;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 错误日志事件处理器
 *
 * 该类负责处理错误日志事件，继承自BaseEventHandler基类。
 * 通过Spring事件机制异步处理错误日志的存储操作，便于问题排查和系统监控。
 *
 * 主要功能包括：
 * 1. 监听错误日志事件
 * 2. 异步处理错误日志存储
 * 3. 补充日志实体的其他信息
 * 4. 委托给日志存储服务进行实际存储
 *
 * 使用场景：
 * - 系统异常的错误日志记录处理
 * - 异步错误日志存储操作
 * - 错误日志事件的监听和处理
 * - 错误日志存储的解耦处理
 *
 * 设计意图：
 * 通过事件处理器实现错误日志的异步处理，提供完整的异常信息记录能力，
 * 支持异常分析、问题排查和系统监控。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 09:29
 * @since 1.0.0
 */
@Slf4j
@Component
public class ErrorLogEventHandler extends BaseEventHandler<ErrorLogEvent> {
    /** Log storage factory */
    @Resource
    private LogStorageFactory logStorageFactory;

    /**
     * 处理错误日志事件
     *
     * 异步处理错误日志事件，从事件中提取错误日志实体，补充其他信息后保存到存储服务中。
     * 该方法使用@Async注解实现异步处理，不会阻塞主线程。
     *
     * 处理流程：
     * 1. 从事件中获取错误日志实体
     * 2. 补充日志实体的其他信息（服务ID、服务器信息等）
     * 3. 获取错误日志存储服务
     * 4. 将日志实体保存到存储服务中
     *
     * @param event 错误日志事件对象
     * @since 1.0.0
     */
    @Async
    @Order
    @Override
    @EventListener
    public void handler(@NotNull ErrorLogEvent event) {
        // 从事件中获取错误日志实体
        Map<String, Object> source = event.getSource();
        ErrorLog errorLog = (ErrorLog) source.get(EventEnum.EVENT_LOG.getName());

        // 补充日志实体的其他信息（服务ID、服务器信息、环境信息等）
        LogRecordUtils.addOtherInfoToLog(errorLog);

        // 获取错误日志存储服务
        ILogStorage<ErrorLog> logStorage = this.logStorageFactory.getErrorLogStorage();

        // 将日志实体保存到存储服务中
        LogRecordUtils.save(errorLog, logStorage);
    }

}
