package dev.dong4j.zeka.starter.logsystem.storage;

import dev.dong4j.zeka.starter.logsystem.entity.AbstractLog;

/**
 * 日志存储接口
 *
 * 该接口定义了日志存储的标准规范，用于统一不同类型日志的存储操作。
 * 支持泛型设计，可以存储各种类型的日志实体。
 *
 * 主要功能包括：
 * 1. 定义日志存储的标准方法
 * 2. 支持泛型设计，适配不同类型的日志实体
 * 3. 提供统一的存储接口规范
 * 4. 支持日志存储的扩展和实现
 *
 * 使用场景：
 * - 定义日志存储的标准接口
 * - 实现具体的日志存储逻辑
 * - 支持多种存储方式（数据库、文件、消息队列等）
 * - 提供日志存储的抽象层
 *
 * 设计意图：
 * 通过接口定义日志存储的标准规范，支持多种存储方式的实现，
 * 提供统一的日志存储抽象层。
 *
 * @param <T> 日志实体类型，必须继承自AbstractLog
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 10:41
 * @since 1.0.0
 */
public interface ILogStorage<T extends AbstractLog> {

    /**
     * 保存日志实体
     *
     * 将日志实体保存到指定的存储介质中，如数据库、文件系统或消息队列等。
     * 具体实现由实现类决定，支持异步或同步存储。
     *
     * @param logging 要保存的日志实体对象
     * @return 保存结果，true表示成功，false表示失败
     * @since 1.0.0
     */
    boolean save(T logging);
}
