package dev.dong4j.zeka.starter.logsystem.publisher;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.kernel.common.util.WebUtils;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;
import dev.dong4j.zeka.starter.logsystem.enums.OperationAction;
import dev.dong4j.zeka.starter.logsystem.event.SystemLogEvent;
import dev.dong4j.zeka.starter.logsystem.util.LogRecordUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * 系统日志事件发布器
 *
 * 该类负责发布系统日志事件，用于记录系统操作的详细信息。
 * 通过Spring事件机制异步处理系统日志记录，便于系统操作审计和监控。
 *
 * 主要功能包括：
 * 1. 构建系统日志实体对象
 * 2. 从请求中提取相关上下文信息
 * 3. 发布系统日志事件供事件处理器处理
 * 4. 支持操作动作和操作名称的记录
 *
 * 使用场景：
 * - 系统敏感操作的审计日志记录
 * - 用户操作的追踪和监控
 * - 系统安全审计
 * - 操作统计和分析
 *
 * 设计意图：
 * 通过事件发布机制实现系统日志的异步处理，提供完整的操作审计能力，
 * 支持系统安全监控和操作分析。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:04
 * @since 1.0.0
 */
@Slf4j
public class SystemLogPublisher {

    /**
     * 发布系统日志事件
     *
     * 该方法用于发布系统日志事件，构建包含系统操作信息的日志对象，
     * 并通过Spring事件机制异步处理系统日志记录。
     *
     * 处理流程：
     * 1. 从当前请求上下文中获取HttpServletRequest对象
     * 2. 构建SystemLog对象，设置操作名称和操作动作
     * 3. 从请求中提取IP地址、用户代理、请求参数等信息
     * 4. 发布系统日志事件供事件处理器处理
     *
     * @param operationAction 操作动作枚举，定义操作的类型
     * @param operationName   操作名称，描述具体的操作内容
     * @since 1.0.0
     */
    public static void publishEvent(@NotNull OperationAction operationAction, String operationName) {
        // 获取当前HTTP请求对象
        HttpServletRequest request = WebUtils.getRequest();

        // 构建系统日志对象，设置操作名称
        SystemLog systemLog = SystemLog.builder().operationName(operationName).build();
        // 设置操作动作代码
        systemLog.setOperationAction(operationAction.getCode());

        // 添加请求相关信息（IP地址、用户代理、请求参数等）
        LogRecordUtils.addRequestInfoToLog(request, systemLog);

        // 构建事件对象并发布系统日志事件
        Map<String, Object> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), systemLog);
        event.put(EventEnum.EVENT_REQUEST.getName(), request);
        log.debug("发送保存操作日志事件. [{}]", systemLog);
        SpringContext.publishEvent(new SystemLogEvent(event));
    }

}
