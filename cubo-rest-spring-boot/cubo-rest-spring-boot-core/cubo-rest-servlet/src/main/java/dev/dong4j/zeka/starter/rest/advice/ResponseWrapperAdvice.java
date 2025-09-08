package dev.dong4j.zeka.starter.rest.advice;

import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.starter.rest.annotation.OriginalResponse;
import dev.dong4j.zeka.starter.rest.handler.CustomizeReturnValueHandler;
import dev.dong4j.zeka.starter.rest.utis.RestUtils;
import jakarta.servlet.Servlet;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 响应结果包装切面处理器
 *
 * 该类作为 Spring MVC 的响应体切面，负责将 Controller 方法的返回结果
 * 自动包装成统一的 Result 格式。这样可以确保所有 API 接口都返回一致的响应结构，
 * 方便前端处理和统一的错误处理。
 *
 * 主要功能：
 * 1. 自动检测需要包装的 Controller 方法
 * 2. 将方法返回值包装成统一的 Result 结构
 * 3. 支持通过 @OriginalResponse 注解跳过包装
 * 4. 智能检测已经被包装的结果，避免重复包装
 *
 * 触发条件：
 * - Controller 类使用了 @RestController、@RestControllerWrapper 或 @ResponseWrapper 注解
 * - Controller 方法使用了 @ResponseBody 或 @ResponseWrapper 注解
 * - 请求的 Content-Type 为 application/json
 * - 方法没有使用 @OriginalResponse 注解
 *
 * 不包装的场景：
 * - 使用 @OriginalResponse 注解的方法
 * - Content-Type 不是 application/json 的响应
 * - 返回的已经是 Result 类型的结果
 *
 * 包装结果：
 * - null 值包装为 R.succeed()
 * - 非 null 值包装为 R.succeed(data)
 * - 已经是 Result 类型的不再包装
 *
 * 性能考虑：
 * - 使用 ConditionalOnClass 注解确保只在 Servlet 环境下生效
 * - 通过缓存和早期返回减少不必要的处理
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 11:46
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class})
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {
    /**
     * 判断是否需要对返回结果进行包装
     *
     * 该方法作为切面的入口点，负责检查当前 Controller 方法是否需要进行结果包装。
     * 只有当此方法返回 true 时，才会触发 beforeBodyWrite 方法。
     *
     * 检查逻辑：
     * 1. 检查 Controller 类是否在框架的包路径下（避免对非目标类生效）
     * 2. 检查 Controller 或方法是否使用了支持的注解
     *
     * 支持的注解：
     * - @RestController：标准的 Spring REST 控制器注解
     * - @RestControllerWrapper：框架提供的组合注解
     * - @ResponseWrapper：显式指定需要包装的注解
     * - @ResponseBody：标准的 Spring 响应体注解
     *
     * 性能优化：
     * - 通过早期过滤减少不必要的处理开销
     * - 使用缓存机制提高判断效率
     *
     * @param returnType 方法返回类型参数，包含注解和类型信息
     * @param converterType HTTP 消息转换器类型，用于判断数据格式
     * @return true 表示需要包装，false 表示不需要包装
     * @since 1.0.0
     */
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return RestUtils.zekaClass(returnType) && RestUtils.supportsAdvice(returnType);
    }

    /**
     * 在响应体写入前对返回结果进行包装处理
     *
     * 该方法在 HTTP 响应体被写入之前被调用，负责将 Controller 方法的返回值
     * 包装成统一的 Result 格式。这是整个切面的核心处理逻辑。
     *
     * 包装条件检查：
     * 1. 检查方法是否使用了 @OriginalResponse 注解
     * 2. 检查 Content-Type 是否为 application/json
     * 3. 只有同时满足“未使用 @OriginalResponse”和“Content-Type 为 JSON”才进行包装
     *
     * 包装策略：
     * - null 值：包装为 R.succeed()，表示成功但无数据
     * - 非 null 值：包装为 R.succeed(data)，表示成功并返回数据
     * - 已经是 Result 类型：直接返回，不重复包装
     *
     * 日志记录：
     * - 使用 TRACE 级别记录包装过程，方便调试
     * - 区分已包装和需要包装的情况
     *
     * 跳过包装的场景：
     * - 使用 @OriginalResponse 注解的方法
     * - Content-Type 不是 application/json 的响应（如文件下载、图片返回等）
     *
     * @param body 原始的方法返回结果
     * @param returnType 方法返回类型参数
     * @param selectedContentType 选定的响应 Content-Type
     * @param selectedConverterType 选定的 HTTP 消息转换器类型
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 包装后的结果或原始结果
     * @see CustomizeReturnValueHandler#handleReturnValue
     * @since 1.0.0
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {

        OriginalResponse originalResponse = Objects.requireNonNull(returnType.getMethod()).getAnnotation(OriginalResponse.class);
        // 检查是否使用了 @OriginalResponse 注解以及 Content-Type 是否为 JSON
        if (originalResponse == null && selectedContentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
            // 没有使用 @OriginalResponse 注解且 Content-Type 为 application/json 时才包装
            return this.wrapper(body);
        }
        // 其他情况直接返回原始结果
        return body;
    }

    /**
     * 将原始返回结果包装成统一的 Result 格式
     *
     * 该方法实现了具体的包装逻辑，将不同类型的返回结果统一包装成 Result 类型。
     * 通过智能判断和分类处理，确保包装结果的一致性和正确性。
     *
     * 处理策略：
     * 1. 已经是 Result 类型：直接返回，避免重复包装
     * 2. null 值：包装为 R.succeed()，表示成功但无返回数据
     * 3. 非 null 值：包装为 R.succeed(data)，表示成功并返回数据
     *
     * 日志记录：
     * - 使用 TRACE 级别记录包装过程，不影响正常性能
     * - 区分“已包装”和“重新包装”的日志信息
     * - 记录原始响应内容，方便问题排查
     *
     * 返回结果：
     * - 始终返回非 null 的 Result 对象
     * - 保证前端可以统一处理 API 响应结构
     *
     * 性能考虑：
     * - 早期识别已包装的结果，减少不必要的对象创建
     * - 使用简单的类型检查，避免复杂的反射操作
     *
     * @param body 原始的方法返回结果，可能为 null
     * @return 包装后的 Result 对象，不会为 null
     * @since 1.0.0
     */
    @NotNull
    private Object wrapper(Object body) {
        if (body instanceof Result) {
            // 已经是 Result 类型，直接返回，避免重复包装
            log.trace("请求响应已使用 Result 包装, 原始响应: [{}]", body);
            return body;
        } else {
            if (body == null) {
                // null 值包装为成功但无数据的结果
                return R.succeed();
            }
            // 非 null 值包装为成功并返回数据的结果
            log.trace("重写请求响应, 使用 Result 包装, 原始响应: [{}]", body);
            return R.succeed(body);
        }
    }
}
