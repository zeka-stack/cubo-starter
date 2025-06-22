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
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>Description: API日志信息事件发送 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:38
 * @since 1.0.0
 */
@Slf4j
public class ApiLogPublisher {

    /**
     * Publish event *
     *
     * @param methodName  method name
     * @param methodClass method class
     * @param restLog     api log
     * @param time        time
     * @since 1.0.0
     */
    public static void publishEvent(String methodName,
                                    String methodClass,
                                    @NotNull RestLog restLog,
                                    long time) {
        HttpServletRequest request = WebUtils.getRequest();
        ApiLog logApi = ApiLog.builder()
            .type(LogSystem.LOG_NORMAL_TYPE)
            .title(restLog.value())
            .build();

        logApi.setType(LogSystem.LOG_NORMAL_TYPE);
        logApi.setTitle(restLog.value());
        logApi.setTime(time);
        logApi.setMethodClass(methodClass);
        logApi.setMethodName(methodName);
        LogRecordUtils.addRequestInfoToLog(request, logApi);

        // 从 token 获取用户信息, 如果 request 没有传 token 则 user 为 null
        String token = AuthUtils.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            CurrentUser user = JwtUtils.PlayGround.getUser(token);
            logApi.setUser(user);
            if (user != null) {
                logApi.setCreateBy(user.getUsername());
            }
        }

        Map<String, AbstractLog> event = Maps.newHashMapWithExpectedSize(2);
        event.put(EventEnum.EVENT_LOG.getName(), logApi);
        log.debug("发送保存 API 日志事件. [{}]", logApi);
        SpringContext.publishEvent(new ApiLogEvent(event));
    }

}
