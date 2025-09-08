package dev.dong4j.zeka.starter.rest.handler;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.common.api.IResultCode;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.exception.ExceptionInfo;
import dev.dong4j.zeka.kernel.common.exception.GlobalExceptionHandler;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.util.Exceptions;
import dev.dong4j.zeka.starter.rest.advice.RestGlobalExceptionHandler;
import jakarta.servlet.ServletException;
import java.util.Iterator;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Servlet 环境下的自定义错误属性提取器
 * <p>
 * 该类继承自 Spring Boot 的 DefaultErrorAttributes，为传统的 Servlet Web 环境提供定制化的错误信息提取和格式化功能。
 * 它是 Servlet 全局异常处理机制的核心组件之一，负责将各种异常转换为统一的错误响应格式。
 * <p>
 * 主要功能：
 * 1. 自定义错误信息的提取和格式化逻辑
 * 2. 根据环境（开发/生产）提供不同详细程度的错误信息
 * 3. 统一所有错误的响应格式，符合框架的 {@link R} 结构
 * 4. 提供详细的异常上下文信息，便于问题排查
 * 5. 集成链路追踪信息，支持分布式环境下的问题定位
 * <p>
 * 环境差异：
 * - 生产环境：返回简化的错误信息，保护系统内部细节
 * - 开发环境：返回详细的异常信息，包括堆栈跟踪、请求参数等
 * <p>
 * 支持的异常类型：
 * - {@link LowestException}：框架自定义异常，直接返回异常中的错误码和消息
 * - {@link MethodArgumentNotValidException}：请求参数验证异常
 * - {@link ServletException}：Servlet 容器异常
 * - 其他所有未处理的异常：统一处理为服务器内部错误
 * <p>
 * 错误响应结构：
 * ```json
 * {
 * "code": "错误码",
 * "message": "错误消息",
 * "success": false,
 * "data": {
 * "path": "请求路径",
 * "method": "请求方法",
 * "params": "请求参数",
 * "headers": "请求头",
 * "stackTrace": "堆栈跟踪（仅开发环境）",
 * "traceId": "链路追踪ID",
 * "hyperlink": "错误帮助链接"
 * }
 * }
 * ```
 * <p>
 * 设计特点：
 * - 基于传统的 Servlet API，适用于 Spring MVC 环境
 * - 与框架的全局异常处理器协同工作
 * - 提供丰富的异常上下文信息
 * - 支持分布式链路追踪
 * - 区分不同环境的信息暴露级别
 * <p>
 * 使用方式：
 * 该类通常由 Servlet 自动配置机制自动注册，与 {@link RestGlobalExceptionHandler}
 * 配合使用，形成完整的异常处理链路。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:22
 * @since 1.0.0
 */
@Slf4j
public class ZekaServletExceptionErrorAttributes extends DefaultErrorAttributes {

    /** 是否包含异常详细信息的标志，通常在开发环境为 true，生产环境为 false */
    private final boolean includeException;

    /**
     * 构造函数，初始化错误属性提取器
     * <p>
     * 该构造函数用于创建自定义的错误属性提取器实例。通过 includeException 参数
     * 可以控制是否在错误响应中包含详细的异常信息（如堆栈跟踪）。
     * <p>
     * 使用场景：
     * - 开发环境：includeException = true，提供详细的调试信息
     * - 生产环境：includeException = false，只提供必要的错误信息，保护系统安全
     *
     * @param includeException 是否包含异常详细信息的标志
     *                         - true：包含堆栈跟踪等详细信息，适用于开发环境
     *                         - false：只包含基本错误信息，适用于生产环境
     * @since 1.0.0
     */
    public ZekaServletExceptionErrorAttributes(boolean includeException) {
        this.includeException = includeException;
    }

    /**
     * 获取错误属性信息，支持选项控制
     * <p>
     * 重写父类方法，提供更灵活的错误属性获取机制。该方法根据传入的选项参数
     * 决定是否包含堆栈跟踪信息，并对返回的错误属性进行过滤。
     *
     * @param webRequest 服务器 Web 请求对象，包含异常和请求上下文
     * @param options    错误属性选项，控制哪些信息需要包含在响应中
     * @return 包含错误信息的 Map 对象
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = this.getErrorAttributes(webRequest, options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));
        options.retainIncluded(errorAttributes);
        return errorAttributes;
    }

    /**
     * 根据环境配置生成适当的错误属性信息
     * <p>
     * 该方法是自定义错误属性提取的核心逻辑。它会根据实例初始化时的
     * includeException 参数来决定返回什么级别的错误信息。
     * <p>
     * 处理策略：
     * 1. 生产环境 (includeException = false)：
     * - 先检查是否为框架自定义异常 (LowestException)
     * - 如果是，返回异常中的错误码和消息
     * - 否则返回通用的失败信息，不暴露具体的异常细节
     * <p>
     * 2. 开发环境 (includeException = true)：
     * - 返回详细的异常信息，包括堆栈跟踪、请求参数等
     * - 便于开发人员进行问题排查和调试
     *
     * @param webRequest        服务器 Web 请求对象，包含异常和请求上下文
     * @param includeStackTrace 是否包含堆栈跟踪信息的标志
     * @return 格式化的错误属性 Map 对象
     * @since 1.0.0
     */

    private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        // 根据是否为 prod 环境来获取 map, 不使用原来的配置
        if (!this.includeException) {
            Throwable error = this.getError(webRequest);
            // 如果是系统自定义异常
            if (error instanceof LowestException) {
                return R.failMap(((LowestException) error).getResultCode());
            }
            return R.failMap(BaseCodes.FAILURE);
        }
        return this.buildExceptionErrorAttributes(webRequest);
    }

    /**
     * 构建开发环境下的详细异常信息
     * <p>
     * 该方法专门用于开发环境，它会构建包含详细异常信息的响应对象。
     * 包括异常堆栈、请求参数、请求头等调试时非常有用的信息。
     * <p>
     * 处理流程：
     * 1. 创建基础的失败响应结构
     * 2. 添加状态码和错误消息
     * 3. 构建详细的异常上下文数据
     * 4. 返回完整的错误响应
     *
     * @param webRequest 服务器 Web 请求对象，包含异常和请求上下文
     * @return 包含详细异常信息的 Map 对象
     * @since 1.0.0
     */
    @NotNull
    private Map<String, Object> buildExceptionErrorAttributes(WebRequest webRequest) {
        Map<String, Object> result = R.failMap(BaseCodes.FAILURE);
        this.addStatus(result, webRequest);
        result.put(R.DATA, this.buildData(webRequest));
        return result;
    }

    /**
     * 添加状态码和错误消息信息
     * <p>
     * 该方法负责在错误响应中添加适当的错误码和消息。它会优先处理
     * 框架自定义异常，然后才处理 HTTP 状态码相关的错误。
     * <p>
     * 处理优先级：
     * 1. 直接的 LowestException 异常：直接使用异常中的错误码和消息
     * 2. 嵌套的 LowestException 异常：提取内层异常的错误信息
     * 3. HTTP 状态码错误：使用 Servlet 属性中的状态码和消息
     *
     * @param errorAttributes 需要填充错误信息的属性 Map
     * @param webRequest      Web 请求对象，用于获取异常和 Servlet 属性
     * @since 1.0.0
     */
    private void addStatus(Map<String, Object> errorAttributes, WebRequest webRequest) {
        IResultCode resultCode = null;
        Throwable error = this.getError(webRequest);
        if (error != null) {
            log.debug("处理异常信息: [{}]", error.getClass());
            // 如果是系统自定义异常
            if (error instanceof LowestException) {
                resultCode = ((LowestException) error).getResultCode();
            }
            if (error.getCause() instanceof LowestException) {
                resultCode = ((LowestException) error.getCause()).getResultCode();
            }
        }

        if (resultCode != null) {
            errorAttributes.put(R.CODE, resultCode.getCode());
            errorAttributes.put(R.MESSAGE, resultCode.getMessage());
        } else {
            Integer code = this.getAttribute(webRequest, "javax.servlet.error.status_code");
            errorAttributes.put(R.CODE, code);
            try {
                errorAttributes.put(R.MESSAGE, HttpStatus.valueOf(code).getReasonPhrase());
            } catch (Exception ex) {
                errorAttributes.put(R.MESSAGE, this.getAttribute(webRequest, "javax.servlet.error.message"));
            }
        }
    }

    /**
     * 构建详细的异常上下文数据
     * <p>
     * 该方法构建包含丰富上下文信息的异常数据对象，主要用于开发环境下的
     * 问题排查和调试。它会收集各种与 HTTP 请求和异常相关的信息。
     * <p>
     * 包含的信息：
     * - HTTP 请求方法和路径
     * - 请求参数和请求头
     * - 异常类名和消息
     * - 远程客户端信息
     * - 链路追踪 ID
     * - 完整的堆栈跟踪信息
     * - 错误帮助文档链接
     * <p>
     * 异常处理：
     * - 自动解包嵌套的 LowestException 和 ServletException
     * - 支持参数验证异常的特殊处理
     * - 提取原始异常的最终原因
     *
     * @param webRequest Web 请求对象，用于提取请求信息
     * @return 包含完整上下文信息的 ExceptionInfo 对象
     * @since 1.0.0
     */
    private @NotNull Object buildData(@NotNull WebRequest webRequest) {
        ExceptionInfo exceptionEntity = new ExceptionInfo();
        HttpMethod httpMethod = ((ServletWebRequest) webRequest).getHttpMethod();
        String method = httpMethod.name();
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(16);
        Iterator<String> headerNames = webRequest.getHeaderNames();
        while (headerNames.hasNext()) {
            String headerName = headerNames.next();
            headers.put(headerName, webRequest.getHeader(headerName));
        }
        exceptionEntity.setMethod(method);
        exceptionEntity.setPath(this.fetchPath(webRequest));
        exceptionEntity.setParams(webRequest.getParameterMap());
        exceptionEntity.setRemoteAddr(webRequest.getRemoteUser());
        exceptionEntity.setHeaders(headers);
        exceptionEntity.setTraceId(Trace.context().get());
        exceptionEntity.setHyperlink(GlobalExceptionHandler.buildErrorLink());

        Throwable error = this.getError(webRequest);
        if (error != null) {
            while ((error instanceof LowestException || error instanceof ServletException) && error.getCause() != null) {
                error = error.getCause();
            }
            exceptionEntity.setExceptionClass(error.getClass().getName());
            this.addErrorMessage(exceptionEntity, error);
            exceptionEntity.setStackTrace(Exceptions.getStackTraceAsString(error));
        }
        return exceptionEntity;
    }

    /**
     * 从请求属性中获取指定的属性值
     * <p>
     * 该方法为获取 Servlet 请求属性提供了类型安全的包装。
     * 它能够从请求作用域中获取各种 Servlet 相关的属性信息。
     * <p>
     * 常用的 Servlet 属性：
     * - javax.servlet.error.status_code：HTTP 状态码
     * - javax.servlet.error.message：错误消息
     * - javax.servlet.error.request_uri：请求 URI
     * - javax.servlet.error.exception：异常对象
     *
     * @param <T>               返回值的泛型参数
     * @param requestAttributes 请求属性对象
     * @param name              要获取的属性名称
     * @return 指定属性的值，可能为 null
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private <T> T getAttribute(@NotNull RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 从请求属性中获取请求路径
     * <p>
     * 该方法从 Servlet 错误属性中提取当前请求的 URI 路径。
     * 如果无法获取到路径信息，则返回空字符串作为默认值。
     * <p>
     * 这个路径信息对于问题排查非常有用，能够帮助开发人员
     * 快速定位出现错误的具体 API 接口。
     *
     * @param requestAttributes 请求属性对象，包含 Servlet 相关信息
     * @return 请求的 URI 路径，不为 null
     * @since 1.0.0
     */
    private @NotNull String fetchPath(RequestAttributes requestAttributes) {
        String path = this.getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        return path != null ? path : "";
    }

    /**
     * 添加错误消息到异常上下文中
     * <p>
     * 该方法负责从异常对象中提取有意义的错误消息。它会特别处理
     * 参数验证异常，提供更加详细和有用的错误信息。
     * <p>
     * 处理策略：
     * 1. 如果存在参数验证错误：
     * - 提取验证结果中的详细信息
     * - 包括验证失败的对象名和错误数量
     * <p>
     * 2. 如果是其他类型的异常：
     * - 直接使用异常的 getMessage() 方法
     *
     * @param exceptionEntity 异常上下文对象，用于存储错误消息
     * @param error           需要处理的异常对象
     * @since 1.0.0
     */
    private void addErrorMessage(ExceptionInfo exceptionEntity, Throwable error) {
        BindingResult result = this.extractBindingResult(error);
        if (result == null) {
            exceptionEntity.setErrorMessage(error.getMessage());
            return;
        }
        if (result.hasErrors()) {
            exceptionEntity.setErrorMessage("Validation failed for object='"
                + result.getObjectName()
                + "'. Error count: "
                + result.getErrorCount());
        } else {
            exceptionEntity.setErrorMessage("No errors");
        }
    }

    /**
     * 从异常中提取参数验证结果
     * <p>
     * 该方法尝试从异常对象中提取参数验证的结果信息。
     * 它支持多种类型的验证异常，为上层方法提供统一的处理接口。
     * <p>
     * 支持的异常类型：
     * 1. {@link MethodArgumentNotValidException}：请求参数验证异常
     * - 通常在 @Valid 注解验证失败时抛出
     * - 包含详细的字段验证错误信息
     * <p>
     * 2. 直接的 {@link BindingResult} 实例：
     * - 某些情况下异常对象本身就实现了 BindingResult 接口
     *
     * @param error 需要分析的异常对象
     * @return 参数验证结果，如果不是验证异常则返回 null
     * @since 1.0.0
     */
    @Contract("null -> null")
    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        return null;
    }

}
