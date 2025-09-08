package dev.dong4j.zeka.starter.logsystem.event;

import dev.dong4j.zeka.kernel.common.event.BaseEventHandler;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.starter.logsystem.entity.AbstractLog;
import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
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
 * API日志事件处理器
 *
 * 该类负责处理API日志事件，继承自BaseEventHandler基类。
 * 通过Spring事件机制异步处理API日志的存储操作，提高系统性能。
 *
 * 主要功能包括：
 * 1. 监听API日志事件
 * 2. 异步处理API日志存储
 * 3. 补充日志实体的其他信息
 * 4. 委托给日志存储服务进行实际存储
 *
 * 使用场景：
 * - API接口调用的日志记录处理
 * - 异步日志存储操作
 * - 日志事件的监听和处理
 * - 日志存储的解耦处理
 *
 * 设计意图：
 * 通过事件处理器实现API日志的异步处理，避免同步日志记录对接口性能的影响，
 * 提供统一的API日志处理标准和存储能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:00
 * @since 1.0.0
 */
@Slf4j
@Component
public class ApiLogEventHandler extends BaseEventHandler<ApiLogEvent> {
    /** Log storage factory */
    @Resource
    private LogStorageFactory logStorageFactory;

    /**
     * 处理API日志事件
     *
     * 异步处理API日志事件，从事件中提取API日志实体，补充其他信息后保存到存储服务中。
     * 该方法使用@Async注解实现异步处理，不会阻塞主线程。
     *
     * 处理流程：
     * 1. 从事件中获取API日志实体
     * 2. 补充日志实体的其他信息（服务ID、服务器信息等）
     * 3. 获取API日志存储服务
     * 4. 将日志实体保存到存储服务中
     *
     * @param event API日志事件对象
     * @since 1.0.0
     */
    @Order
    @Async
    @Override
    @EventListener
    public void handler(@NotNull ApiLogEvent event) {
        // 从事件中获取API日志实体
        Map<String, AbstractLog> source = event.getSource();
        ApiLog apiLog = (ApiLog) source.get(EventEnum.EVENT_LOG.getName());

        // 补充日志实体的其他信息（服务ID、服务器信息、环境信息等）
        LogRecordUtils.addOtherInfoToLog(apiLog);

        // 获取API日志存储服务
        ILogStorage<ApiLog> logStorage = this.logStorageFactory.getApiLogStorage();

        // 将日志实体保存到存储服务中
        LogRecordUtils.save(apiLog, logStorage);
    }
}
