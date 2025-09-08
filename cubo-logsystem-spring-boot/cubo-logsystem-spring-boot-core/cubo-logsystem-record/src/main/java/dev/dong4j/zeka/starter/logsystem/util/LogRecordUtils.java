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
 * 日志记录工具类
 *
 * 该类提供日志记录相关的工具方法，用于处理日志实体的信息补充和存储操作。
 * 包含静态工具方法，支持日志记录的标准化处理。
 *
 * 主要功能包括：
 * 1. 向日志实体添加请求相关信息（IP地址、用户代理、请求参数等）
 * 2. 向日志实体添加其他信息（服务ID、服务器信息、环境信息等）
 * 3. 提供日志存储的统一处理方法
 * 4. 支持日志记录的标准化和规范化
 *
 * 使用场景：
 * - 日志实体的信息补充
 * - 日志存储的统一处理
 * - 日志记录的标准化操作
 * - 日志工具方法的统一管理
 *
 * 设计意图：
 * 通过工具类提供日志记录相关的通用方法，简化日志处理的代码，
 * 提供日志记录的标准化和规范化处理能力。
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
     * 向日志实体添加请求相关信息
     *
     * 从HTTP请求中提取相关信息并设置到日志实体中，包括IP地址、用户代理、
     * 请求URI、HTTP方法、请求参数等信息。
     *
     * @param request      HTTP请求对象
     * @param abstractLog  日志实体对象
     * @since 1.0.0
     */
    public static void addRequestInfoToLog(HttpServletRequest request, @NotNull AbstractLog abstractLog) {
        // 设置客户端IP地址
        abstractLog.setRemoteIp(WebUtils.getIp(request));
        // 设置用户代理信息
        abstractLog.setUserAgent(request.getHeader(WebUtils.USER_AGENT_HEADER));
        // 设置请求URI路径
        abstractLog.setRequestUri(UrlUtils.getPath(request.getRequestURI()));
        // 设置HTTP请求方法
        abstractLog.setHttpMethod(request.getMethod());
        // 设置请求参数
        abstractLog.setParams(WebUtils.getRequestParamString(request));
        // TODO: 获取操作人信息，目前设置为空字符串
        abstractLog.setCreateBy(StringPool.NULL_STRING);
    }

    /**
     * 向日志实体添加其他信息
     *
     * 向日志实体添加服务相关信息，包括服务ID、服务器主机名、服务器IP、
     * 环境信息、创建时间等信息。
     *
     * @param abstractLog 日志实体对象
     * @since 1.0.0
     */
    public static void addOtherInfoToLog(@NotNull AbstractLog abstractLog) {
        // 设置服务ID
        abstractLog.setServiceId(ConfigKit.getAppName());
        // 设置服务器主机名
        abstractLog.setServerHost(ConfigKit.getHostName());
        // 设置服务器IP和端口
        abstractLog.setServerIp(ConfigKit.getIpWithPort());
        // 设置环境信息
        abstractLog.setEnv(ConfigKit.getEnv());
        // 设置创建时间
        abstractLog.setCreateTime(DateUtils.now());
        // 如果请求参数为空，设置为空字符串
        if (abstractLog.getParams() == null) {
            abstractLog.setParams(StringPool.EMPTY);
        }
    }

    /**
     * 保存日志实体
     *
     * 将日志实体保存到指定的存储服务中，支持泛型设计。
     * 如果存储服务未配置，会记录警告日志。
     *
     * @param <T>        日志实体类型，必须继承自AbstractLog
     * @param logging    要保存的日志实体对象
     * @param logStorage 日志存储服务实例
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
