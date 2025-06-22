package dev.dong4j.zeka.starter.logsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.dong4j.zeka.kernel.common.enums.ZekaEnv;
import dev.dong4j.zeka.kernel.common.util.DateUtils;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: logApi、logError、logUsual的父类,拥有相同的属性值 </p>
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
