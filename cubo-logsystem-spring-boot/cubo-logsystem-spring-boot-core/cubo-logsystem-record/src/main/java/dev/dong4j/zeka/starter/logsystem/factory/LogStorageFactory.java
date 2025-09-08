package dev.dong4j.zeka.starter.logsystem.factory;

import dev.dong4j.zeka.starter.logsystem.entity.ApiLog;
import dev.dong4j.zeka.starter.logsystem.entity.ErrorLog;
import dev.dong4j.zeka.starter.logsystem.entity.SystemLog;
import dev.dong4j.zeka.starter.logsystem.storage.ILogStorage;
import dev.dong4j.zeka.starter.logsystem.storage.LogStorageService;

/**
 * 日志存储工厂接口
 *
 * 该接口定义了日志存储工厂的标准规范，用于获取不同类型的日志存储服务。
 * 通过工厂模式统一管理各种日志存储服务的创建和获取。
 *
 * 主要功能包括：
 * 1. 定义日志存储工厂的标准方法
 * 2. 支持获取不同类型的日志存储服务
 * 3. 提供统一的工厂接口规范
 * 4. 支持日志存储服务的统一管理
 *
 * 使用场景：
 * - 定义日志存储工厂的标准接口
 * - 实现具体的日志存储工厂逻辑
 * - 支持多种存储方式的统一管理
 * - 提供日志存储服务的抽象层
 *
 * 设计意图：
 * 通过工厂接口定义日志存储服务的统一获取规范，支持多种存储方式的统一管理，
 * 提供灵活的日志存储服务配置能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 17:36
 * @since 1.0.0
 */
public interface LogStorageFactory {

    /**
     * 获取日志存储服务
     *
     * 获取统一的日志存储服务实例，用于处理所有类型的日志存储操作。
     *
     * @return 日志存储服务实例
     * @since 1.0.0
     */
    LogStorageService getLogStorageService();

    /**
     * 获取系统日志存储服务
     *
     * 获取专门用于处理系统日志的存储服务实例。
     *
     * @return 系统日志存储服务实例
     * @since 1.0.0
     */
    ILogStorage<SystemLog> getSystemLogStorage();

    /**
     * 获取错误日志存储服务
     *
     * 获取专门用于处理错误日志的存储服务实例。
     *
     * @return 错误日志存储服务实例
     * @since 1.0.0
     */
    ILogStorage<ErrorLog> getErrorLogStorage();

    /**
     * 获取API日志存储服务
     *
     * 获取专门用于处理API日志的存储服务实例。
     *
     * @return API日志存储服务实例
     * @since 1.0.0
     */
    ILogStorage<ApiLog> getApiLogStorage();
}
