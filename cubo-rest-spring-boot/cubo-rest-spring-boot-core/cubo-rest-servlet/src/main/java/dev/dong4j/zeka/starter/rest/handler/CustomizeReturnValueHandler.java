package dev.dong4j.zeka.starter.rest.handler;

import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.asserts.Assertions;
import dev.dong4j.zeka.kernel.common.base.BasePage;
import dev.dong4j.zeka.kernel.common.constant.BasicConstant;
import dev.dong4j.zeka.kernel.common.util.DataTypeUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.starter.rest.advice.ResponseWrapperAdvice;
import dev.dong4j.zeka.starter.rest.annotation.OriginalResponse;
import dev.dong4j.zeka.starter.rest.utis.RestUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * 自定义返回值处理器
 *
 * 该类是 Spring MVC 的自定义返回值处理器，用于在响应返回给客户端之前对返回值进行预处理和格式化。
 * 它采用装饰器模式包装了 Spring 的 RequestResponseBodyMethodProcessor，在保持原有功能的基础上
 * 增加了自动数据包装、类型转换和格式统一等功能。
 *
 * 主要功能：
 * 1. 基础数据类型自动包装：将原始类型、String 等包装为统一的 Map 结构
 * 2. 分页数据处理：自动提取 BasePage 中的分页信息并返回
 * 3. 内容类型检测：根据 Content-Type 和 produces 配置决定是否进行包装
 * 4. 原始响应支持：通过 @OriginalResponse 注解跳过自动包装
 * 5. 字符串预处理：自动去除 String 类型返回值的首尾空白
 *
 * 处理优先级：
 * 该处理器的执行优先级在 {@link ResponseWrapperAdvice} 之前，这样可以在全局响应包装之前
 * 先对返回值进行预处理，确保数据格式的统一性。
 *
 * 包装条件：
 * 只有同时满足以下条件时才会进行数据包装：
 * 1. 方法上没有 @OriginalResponse 注解标识
 * 2. 未使用 Response 设置 Content-Type 或显式设置为 "application/json"
 * 3. 没有显式设置 produces 属性或 produces 为 "application/json"
 * 4. 返回值类型属于框架指定的包路径下的类
 * 5. 返回值类型支持响应通知处理
 *
 * 数据包装策略：
 * - 基础类型（int、long、String等）：包装为 {"value": data} 格式的 Map
 * - Result 类型：提取其中的 data 字段进行包装
 * - BasePage 类型：提取其中的分页信息（pagination）
 * - 其他复杂类型：保持原样，交由后续的 ResponseWrapperAdvice 处理
 *
 * 内容类型处理：
 * 1. 显式设置了 Content-Type：
 *    - 如果是 "application/json"：进行数据包装
 *    - 如果是其他类型：跳过包装，保持原样
 *
 * 2. 未设置 Content-Type：
 *    - 检查 @RequestMapping 的 produces 属性
 *    - 未设置或设置为 "application/json"：进行数据包装
 *    - 设置为其他类型：跳过包装
 *
 * 使用场景：
 * - 统一 Web 端和客户端的数据模型，确保 data 字段都是 Object 类型
 * - 简化前端数据处理，基础类型也能通过统一的 data 字段访问
 * - 支持分页数据的自动解包，减少前端处理复杂度
 * - 保持与现有 API 的兼容性，支持原始响应格式
 *
 * 设计特点：
 * - 装饰器模式：扩展而不修改 Spring 原有功能
 * - 条件化处理：只在需要时才进行数据包装
 * - 类型智能识别：根据返回值类型选择合适的处理策略
 * - 向后兼容：支持通过注解禁用自动包装
 *
 * 注意事项：
 * - 该处理器需要与 ResponseWrapperAdvice 配合使用
 * - 包装逻辑只对特定包路径下的类生效
 * - 显式的 Content-Type 设置优先级最高
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.12.14 10:39
 * @since 1.0.0
 */
@Slf4j
public class CustomizeReturnValueHandler implements HandlerMethodReturnValueHandler {
    /** 被装饰的目标处理器，Spring MVC 的标准请求响应体方法处理器 */
    private final RequestResponseBodyMethodProcessor target;

    /**
     * 构造函数，创建自定义返回值处理器
     *
     * 该构造函数接受一个 RequestResponseBodyMethodProcessor 实例作为装饰目标。
     * 通过装饰器模式，在保持原有处理器功能的基础上，增加了自定义的数据包装逻辑。
     *
     * @param target Spring MVC 的标准请求响应体方法处理器实例
     * @since 1.0.0
     */
    public CustomizeReturnValueHandler(RequestResponseBodyMethodProcessor target) {
        this.target = target;
    }

    /**
     * 检查是否支持处理指定的返回类型
     *
     * 该方法直接委托给被装饰的目标处理器来决定是否支持处理特定的返回类型。
     * 这样可以确保装饰器与原始处理器在支持的返回类型上保持一致。
     *
     * @param returnType 需要检查的返回值类型参数
     * @return 如果支持处理该返回类型则返回 true，否则返回 false
     * @since 1.0.0
     */
    @Override
    public boolean supportsReturnType(@NotNull MethodParameter returnType) {
        return this.target.supportsReturnType(returnType);
    }

    /**
     * 处理返回结果，执行自定义的数据包装逻辑
     *
     * 该方法是整个处理器的核心，负责在返回值被写入响应之前进行各种预处理操作。
     * 它的执行优先级在 {@link ResponseWrapperAdvice} 之前，主要用于处理基础数据类型的自动包装。
     *
     * 处理流程：
     * 1. 字符串预处理：如果返回值是 String，自动去除首尾空白
     * 2. 类型检查：检查返回类型是否属于框架指定的包路径且支持通知处理
     * 3. 注解检查：检查方法上是否有 @OriginalResponse 注解
     * 4. Content-Type 判断：根据响应的 Content-Type 决定是否进行包装
     * 5. Produces 判断：检查 @RequestMapping 的 produces 属性
     * 6. 数据包装：满足条件时调用 returnValueWrapper 进行包装
     * 7. 委托处理：最终调用目标处理器完成响应写入
     *
     * 包装条件组合：
     * - 必须同时满足：没有 @OriginalResponse 注解 + 类型检查通过 + Content-Type 或 Produces 检查通过
     *
     * Content-Type 处理优先级：
     * 1. 显式设置了 response.setContentType("application/json")：直接进行包装
     * 2. 显式设置了其他 Content-Type：不进行包装，忽略 produces 配置
     * 3. 未设置 Content-Type：检查 @RequestMapping 的 produces 属性
     *    - 未设置 produces 或 produces="application/json"：进行包装
     *    - 设置了其他 produces 值：不进行包装
     *
     * @param returnValue  需要处理的返回值
     * @param returnType   返回值的类型参数信息
     * @param mavContainer 模型和视图容器
     * @param webRequest   Web 请求对象
     * @throws Exception 处理过程中可能抛出的异常
     * @see ResponseWrapperAdvice#beforeBodyWrite
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("all")
    public void handleReturnValue(Object returnValue,
                                  @NotNull MethodParameter returnType,
                                  @NotNull ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest) throws Exception {

        Object finalReturnValue = returnValue;
        // 如果是 string 则删除首尾空格
        if (finalReturnValue instanceof String) {
            finalReturnValue = StringUtils.trimWhitespace((String) finalReturnValue);
        }

        if (RestUtils.zekaClass(returnType) && RestUtils.supportsAdvice(returnType)) {
            Method method = returnType.getMethod();
            Assertions.notNull(method);
            @SuppressWarnings("all") OriginalResponse originalResponse = method.getAnnotation(OriginalResponse.class);
            // 如果不存在此注解, 则需要再次判断
            if (originalResponse == null) {
                String contentType = Objects.requireNonNull(webRequest.getNativeResponse(HttpServletResponse.class)).getContentType();

                // 没有使用 response 写数据 或者显式设置了 Content-Type 为 application/json
                boolean contentTypeIsJson = StringUtils.isNotBlank(contentType)
                    && MediaType.parseMediaType(contentType).equalsTypeAndSubtype(MediaType.APPLICATION_JSON);

                // 显式使用 response.setContentType("application/json") 就优先使用
                if (contentTypeIsJson) {
                    finalReturnValue = this.returnValueWrapper(returnValue, returnType);
                } else if (StringUtils.isBlank(contentType)) {
                    // 如果没有使用 response.setContentType, 则判断 rest api 是否添加了 RequestMapping.produces, 且非 json 的不包装
                    RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    if (requestMapping != null) {
                        // 如果 rest api 上使用了 produces 属性, 则需要判断是否为 json
                        String[] serverProduces = requestMapping.produces();
                        String produceValue = serverProduces.length == 0 ? null
                            : emptyToNull(serverProduces[0]);
                        // 没有显式设置 produces 或 produces 为 json 则包装
                        if (produceValue == null
                            || MediaType.parseMediaType(produceValue).equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
                            finalReturnValue = this.returnValueWrapper(returnValue, returnType);
                        }
                    }

                } else {
                    // 只要使用了 response.setContentType 且不为 application/json 的, 就不再处理 produces, 优先使用显式设置的 Content-Type
                    log.trace("显式设置 Content-Type: {}", contentType);
                }
            }
        }

        this.target.handleReturnValue(finalReturnValue, returnType, mavContainer, webRequest);
    }

    /**
     * 包装返回值, 如果 returnValue 为 null 则直接返回.
     *
     * @param returnValue return value
     * @param returnType  return type
     * @return the object
     * @since 1.0.0
     */
    @Contract("null, _ -> null")
    private Object returnValueWrapper(Object returnValue, @NotNull MethodParameter returnType) {

        // 如果返回值为 null , 则交由 ResponseWrapperAdvice#beforeBodyWrite 处理
        if (returnValue == null) {
            return null;
        } else if (returnType.getMethod() != null && DataTypeUtils.isExtendPrimitive(returnType.getMethod())) {
            // 基础类型则包装为 map
            Map<String, Object> map = new HashMap<>(2);
            if (returnValue instanceof Result) {
                // 如果返回值是 Result，则需要取出 data 并封装为 Map
                map.put(BasicConstant.RESULT_WRAPPER_VALUE_KEY, ((Result<?>) returnValue).getData());
            } else {
                // 非 Result 的基础类型
                map.put(BasicConstant.RESULT_WRAPPER_VALUE_KEY, returnValue);
            }
            return map;
        } else {
            // 处理返回内容中包括 IPage 时返回实体格式
            return this.processBody(returnValue);
        }
    }

    /**
     * Empty to null
     *
     * @param string string
     * @return the string
     * @since 1.0.0
     */
    @Contract(value = "null -> null", pure = true)
    private static String emptyToNull(String string) {
        return string == null || string.isEmpty() ? null : string;
    }

    /**
     * 处理返回内容中包括 IPage 时返回实体格式
     *
     * @param body body
     * @return the object
     * @see ResponseWrapperAdvice#beforeBodyWrite
     * @since 1.0.0
     */
    private Object processBody(Object body) {
        Object result;
        Object data;
        if (body instanceof Result) {
            data = ((Result<?>) body).getData();
        } else {
            data = body;
        }
        // 判断 data 类型是否为 BasePage, 如果是，则返回 basePage.getPagination
        if (data instanceof BasePage) {
            result = ((BasePage<?>) data).getPagination();
        } else {
            // 如果解包后的数据或元素数据不是 BasePage, 则返回原始数据
            result = body;
        }
        return result;
    }

}
