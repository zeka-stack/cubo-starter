package dev.dong4j.zeka.starter.rest.advice;

import dev.dong4j.zeka.kernel.web.exception.ServletGlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * REST 模块全局异常处理器
 *
 * 该类继承自 Servlet 全局异常处理器，专门用于处理 REST API 中发生的各种异常。
 * 作为框架的核心组件之一，它确保了所有未处理的异常都能被统一捕获并
 * 转换为统一的错误响应格式，提高系统的稳定性和用户体验。
 *
 * 主要功能：
 * 1. 继承父类的所有异常处理能力
 * 2. 专门针对 REST API 的异常处理场景进行优化
 * 3. 与 ResponseWrapperAdvice 配合，确保错误响应也遵循统一格式
 * 4. 提供统一的异常日志记录和错误码管理
 *
 * 处理范围：
 * - Spring MVC 框架异常（参数验证、类型转换等）
 * - 业务逻辑异常（自定义异常类）
 * - 系统级异常（数据库异常、网络异常等）
 * - HTTP 协议相关异常（404、5xx 等）
 *
 * 设计原则：
 * - 统一异常响应格式，方便前端处理
 * - 保护敏感信息，避免系统内部信息泄露
 * - 提供详细的错误日志，方便问题排查
 * - 支持国际化错误信息
 *
 * 使用参考：
 * - {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}
 * - {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}
 *
 * 扩展性：
 * 子类可以重写父类方法或添加新的异常处理方法，以支持特定的业务异常处理需求。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:23
 * @since 1.0.0
 */
@Slf4j
public class RestGlobalExceptionHandler extends ServletGlobalExceptionHandler {

    /**
     * 构造方法，初始化 REST 全局异常处理器
     *
     * 在实例化时记录加载日志，方便跟踪系统初始化过程。
     * 该日志可以帮助开发者确认全局异常处理器是否正确加载和配置。
     *
     * 加载过程：
     * 1. Spring 扫描并检测到该类
     * 2. 创建实例并注册为 Bean
     * 3. 调用构造方法并记录加载日志
     * 4. 与 Spring MVC 的异常处理机制集成
     *
     * 注意事项：
     * - 该构造方法不应包含复杂的初始化逻辑
     * - 所有重要的初始化工作都由父类完成
     * - 日志记录使用 INFO 级别，确保在正常运行时可以看到
     *
     * @since 1.0.0
     */
    public RestGlobalExceptionHandler() {
        log.info("加载全局异常处理器: [{}]", RestGlobalExceptionHandler.class);
    }
}
