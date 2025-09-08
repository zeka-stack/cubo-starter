package dev.dong4j.zeka.starter.rest.mapping;

import dev.dong4j.zeka.kernel.common.context.EarlySpringContext;
import dev.dong4j.zeka.starter.rest.annotation.ApiVersion;
import dev.dong4j.zeka.starter.rest.condition.ApiVersionCondition;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * <p>Description: api handler mapping </p>
 *
 * @author dong4jj
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.26 10:52
 * @since 1.0.0
 */
@Slf4j
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    /** VERSION_PATTERN */
    private static final String VERSION_PATTERN = "/v%d";

    /**
     * Gets custom type condition *
     *
     * @param handlerType handler type
     * @return the custom type condition
     * @since 1.0.0
     */
    @Override
    protected RequestCondition<ApiVersionCondition> getCustomTypeCondition(@NotNull Class<?> handlerType) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return createCondition(apiVersion);
    }

    /**
     * Gets custom method condition *
     *
     * @param method method
     * @return the custom method condition
     * @since 1.0.0
     */
    @Override
    protected RequestCondition<ApiVersionCondition> getCustomMethodCondition(@NotNull Method method) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createCondition(apiVersion);
    }

    /**
     * Create condition
     *
     * @param apiVersion api version
     * @return the request condition
     * @since 1.0.0
     */
    private RequestCondition<ApiVersionCondition> createCondition(ApiVersion apiVersion) {
        if (apiVersion == null || 0 == apiVersion.value().length) {
            return null;
        } else {
            return new ApiVersionCondition(Arrays.stream(apiVersion.value()).sorted().findFirst().orElse(1));
        }
    }

    /**
     * Register handler method
     *
     * @param handler handler
     * @param method  method
     * @param mapping mapping
     * @since 1.0.0
     */
    @Override
    protected void registerHandlerMethod(@NotNull Object handler, @NotNull Method method, @NotNull RequestMappingInfo mapping) {
        // handler may be bean name
        Class<?> clazz = handler instanceof String ? EarlySpringContext.getInstance(handler.toString()).getClass() : handler.getClass();
        // @ApiVersion 动态 mapping 逻辑注册
        ApiVersion apiVersion = Optional
            .ofNullable(AnnotationUtils.findAnnotation(method, ApiVersion.class))
            .orElse(AnnotationUtils.findAnnotation(clazz, ApiVersion.class));

        if (null != apiVersion && 0 != apiVersion.value().length) {
            log.trace("为 [{}] @ApiVersion.value({}) 添加新的URI匹配规则", method, apiVersion.value());

            String[] patterns = mapping.getPatternsCondition().getPatterns()
                .stream()
                .flatMap(pattern -> Arrays.stream(apiVersion.value())
                    .mapToObj(version -> String.format(VERSION_PATTERN, version) + pattern))
                .toArray(String[]::new);

            // 使用 RequestMappingInfo.Builder 创建新的 mapping
            RequestMappingInfo newMappingInfo = RequestMappingInfo.paths(patterns)
                .methods(mapping.getMethodsCondition().getMethods().toArray(new RequestMethod[0]))
                .params(mapping.getParamsCondition().getExpressions().stream()
                    .map(Object::toString)
                    .toArray(String[]::new))
                .headers(mapping.getHeadersCondition().getExpressions().stream()
                    .map(Object::toString)
                    .toArray(String[]::new))
                .consumes(mapping.getConsumesCondition().getExpressions().stream()
                    .map(Object::toString)
                    .toArray(String[]::new))
                .produces(mapping.getProducesCondition().getExpressions().stream()
                    .map(Object::toString)
                    .toArray(String[]::new))
                .build();

            // 添加自定义版本条件
            ApiVersionCondition versionCondition = new ApiVersionCondition(
                Arrays.stream(apiVersion.value()).sorted().findFirst().orElse(1));

            // 通过反射或其他方式添加自定义条件（具体实现取决于您的需求）
            super.registerHandlerMethod(handler, method, newMappingInfo);
            return;
        }

        // 如果没有打标签，那么走基本mapping逻辑注册
        super.registerHandlerMethod(handler, method, mapping);
    }
}
