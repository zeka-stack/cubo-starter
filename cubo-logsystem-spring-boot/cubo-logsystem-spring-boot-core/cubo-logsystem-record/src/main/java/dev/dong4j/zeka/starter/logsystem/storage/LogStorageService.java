package dev.dong4j.zeka.starter.logsystem.storage;


import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;

/**
 * 日志存储服务接口
 *
 * 该接口定义了日志存储服务的标准规范，由各应用根据具体需求自行实现。
 * 支持不同类型日志的存储操作，提供统一的存储接口。
 *
 * 主要功能包括：
 * 1. 定义日志存储服务的标准方法
 * 2. 支持系统日志、API日志、错误日志的存储
 * 3. 提供统一的存储接口规范
 * 4. 支持多种存储方式的实现
 *
 * 使用场景：
 * - 定义日志存储服务的标准接口
 * - 实现具体的日志存储逻辑
 * - 支持多种存储方式（数据库、文件、消息队列等）
 * - 提供日志存储的抽象层
 *
 * 设计意图：
 * 通过接口定义日志存储服务的标准规范，由各应用根据具体需求实现，
 * 提供灵活的日志存储配置能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:19
 * @since 1.0.0
 */
public interface LogStorageService {

    /**
     * 保存系统日志
     *
     * 保存系统操作日志，一般用于后台管理系统的操作审计。
     * 记录用户的操作行为，便于审计和问题排查。
     *
     * @param log 系统日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    Boolean save(SystemLog log);

    /**
     * 保存接口日志
     *
     * 保存API接口调用日志，适用于所有的Web服务。
     * 记录接口调用的详细信息，便于性能分析和问题排查。
     *
     * @param log API日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    Boolean save(ApiLog log);

    /**
     * 保存错误日志
     *
     * 保存系统异常和错误日志，一般存储在Elasticsearch等搜索引擎中。
     * 记录系统异常信息，便于错误分析和系统监控。
     *
     * @param log 错误日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    Boolean save(ErrorLog log);
}
