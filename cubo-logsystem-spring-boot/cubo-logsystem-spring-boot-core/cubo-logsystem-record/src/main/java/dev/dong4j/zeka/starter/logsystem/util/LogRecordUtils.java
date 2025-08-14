package dev.dong4j.zeka.starter.logsystem.util;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.DateUtils;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.UrlUtils;
import dev.dong4j.zeka.kernel.common.util.WebUtils;
import dev.dong4j.zeka.starter.logsystem.entity.AbstractLog;
import dev.dong4j.zeka.starter.logsystem.storage.ILogStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.31 10:22
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class LogRecordUtils {

    /**
     * 向log中添加补齐 request 的信息
     *
     * @param request     请求
     * @param abstractLog 日志基础类
     * @since 1.0.0
     */
    public static void addRequestInfoToLog(HttpServletRequest request, @NotNull AbstractLog abstractLog) {
        abstractLog.setRemoteIp(WebUtils.getIp(request));
        abstractLog.setUserAgent(request.getHeader(WebUtils.USER_AGENT_HEADER));
        abstractLog.setRequestUri(UrlUtils.getPath(request.getRequestURI()));
        abstractLog.setHttpMethod(request.getMethod());
        abstractLog.setParams(WebUtils.getRequestParamString(request));
        // todo-dong4j : (2020-09-14 23:49) [获取操作人信息]
        abstractLog.setCreateBy(StringPool.NULL_STRING);
    }

    /**
     * 向 log 中添加补齐其他的信息
     *
     * @param abstractLog 日志基础类
     * @since 1.0.0
     */
    public static void addOtherInfoToLog(@NotNull AbstractLog abstractLog) {
        abstractLog.setServiceId(ConfigKit.getAppName());
        abstractLog.setServerHost(ConfigKit.getHostName());
        abstractLog.setServerIp(ConfigKit.getIpWithPort());
        abstractLog.setEnv(ConfigKit.getEnv());
        abstractLog.setCreateTime(DateUtils.now());
        if (abstractLog.getParams() == null) {
            abstractLog.setParams(StringPool.EMPTY);
        }
    }

    /**
     * Save *
     *
     * @param <T>        parameter
     * @param logging    logging
     * @param logStorage log storage
     * @since 1.0.0
     */
    public static <T extends AbstractLog> void save(T logging, ILogStorage<T> logStorage) {
        if (logStorage == null) {
            log.warn("未找到 logStorage 实现类, 将不会发送日志. 如果需要发送日志, 请正确配置 ILogStorage 实现类");
        } else if (logStorage.save(logging)) {
            log.debug("日志保存成功");
        } else {
            log.debug("日志保存失败: [{}]", logging);
        }
    }
}
