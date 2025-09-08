package dev.dong4j.zeka.starter.rest.handler;

import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.exception.ExceptionInfo;
import dev.dong4j.zeka.kernel.common.exception.GlobalExceptionHandler;
import dev.dong4j.zeka.kernel.common.util.Exceptions;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

/**
 * WebFlux 环境下的自定义错误属性提取器
 *
 * 该类继承自 Spring Boot 的 DefaultErrorAttributes，提供了定制化的错误信息提取和格式化功能。
 * 它是 WebFlux 全局异常处理机制的核心组件之一，负责将各种异常转换为统一的错误响应格式。
 *
 * 主要功能：
 * 1. 自定义错误信息的提取和格式化逻辑
 * 2. 根据环境（开发/生产）提供不同详细程度的错误信息
 * 3. 统一所有错误的响应格式，符合框架的 {@link R} 结构
 * 4. 提供详细的异常上下文信息，便于问题排查
 * 5. 集成链路追踪信息，支持分布式环境下的问题定位
 *
 * 环境差异：
 * - 生产环境：返回简化的错误信息，保护系统内部细节
 * - 开发环境：返回详细的异常信息，包括堆栈跟踪、请求参数等
 *
 * 支持的异常类型：
 * - {@link ResponseStatusException}：HTTP 状态异常
 * - {@link WebExchangeBindException}：请求参数绑定异常
 * - 带有 @ResponseStatus 注解的自定义异常
 * - 其他所有未处理的异常
 *
 * 错误响应结构：
 * ```json
 * {
 *   "code": "错误码",
 *   "message": "错误消息",
 *   "success": false,
 *   "data": {
 *     "path": "请求路径",
 *     "method": "请求方法",
 *     "params": "请求参数",
 *     "headers": "请求头",
 *     "stackTrace": "堆栈跟踪（仅开发环境）",
 *     "traceId": "链路追踪ID",
 *     "hyperlink": "错误帮助链接"
 *   }
 * }
 * ```
 *
 * 设计特点：
 * - 基于 Spring WebFlux 响应式编程模型
 * - 与框架的全局异常处理器协同工作
 * - 提供丰富的异常上下文信息
 * - 支持分布式链路追踪
 * - 区分不同环境的信息暴露级别
 *
 * 使用方式：
 * 该类通常由 WebFlux 自动配置机制自动注册，与 {@link JsonErrorWebExceptionHandler}
 * 配合使用，形成完整的异常处理链路。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:23
 * @since 1.0.0
 */
public class ZekaWebfluxExceptionErrorAttributes extends DefaultErrorAttributes {

    /** 错误属性在 ServerWebExchange 中的存储键名 */
    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
    /** 是否包含异常详细信息的标志，通常在开发环境为 true，生产环境为 false */
    private final boolean includeException;

    /**
     * 构造函数，初始化错误属性提取器
     *
     * 该构造函数用于创建自定义的错误属性提取器实例。通过 includeException 参数
     * 可以控制是否在错误响应中包含详细的异常信息（如堆栈跟踪）。
     *
     * 使用场景：
     * - 开发环境：includeException = true，提供详细的调试信息
     * - 生产环境：includeException = false，只提供必要的错误信息，保护系统安全
     *
     * @param includeException 是否包含异常详细信息的标志
     *                        - true：包含堆栈跟踪等详细信息，适用于开发环境
     *                        - false：只包含基本错误信息，适用于生产环境
     * @since 1.0.0
     */
    public ZekaWebfluxExceptionErrorAttributes(boolean includeException) {
        this.includeException = includeException;
    }


    /**
     * 获取错误属性信息，支持选项控制
     * <p>
     * 重写父类方法，提供更灵活的错误属性获取机制。该方法根据传入的选项参数
     * 决定是否包含堆栈跟踪信息，并对返回的错误属性进行过滤。
     *
     * @param request 服务器请求对象，包含异常和请求上下文
     * @param options 错误属性选项，控制哪些信息需要包含在响应中
     * @return 包含错误信息的 Map 对象
     */
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));
        options.retainIncluded(errorAttributes);
        return errorAttributes;
}

/**
 * 根据环境配置生成适当的错误属性信息
 *
 * 该方法是自定义错误属性提取的核心逻辑。它会根据实例初始化时的
 * includeException 参数来决定返回什么级别的错误信息。
 *
 * 处理策略：
 * 1. 生产环境 (includeException = false)：
 *    - 只返回通用的错误信息，不包含具体的异常细节
 *    - 避免泄露数据库连接、文件路径等敏感信息
 *
 * 2. 开发环境 (includeException = true)：
 *    - 返回详细的异常信息，包括堆栈跟踪、请求参数等
 *    - 便于开发人员进行问题排查和调试
 *
 * @param request           服务器请求对象，包含异常和请求上下文
 * @param includeStackTrace 是否包含堆栈跟踪信息的标志
     * @return 格式化的错误属性 Map 对象
     * @since 1.0.0
     */

    private Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        // 根据是否为 prod 环境来获取 map, 不使用原来的配置
        if (!this.includeException) {
            // 当为生产环境,不适合把具体的异常信息展示给用户,比如数据库异常信息.
            return R.failMap(BaseCodes.FAILURE);
        }
        return this.buildExceptionErrorAttributes(request);
    }

/**
 * 构建开发环境下的详细异常信息
 *
 * 该方法专门用于开发环境，它会构建包含详细异常信息的响应对象。
 * 包括异常堆栈、请求参数、请求头等调试时非常有用的信息。
 *
 * 处理流程：
 * 1. 创建基础的失败响应结构
 * 2. 获取异常对象并决定 HTTP 状态码
 * 3. 构建详细的异常上下文数据
 * 4. 返回完整的错误响应
 *
 * @param webRequest 服务器请求对象，包含异常和请求上下文
     * @return 包含详细异常信息的 Map 对象
     * @since 1.0.0
     */
    @NotNull
    private Map<String, Object> buildExceptionErrorAttributes(ServerRequest webRequest) {
        Map<String, Object> result = R.failMap(BaseCodes.FAILURE);
        Throwable error = this.getError(webRequest);
        HttpStatus errorStatus = determineHttpStatus(error);
        addStatus(result, errorStatus);
        result.put(R.DATA, this.buildData(webRequest, error));
        return result;
    }

/**
 * 决定异常对应的 HTTP 状态码
 *
 * 该方法根据异常类型来决定适当的 HTTP 状态码。它支持多种方式的状态码映射：
 *
 * 支持的异常类型：
 * 1. {@link ResponseStatusException}：直接使用异常中的状态码
 * 2. 带有 @ResponseStatus 注解的异常：使用注解中定义的状态码
 * 3. 其他异常：默认返回 500 内部服务器错误
 *
 * @param error 需要分析的异常对象
     * @return 对应的 HTTP 状态码
     * @since 1.0.0
     */
    private static HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return HttpStatus.resolve(((ResponseStatusException) error).getStatusCode().value());
        }
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
            ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.code();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

/**
 * 向错误属性中添加 HTTP 状态信息
 *
 * 该方法负责在错误响应中添加状态码和状态描述信息。
 * 这些信息对于客户端理解错误类型非常重要。
 *
 * @param errorAttributes 需要填充状态信息的错误属性 Map
 * @param errorStatus     HTTP 状态对象，包含状态码和描述
     * @since 1.0.0
     */
    private static void addStatus(@NotNull Map<String, Object> errorAttributes, @NotNull HttpStatus errorStatus) {
        errorAttributes.put(R.CODE, errorStatus.value());
        errorAttributes.put(R.MESSAGE, errorStatus.getReasonPhrase());
    }

/**
 * 构建详细的异常上下文数据
 *
 * 该方法构建包含丰富上下文信息的异常数据对象，主要用于开发环境下的
 * 问题排查和调试。它会收集各种与请求和异常相关的信息。
 *
 * 包含的信息：
 * - 请求路径和方法
 * - 请求参数和请求头
 * - 异常类名和消息
 * - 客户端 IP 地址
 * - 链路追踪 ID
 * - 完整的堆栈跟踪信息
 * - 错误帮助文档链接
 *
 * @param webRequest 服务器请求对象，用于提取请求信息
 * @param error      异常对象，用于提取异常相关信息
 * @return 包含完整上下文信息的 ExceptionInfo 对象
     * @since 1.0.0
     */
    private Object buildData(@NotNull ServerRequest webRequest, Throwable error) {
        ServerHttpRequest serverHttpRequest = webRequest.exchange().getRequest();
        HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
        ExceptionInfo exceptionEntity = new ExceptionInfo();
        exceptionEntity.setPath(webRequest.path());
        exceptionEntity.setErrorMessage(determineMessage(error));
        exceptionEntity.setExceptionClass(error.getClass().getName());
        exceptionEntity.setParams(serverHttpRequest.getQueryParams().toSingleValueMap());
        exceptionEntity.setMethod(webRequest.method().name());
        exceptionEntity.setRemoteAddr(webRequest.remoteAddress().isPresent() ? webRequest.remoteAddress().get().getHostName() : "");
        exceptionEntity.setHeaders(httpHeaders.toSingleValueMap());
        exceptionEntity.setTraceId(Trace.context().get());
        exceptionEntity.setStackTrace(Exceptions.getStackTraceAsString(error));
        exceptionEntity.setHyperlink(GlobalExceptionHandler.buildErrorLink());
        this.addStackTrace(exceptionEntity, error);
        return exceptionEntity;
    }

/**
 * 决定异常的具体消息内容
 *
 * 该方法根据不同类型的异常提取适当的错误消息。它会针对常见的
 * Web 异常类型进行特殊处理，确保返回最有用的错误信息。
 *
 * 异常处理策略：
 * 1. {@link WebExchangeBindException}：请求参数绑定异常，返回详细的验证错误信息
 * 2. {@link ResponseStatusException}：HTTP 状态异常，返回异常中的原因描述
 * 3. 带有 @ResponseStatus 注解的异常：返回注解中定义的原因
 * 4. 其他异常：返回异常对象的 getMessage() 结果
 *
 * @param error 需要提取消息的异常对象
     * @return 格式化的错误消息字符串
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    private static String determineMessage(Throwable error) {
        if (error instanceof WebExchangeBindException) {
            return error.getMessage();
        }
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getReason();
        }
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
            ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.reason();
        }
        return error.getMessage();
    }

/**
 * 添加异常堆栈跟踪信息
 *
 * 该方法将异常的完整堆栈跟踪信息添加到异常上下文对象中。
 * 这对于开发环境下的问题定位非常有用，能够帮助开发人员快速
 * 找到异常的根源和调用链路。
 *
 * 注意：在生产环境中应该避免使用该方法，以防止泄露系统内部信息。
 *
 * @param exceptionEntity 异常上下文对象，用于存储堆栈跟踪信息
 * @param error           需要提取堆栈跟踪的异常对象
     * @since 1.0.0
     */
    @SuppressWarnings("all")
    private void addStackTrace(@NotNull ExceptionInfo exceptionEntity, @NotNull Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        exceptionEntity.setStackTrace(stackTrace.toString());
    }

/**
 * 从服务器请求中获取异常对象
 *
 * 重写父类方法，从 ServerWebExchange 的属性中提取存储的异常对象。
 * 该方法是错误处理流程中的关键绁节，它连接了异常的捕获和处理。
 *
 * 工作流程：
 * 1. 异常发生时，会通过 storeErrorInformation 方法存储到 ServerWebExchange
 * 2. 在错误处理阶段，通过该方法取回异常对象
 * 3. 如果没有找到异常对象，则抛出 IllegalStateException
 *
 * @param request 服务器请求对象，包含 ServerWebExchange 属性
 * @return 存储在请求中的异常对象
 * @throws IllegalStateException 如果没有找到异常对象
     * @since 1.0.0
     */
    @Override
    public Throwable getError(@NotNull ServerRequest request) {
        return (Throwable) request.attribute(ERROR_ATTRIBUTE)
            .orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
    }

/**
 * 在 ServerWebExchange 中存储异常信息
 *
 * 重写父类方法，将异常对象存储在 ServerWebExchange 的属性中。
 * 这是 WebFlux 异常处理流程的第一步，确保异常信息可以在后续的
 * 错误处理环节中被正确获取和处理。
 *
 * 工作机制：
 * 1. 使用 putIfAbsent 确保不覆盖已存在的异常信息
 * 2. 将异常对象以标准的键名存储在 ServerWebExchange 中
 * 3. 保持与 Spring 框架的异常处理机制的兼容性
 *
 * @param error    需要存储的异常对象
 * @param exchange 服务器 Web 交换对象，用于存储异常信息
     * @since 1.0.0
     */
    @Override
    public void storeErrorInformation(Throwable error, @NotNull ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
    }
}
