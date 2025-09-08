package dev.dong4j.zeka.starter.logsystem.publisher;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.util.AuthUtils;
import dev.dong4j.zeka.kernel.auth.util.JwtUtils;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.event.EventEnum;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.kernel.common.util.WebUtils;
import dev.dong4j.zeka.starter.logsystem.annotation.RestLog;
import dev.dong4j.zeka.starter.logsystem.constant.LogSystem;
import dev.dong4j.zeka.starter.logsystem.entity.AbstractLog;
import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
import dev.dong4j.zeka.starter.logsystem.event.ApiLogEvent;
import dev.dong4j.zeka.starter.logsystem.util.LogRecordUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * API日志事件发布器
 *
 * 该类负责发布API日志事件，用于记录REST API接口调用的详细信息。
 * 通过Spring事件机制异步处理API日志记录，提高系统性能。
 *
 * 主要功能包括：
 * 1. 构建API日志实体对象
 * 2. 从请求中提取用户信息和请求参数
 * 3. 发布API日志事件供事件处理器处理
 * 4. 支持JWT token解析获取用户身份信息
 *
 * 使用场景：
 * - REST API接口调用的日志记录
 * - 接口性能监控和分析
 * - 用户行为追踪
 * - 接口调用的审计和统计
 *
 * 设计意图：
 * 通过事件发布机制实现API日志的异步处理，避免同步日志记录对接口性能的影响，
 * 提供统一的API日志记录标准和用户身份识别能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:38
 * @since 1.0.0
 */
@Slf4j
public class ApiLogPublisher {

    /**
     * 发布API日志事件
     *
     * 该方法用于发布API日志事件，构建包含接口调用信息的日志对象，
     * 并通过Spring事件机制异步处理日志记录。
     *
     * 处理流程：
     * 1. 从当前请求上下文中获取HttpServletRequest对象
     * 2. 构建ApiLog对象，设置基本信息和执行时间
     * 3. 从请求中提取IP地址、用户代理、请求参数等信息
     * 4. 尝试从JWT token中解析用户身份信息
     * 5. 发布API日志事件供事件处理器处理
     *
     * @param methodName  方法名称
     * @param methodClass 方法所在类名
     * @param restLog     REST日志注解信息
     * @param time        方法执行时间（毫秒）
     * @since 1.0.0
     */
    public static void publishEvent(String methodName,
                                    String methodClass,
                                    @NotNull RestLog restLog,
                                    long time) {
        // 获取当前HTTP请求对象
        HttpServletRequest request = WebUtils.getRequest();

        // 构建API日志对象，设置基本属性
        ApiLog logApi = ApiLog.builder()
            .type(LogSystem.LOG_NORMAL_TYPE)
            .title(restLog.value())
            .build();

        // 设置日志详细信息
        logApi.setType(LogSystem.LOG_NORMAL_TYPE);
        logApi.setTitle(restLog.value());
        logApi.setTime(time);
        logApi.setMethodClass(methodClass);
        logApi.setMethodName(methodName);

        // 添加请求相关信息（IP地址、用户代理、请求参数等）
        LogRecordUtils.addRequestInfoToLog(request, logApi);

        // 尝试从JWT token中获取用户身份信息
        // 如果请求中没有token或token无效，则用户信息为null
        String token = AuthUtils.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            CurrentUser user = JwtUtils.PlayGround.getUser(token);
            logApi.setUser(user);
            if (user != null) {
                // 设置操作人信息
                logApi.setCreateBy(user.getUsername());
            }
        }

        // 构建事件对象并发布API日志事件
        Map<String, AbstractLog> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), logApi);
        log.debug("发送保存 API 日志事件. [{}]", logApi);
        SpringContext.publishEvent(new ApiLogEvent(event));
    }

}
