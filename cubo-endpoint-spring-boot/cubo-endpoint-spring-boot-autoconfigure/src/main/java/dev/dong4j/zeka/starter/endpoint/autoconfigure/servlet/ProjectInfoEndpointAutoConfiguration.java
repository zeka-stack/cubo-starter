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
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:12
 * @since 1.0.0
 */
@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Tag(name = "应用接口信息")
public class ProjectInfoEndpointAutoConfiguration {
    /** Application context */
    @Resource
    private WebApplicationContext applicationContext;
    /** Path matcher */
    private final PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 获取所有接口
     *
     * @return the result
     * @since 1.0.0
     */
    @Operation(summary = "获取应用所有接口")
    @GetMapping(value = "/request-urls")
    public Result<List<RequestToMethodItem>> index() {
        List<RequestToMethodItem> list = Lists.newArrayList();
        RequestMappingHandlerMapping requestMappingHandlerMapping =
            this.applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
            requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            // HTTP 请求类型 (GET, POST ...)
            RequestMethodsRequestCondition methodCondition = requestMappingInfo.getMethodsCondition();
            String requestType = methodCondition.getMethods().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

            // 兼容 Spring Boot 2.x 和 3.x 的 URL 路径解析
            String requestUrl;
            PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
            if (patternsCondition != null) {
                // Spring 5.x / Boot 2.x
                requestUrl = patternsCondition.getPatterns().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            } else if (requestMappingInfo.getPathPatternsCondition() != null) {
                // Spring 6.x / Boot 3.x
                requestUrl = requestMappingInfo.getPathPatternsCondition().getPatterns().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            } else {
                requestUrl = "";
            }

            // 方法参数类型
            Class<?>[] methodParamTypes = handlerMethod.getMethod().getParameterTypes();

            // 过滤掉默认跳过的 URL
            boolean allMatch = SecurityUtils.DEFAULT_SKIP_URL.stream()
                .allMatch(pattern -> this.pathMatcher.matchStart(pattern, requestUrl));
            if (allMatch) {
                continue;
            }

            // Controller 类名
            String controllerName = handlerMethod.getBeanType().toString();
            // 方法名
            String requestMethodName = handlerMethod.getMethod().getName();

            RequestToMethodItem item = RequestToMethodItem.builder()
                .requestType(requestType)
                .requestUrl(requestUrl)
                .controllerName(controllerName)
                .requestMethodName(requestMethodName)
                .methodParamTypes(methodParamTypes)
                .build();

            list.add(item);
        }
        return R.succeed(list);
    }

    /**
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.10 11:42
     * @since 1.0.0
     */
    @Data
    @Builder
    public static class RequestToMethodItem {
        /** Request type */
        private String requestType;
        /** Request url */
        private String requestUrl;
        /** Controller name */
        private String controllerName;
        /** Request method name */
        private String requestMethodName;
        /** Method param types */
        private Class<?>[] methodParamTypes;
    }
}
