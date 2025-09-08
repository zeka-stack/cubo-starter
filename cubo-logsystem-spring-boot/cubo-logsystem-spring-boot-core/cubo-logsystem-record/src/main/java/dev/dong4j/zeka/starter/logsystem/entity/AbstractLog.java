package dev.dong4j.zeka.starter.logsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.dong4j.zeka.kernel.common.enums.ZekaEnv;
import dev.dong4j.zeka.kernel.common.util.DateUtils;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 抽象日志实体基类
 *
 * 该类是所有日志实体的抽象基类，定义了日志记录的基础属性。
 * 为ApiLog、ErrorLog、SystemLog等具体日志类型提供统一的属性结构。
 *
 * 主要功能包括：
 * 1. 定义日志记录的基础属性结构
 * 2. 提供统一的序列化支持
 * 3. 包含请求信息、环境信息、时间信息等
 * 4. 支持日志的标准化存储和查询
 *
 * 基础属性包括：
 * - 标识信息：ID、服务ID、环境等
 * - 请求信息：IP地址、用户代理、请求URI等
 * - 方法信息：类名、方法名、参数等
 * - 时间信息：创建时间、执行时间等
 * - 用户信息：创建人、操作人等
 *
 * 使用场景：
 * - 作为所有日志实体的基类
 * - 提供统一的日志属性定义
 * - 支持日志的标准化处理
 * - 简化日志实体的开发
 *
 * 设计意图：
 * 通过抽象基类提供统一的日志属性结构，确保所有日志类型的一致性，
 * 简化日志实体的开发和维护。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:24
 * @since 1.0.0
 */
@Data
public abstract class AbstractLog implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** Id */
    protected Long id;
    /** 服务ID */
    protected String serviceId;
    /** 服务器 ip */
    protected String serverIp;
    /** 服务器名 */
    protected String serverHost;
    /** 环境 */
    protected ZekaEnv env;
    /** 操作IP地址 */
    protected String remoteIp;
    /** 用户代理 */
    protected String userAgent;
    /** 请求URI */
    protected String requestUri;
    /** 操作方式 */
    protected String httpMethod;
    /** 方法类 */
    protected String methodClass;
    /** 方法名 */
    protected String methodName;
    /** 操作提交的数据 */
    protected String params;
    /** 执行时间 */
    protected Long time;
    /** 创建人 */
    protected String createBy;
    /** 创建时间 */
    @DateTimeFormat(pattern = DateUtils.PATTERN_DATETIME)
    @JsonFormat(pattern = DateUtils.PATTERN_DATETIME)
    protected Date createTime;

}
