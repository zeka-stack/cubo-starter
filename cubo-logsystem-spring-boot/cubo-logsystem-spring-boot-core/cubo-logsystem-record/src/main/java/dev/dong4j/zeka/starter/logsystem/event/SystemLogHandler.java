package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEventHandler;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;
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
 * 系统日志事件处理器
 *
 * 该类负责处理系统日志事件，继承自BaseEventHandler基类。
 * 通过Spring事件机制异步处理系统日志的存储操作，便于系统操作审计和监控。
 *
 * 主要功能包括：
 * 1. 监听系统日志事件
 * 2. 异步处理系统日志存储
 * 3. 补充日志实体的其他信息
 * 4. 委托给日志存储服务进行实际存储
 *
 * 使用场景：
 * - 系统敏感操作的审计日志记录处理
 * - 异步系统日志存储操作
 * - 系统日志事件的监听和处理
 * - 系统日志存储的解耦处理
 *
 * 设计意图：
 * 通过事件处理器实现系统日志的异步处理，提供完整的操作审计能力，
 * 支持系统安全监控和操作分析。
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
     * 处理系统日志事件
     *
     * 异步处理系统日志事件，从事件中提取系统日志实体，补充其他信息后保存到存储服务中。
     * 该方法使用@Async注解实现异步处理，不会阻塞主线程。
     *
     * 处理流程：
     * 1. 从事件中获取系统日志实体
     * 2. 补充日志实体的其他信息（服务ID、服务器信息等）
     * 3. 获取系统日志存储服务
     * 4. 将日志实体保存到存储服务中
     *
     * @param event 系统日志事件对象
     * @since 1.0.0
     */
    @Async
    @Order
    @Override
    @EventListener
    public void handler(@NotNull SystemLogEvent event) {
        // 从事件中获取系统日志实体
        Map<String, Object> source = event.getSource();
        SystemLog systemLog = (SystemLog) source.get(EventEnum.EVENT_LOG.getName());

        // 补充日志实体的其他信息（服务ID、服务器信息、环境信息等）
        LogRecordUtils.addOtherInfoToLog(systemLog);

        // 获取系统日志存储服务
        ILogStorage<SystemLog> logStorage = this.logStorageFactory.getSystemLogStorage();

        // 将日志实体保存到存储服务中
        LogRecordUtils.save(systemLog, logStorage);
    }
}
