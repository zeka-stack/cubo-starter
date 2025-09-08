package dev.dong4j.zeka.starter.logsystem.entity;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API日志实体类
 *
 * 该类用于记录REST API接口调用的日志信息，继承自AbstractLog基类。
 * 主要用于记录接口调用的详细信息，包括请求参数、响应时间、用户信息等。
 *
 * 主要功能包括：
 * 1. 记录API接口调用的详细信息
 * 2. 包含用户身份信息
 * 3. 支持日志类型和标题的分类
 * 4. 提供完整的接口调用追踪能力
 *
 * 特有属性：
 * - 日志类型：用于区分不同类型的API调用
 * - 日志标题：接口调用的描述信息
 * - 用户信息：当前登录用户的信息
 *
 * 使用场景：
 * - REST API接口的调用日志记录
 * - 接口性能监控和分析
 * - 用户行为追踪
 * - 接口调用的审计和统计
 *
 * 设计意图：
 * 通过专门的API日志实体，提供完整的接口调用追踪能力，
 * 支持接口监控、性能分析和用户行为分析。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:26
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ApiLog extends AbstractLog {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 日志类型 */
    private String type;
    /** 日志标题 */
    private String title;
    /** User */
    private CurrentUser user;

}
