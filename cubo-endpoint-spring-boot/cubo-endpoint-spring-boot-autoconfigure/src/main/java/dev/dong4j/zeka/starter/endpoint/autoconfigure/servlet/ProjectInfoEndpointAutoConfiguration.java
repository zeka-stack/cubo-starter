package dev.dong4j.zeka.starter.endpoint.autoconfigure.servlet;

import com.google.common.collect.Lists;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 项目信息端点自动配置类
 *
 * 该类提供了获取应用中所有接口信息的 REST 端点。
 * 主要功能包括：
 *
 * 1. 扫描和收集应用中所有的 RequestMapping 信息
 * 2. 提供接口的详细信息，包括 URL、HTTP 方法、参数类型等
 * 3. 过滤掉默认的系统接口，只显示业务相关的接口
 * 4. 支持 Spring Boot 2.x 和 3.x 的兼容性
 *
 * 仅在 Servlet Web 环境下生效，适用于传统的 Spring MVC 应用。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:12
 * @since 1.0.0
 */
@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Tag(name = "应用接口信息")
public class ProjectInfoEndpointAutoConfiguration {
    /** Spring Web 应用上下文，用于获取请求处理器映射 */
    @Resource
    private WebApplicationContext applicationContext;
    /** 路径匹配器，用于过滤默认系统接口 */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 获取所有应用接口信息
     *
     * 扫描和收集应用中所有的 RequestMapping 信息，包括：
     * - HTTP 请求方法（GET、POST 等）
     * - 请求 URL 路径
     * - Controller 类名和方法名
     * - 方法参数类型
     *
     * 会过滤掉默认的系统接口，只返回业务相关的接口信息。
     * 支持 Spring Boot 2.x 和 3.x 的兼容性处理。
     *
     * @return 包含所有接口信息的响应结果
     * @since 1.0.0
     */
    @Operation(summary = "获取应用所有接口")
    @GetMapping(value = "/request-urls")
    public Result<List<RequestToMethodItem>> index() {
        // 创建结果列表
        List<RequestToMethodItem> list = Lists.newArrayList();
        // 获取 Spring MVC 的请求处理器映射
        RequestMappingHandlerMapping requestMappingHandlerMapping =
            this.applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取所有的处理器方法
        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
            requestMappingHandlerMapping.getHandlerMethods();

        // 遍历所有的处理器方法
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            // 获取 HTTP 请求类型 (GET, POST 等)
            RequestMethodsRequestCondition methodCondition = requestMappingInfo.getMethodsCondition();
            String requestType = methodCondition.getMethods().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

            // 兼容 Spring Boot 2.x 和 3.x 的 URL 路径解析
            String requestUrl;
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            if (patternsCondition != null) {
                // Spring 5.x / Boot 2.x 的处理方式
                requestUrl = patternsCondition.getPatterns().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            } else if (requestMappingInfo.getPathPatternsCondition() != null) {
                // Spring 6.x / Boot 3.x 的处理方式
                requestUrl = requestMappingInfo.getPathPatternsCondition().getPatterns().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            } else {
                // 默认情况
                requestUrl = "";
            }

            // 获取方法参数类型数组
            Class<?>[] methodParamTypes = handlerMethod.getMethod().getParameterTypes();

            // 过滤掉默认跳过的系统 URL（如 Actuator 端点等）
            boolean allMatch = SecurityUtils.DEFAULT_SKIP_URL.stream()
                .allMatch(pattern -> this.pathMatcher.matchStart(pattern, requestUrl));
            if (allMatch) {
                // 跳过系统默认接口
                continue;
            }

            // 获取 Controller 类的完整名称
            String controllerName = handlerMethod.getBeanType().toString();
            // 获取处理方法名
            String requestMethodName = handlerMethod.getMethod().getName();

            // 构建接口信息对象
            RequestToMethodItem item = RequestToMethodItem.builder()
                .requestType(requestType)
                .requestUrl(requestUrl)
                .controllerName(controllerName)
                .requestMethodName(requestMethodName)
                .methodParamTypes(methodParamTypes)
                .build();

            // 添加到结果列表
            list.add(item);
        }
        // 返回成功响应
        return R.succeed(list);
    }

    /**
     * 请求到方法的映射信息对象
     *
     * 该内部类用于封装单个接口的详细信息，包括：
     * - HTTP 请求方法（GET、POST 等）
     * - 请求 URL 路径
     * - Controller 类的完整名称
     * - 处理方法名
     * - 方法参数类型数组
     *
     * 使用 Lombok 注解简化代码，支持构建者模式和数据访问。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.10 11:42
     * @since 1.0.0
     */
    @Data
    @Builder
    public static class RequestToMethodItem {
        /** HTTP 请求类型（GET、POST、PUT、DELETE 等） */
        private String requestType;
        /** 请求 URL 路径 */
        private String requestUrl;
        /** Controller 类的完整名称 */
        private String controllerName;
        /** 处理方法名 */
        private String requestMethodName;
        /** 方法参数类型数组 */
        private Class<?>[] methodParamTypes;
    }
}
