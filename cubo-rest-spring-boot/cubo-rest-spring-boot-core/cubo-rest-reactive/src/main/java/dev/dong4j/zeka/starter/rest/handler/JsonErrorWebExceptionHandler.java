package dev.dong4j.zeka.starter.rest.handler;

import com.google.common.collect.Maps;
import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.context.GlobalContext;
import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.enums.ZekaEnv;
import dev.dong4j.zeka.kernel.common.exception.ExceptionInfo;
import dev.dong4j.zeka.kernel.common.exception.GlobalExceptionHandler;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.starter.rest.ReactiveConstants;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * JSON 格式全局异常处理器
 *
 * 该类是 WebFlux 环境下的自定义全局异常处理器，用于统一处理和包装所有未被捕获的异常。
 * 继承自 Spring Boot 的 DefaultErrorWebExceptionHandler，提供了更符合业务需求的异常响应格式。
 *
 * 主要功能：
 * 1. 统一异常响应格式，所有异常都按照 {@link R} 格式返回
 * 2. 区分开发环境和生产环境，提供不同详细程度的错误信息
 * 3. 特殊处理网关相关异常（如路由不存在、服务不可用等）
 * 4. 自动添加链路追踪 ID，便于问题排查
 * 5. 记录详细的请求信息，包括路径、参数、请求头等
 *
 * 支持的异常类型：
 * - {@link LowestException}：框架自定义异常，直接返回异常中的错误码和消息
 * - Gateway NotFoundException：网关找不到服务实例异常
 * - {@link ResponseStatusException}：HTTP 状态异常，通常是路由配置问题
 * - 其他所有异常：统一处理为服务器内部错误
 *
 * 响应格式：
 * ```json
 * {
 *   "code": "错误码",
 *   "message": "错误信息",
 *   "success": false,
 *   "data": {},
 *   "traceId": "链路追踪ID",
 *   "extend": "扩展信息（仅开发环境）"
 * }
 * ```
 *
 * 环境差异：
 * - 生产环境：只返回通用的服务器繁忙提示，保护系统内部信息
 * - 开发环境：返回详细的异常信息和请求上下文，便于调试
 *
 * 设计特点：
 * - 基于 Spring WebFlux 反应式编程模型
 * - 返回 JSON 格式响应，HTTP 状态码统一为 200
 * - 集成链路追踪，自动关联请求上下文
 * - 支持网关场景下的特殊异常处理
 *
 * 注意事项：
 * - 该处理器只在 WebFlux 环境下生效
 * - 需要配合 {@link ZekaWebfluxExceptionErrorAttributes} 使用
 * - TODO: 尚未处理 Sentinel 流控异常
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:24
 * @since 1.0.0
 */
@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    /** 能找到路由配置, 但是对应的服务不存在 */
    private static final String GATEWAY_NOTFOUNDEXCEPTION = "org.springframework.cloud.gateway.support.NotFoundException";

    /**
     * 构造函数，初始化 JSON 异常处理器
     *
     * 该构造函数用于创建自定义的全局异常处理器实例，需要传入必要的
     * Spring Boot 错误处理相关组件。这些组件将用于提取异常信息、
     * 配置错误处理行为等。
     *
     * @param errorAttributes    错误属性提取器，用于从异常中提取标准错误信息
     * @param resourceProperties 静态资源配置属性，用于错误页面等资源处理
     * @param errorProperties    错误处理配置属性，控制错误信息的显示级别
     * @param applicationContext Spring 应用上下文，用于访问其他 Bean
     * @since 1.0.0
     */
    public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                        WebProperties.Resources resourceProperties,
                                        ErrorProperties errorProperties,
                                        ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取错误属性信息
     *
     * 重写父类方法，提供更灵活的错误属性提取机制。该方法根据传入的选项
     * 决定是否包含堆栈跟踪信息，并过滤掉不需要的属性。
     *
     * @param request 服务器请求对象，包含请求上下文信息
     * @param options 错误属性选项，控制哪些信息需要包含在响应中
     * @return 包含错误信息的 Map 对象
     */
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));
        options.retainIncluded(errorAttributes);
        return errorAttributes;
    }

    /**
     * 获取异常属性并根据环境包装异常信息
     *
     * 该方法是异常信息处理的核心逻辑，负责创建统一的错误响应结构。
     * 它会初始化基本的响应字段，包括数据容器和链路追踪 ID，然后
     * 调用具体的响应构建方法来完善错误信息。
     *
     * @param request           服务器请求对象，包含异常和请求上下文
     * @param includeStackTrace 是否包含堆栈跟踪信息的标志
     * @return 包含完整错误信息的 Map 对象
     * @since 1.0.0
     */
    private Map<String, Object> getErrorAttributes(@NotNull ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(4);
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(2);
        map.put(R.DATA, data);
        map.put(R.TRACE_ID, Trace.context().get());
        return this.response(request, map);
    }

    /**
     * 生产环境统一返回服务器繁忙错误
     *
     * 该方法用于在生产环境下统一处理所有异常，不暴露具体的异常信息，
     * 只返回通用的服务器繁忙提示。这样可以保护系统内部信息安全，
     * 避免向外部泄露敏感的技术细节。
     *
     * @param map 要填充错误信息的 Map 对象
     * @return 填充了标准错误信息的 Map 对象
     * @since 1.0.0
     */
    @Contract("_ -> param1")
    @NotNull
    private static Map<String, Object> response(@NotNull Map<String, Object> map) {
        map.put(R.CODE, BaseCodes.SERVER_BUSY.getCode());
        map.put(R.MESSAGE, BaseCodes.SERVER_BUSY.getMessage());
        map.put(R.SUCCESS, false);
        return map;
    }

    /**
     * 开发环境下构建详细的错误响应信息
     *
     * 该方法为开发环境提供详细的错误信息，包括异常堆栈、请求参数、
     * 请求头等调试信息。同时对不同类型的异常进行特殊处理：
     *
     * 异常处理策略：
     * 1. {@link LowestException}：框架自定义异常，直接使用其错误码和消息
     * 2. Gateway NotFoundException：网关找不到服务实例异常
     * 3. {@link ResponseStatusException}：路由配置错误异常
     * 4. 其他异常：使用默认的服务不可用错误码
     *
     * 环境分支：
     * - 生产环境：记录真实错误到日志，但返回统一的服务器繁忙提示
     * - 开发环境：返回详细的错误信息和请求上下文
     *
     * @param request 服务器请求对象，包含异常和请求信息
     * @param map     需要填充错误信息的 Map 对象
     * @return 填充完成的错误响应 Map 对象
     * @since 1.0.0
     */
    @Contract("_, _ -> param2")
    @NotNull
    private Map<String, Object> response(ServerRequest request, @NotNull Map<String, Object> map) {
        Throwable error = super.getError(request);
        String errorMessage = buildMessage(request, error);
        map.put(R.CODE, request.attribute(R.CODE).orElse(HttpStatus.SERVICE_UNAVAILABLE.value()));
        map.put(R.MESSAGE, errorMessage);
        map.put(R.SUCCESS, false);

        if (error instanceof LowestException LowestException) {
            // 捕获自定义异常
            map.put(R.CODE, LowestException.getResultCode());
            map.put(R.MESSAGE, LowestException.getMessage());
            errorMessage = LowestException.getMessage();
        } else if (GATEWAY_NOTFOUNDEXCEPTION.equals(error.getClass().getName())) {
            // 能找到路由配置, 但是对应的服务不存在 (未在注册中心找到服务)
            map.put(R.CODE, BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getCode());
            map.put(R.MESSAGE, BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getMessage());
            errorMessage = BaseCodes.GATEWAY_NOT_FUND_INSTANCES_ERROR.getMessage();
        } else if (error instanceof ResponseStatusException exception) {
            // 路由配置不正确, 导致直接调用 gateway 的 rest 接口, 而 gateway 根本没有这个接口
            map.put(R.CODE, BaseCodes.GATEWAY_ROUTER_ERROR.getCode());
            map.put(R.MESSAGE, BaseCodes.GATEWAY_ROUTER_ERROR.getMessage() + ": " + exception.getMessage());
            errorMessage = BaseCodes.GATEWAY_ROUTER_ERROR.getMessage() + ": " + exception.getMessage();
        }

        // 日志输出真实的错误信息,
        if (ZekaEnv.PROD.equals(ConfigKit.getEnv())) {
            log.error("code: [{}], errorMessage: [{}]", map.get(R.CODE), errorMessage);
            map.put(R.CODE, BaseCodes.SERVER_BUSY.getCode());
            map.put(R.MESSAGE, BaseCodes.SERVER_BUSY.getMessage());
            return map;
        }

        ServerHttpRequest serverHttpRequest = request.exchange().getRequest();
        HttpHeaders httpHeaders = serverHttpRequest.getHeaders();

        ExceptionInfo exceptionEntity = new ExceptionInfo();
        exceptionEntity.setExceptionClass(error.getClass().getName());
        exceptionEntity.setPath(request.path());
        exceptionEntity.setMethod(request.method().name());
        exceptionEntity.setTraceId(StringUtils.isBlank(Trace.context().get()) ? StringUtils.getUid() : Trace.context().get());
        Optional<InetSocketAddress> inetSocketAddress = request.remoteAddress();
        String hostName = "";
        if (inetSocketAddress.isPresent()) {
            hostName = inetSocketAddress.get().getHostName();
        }
        exceptionEntity.setRemoteAddr(hostName);
        exceptionEntity.setParams(serverHttpRequest.getQueryParams().toSingleValueMap());
        exceptionEntity.setHeaders(httpHeaders.toSingleValueMap());
        exceptionEntity.setHyperlink(GlobalExceptionHandler.buildErrorLink());
        map.put(R.EXTEND, exceptionEntity);
        map.put(R.DATA, Collections.emptyMap());
        return map;
    }

    /**
     * 构建异常信息描述
     *
     * 该方法用于构建统一格式的异常信息描述，包含请求方法、路径和具体的
     * 异常消息。这样的标准化格式有助于开发人员快速定位问题。
     *
     * 生成的消息格式为：
     * "Failed to handle request [GET /api/users]: 具体异常消息"
     *
     * @param request 服务器请求对象，用于获取请求方法和路径
     * @param ex      异常对象，可以为 null
     * @return 格式化的异常信息字符串
     * @since 1.0.0
     */
    @NotNull
    private static String buildMessage(@NotNull ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.method().name());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }

    /**
     * 指定响应处理方法为 JSON 格式处理
     *
     * 重写父类方法，将默认的 HTML 错误页面响应改为 JSON 格式响应。
     * 这样可以确保所有的错误响应都采用统一的 JSON 格式，
     * 方便前端和其他客户端程序处理。
     *
     * @param errorAttributes 错误属性提取器，用于获取异常相关信息
     * @return 路由函数，所有错误请求都会被它处理
     * @since 1.0.0
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 输出 JSON 格式的错误响应
     *
     * 该方法是最终的错误响应渲染方法，负责将错误信息转换为 JSON 格式
     * 并返回给客户端。该方法会：
     *
     * 1. 决定是否包含堆栈跟踪信息
     * 2. 获取完整的错误属性信息
     * 3. 为错误码添加特定的前缀 "S.G-"（Server Gateway）
     * 4. 记录详细的错误日志，包括网关路由信息
     * 5. 将 HTTP 状态码设置为 200，使用响应体判断请求是否成功
     *
     * 这种设计符合 RESTful API 的最佳实践，即使出现错误也保持 HTTP 通信的成功，
     * 具体的错误信息通过响应体中的 success 字段和错误码来传达。
     *
     * @param request 服务器请求对象，包含异常和请求上下文
     * @return 包装了 JSON 响应的 Mono 对象
     * @since 1.0.0
     */
    @Override
    protected @NotNull Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = this.isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = this.getErrorAttributes(request, includeStackTrace);
        error.put(R.CODE, "S.G-" + error.get(R.CODE));
        log.error("router: [{}] \n{}", GlobalContext.get(ReactiveConstants.GATEWAY_ROUTER), Jsons.toJson(error, true));
        // 修改 http 状态为 200, 使用 body 判断请求是否成功
        return ServerResponse.status(200)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(error));
    }

}
