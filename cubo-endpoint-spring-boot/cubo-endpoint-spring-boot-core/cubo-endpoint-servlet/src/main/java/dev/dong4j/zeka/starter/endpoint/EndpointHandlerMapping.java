package dev.dong4j.zeka.starter.endpoint;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 自定义 Endpoint 处理器映射
 *
 * 自动将被 @Endpoint 注解标识的类注册为一个 Controller。
 * 继承自 Spring MVC 的 RequestMappingHandlerMapping，实现了自定义的
 * 请求映射处理逻辑。
 *
 * 主要功能：
 * 1. 识别和处理 @Endpoint 注解的类
 * 2. 支持自定义路径映射
 * 3. 设置适当的优先级以避免与业务 Controller 冲突
 *
 * 参考文档：http://monkeywie.cn/2020/06/22/custom-springmvc-requestmappinghandlermapping
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 03:15
 * @since 1.0.0
 */
@Slf4j
public abstract class EndpointHandlerMapping extends RequestMappingHandlerMapping {

    /** 路径映射配置，用于将默认路径映射到自定义路径 */
    private final Map<String, String> mappings = Maps.newHashMap();
    /** 已注册的路径集合 */
    private final Set<String> paths = Sets.newHashSet();

    /**
     * 获取映射路径
     *
     * 根据默认路径获取对应的自定义路径，如果没有自定义路径则返回默认路径。
     *
     * @param defaultPath 默认路径
     * @return 实际使用的路径（自定义或默认）
     * @since 1.0.0
     */
    private String getPath(String defaultPath) {
        String result = defaultPath;
        // 检查是否存在自定义路径映射
        if (this.mappings.containsKey(defaultPath)) {
            result = this.mappings.get(defaultPath);
        }
        return result;
    }

    /**
     * 构造方法
     *
     * 初始化 EndpointHandlerMapping，设置适当的优先级以确保
     * 用户自定义的映射具有更高的优先级（除了资源映射）。
     *
     * @since 1.0.0
     */
    protected EndpointHandlerMapping() {
        // 确保用户自定义的映射默认具有更高的优先级（除了资源映射）
        this.setOrder(LOWEST_PRECEDENCE - 2);
    }

    /**
     * 检测是否为 Endpoint 处理器
     *
     * 重写父类方法，用于检测被 @Endpoint 注解标识的 Bean。
     * 只有被 @Endpoint 注解标识的类才会被此处理器处理。
     *
     * @param beanType Bean 类型
     * @return 是否为 Endpoint 处理器
     * @see RequestMappingHandlerMapping#isHandler
     * @since 1.0.0
     */
    @Override
    protected boolean isHandler(@NotNull Class<?> beanType) {
        return AnnotationUtils.findAnnotation(beanType, Endpoint.class) != null;
    }

    /**
     * 获取方法的请求映射信息
     *
     * 重写父类方法，用于处理 Endpoint 方法的请求映射。
     * 支持自定义路径映射，并记录所有注册的路径。
     *
     * @param method 方法对象
     * @param handlerType 处理器类型
     * @return 请求映射信息，如果无法处理则返回 null
     * @since 1.0.0
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(@NotNull Method method, @NotNull Class<?> handlerType) {
        // 获取默认的请求映射信息
        RequestMappingInfo defaultMapping = super.getMappingForMethod(method, handlerType);
        // 如果无法获取默认映射，直接返回 null
        if (defaultMapping == null) {
            return null;
        }
        log.trace("添加自定义 endpoint: [{}], handler: [{}]", defaultMapping, handlerType);

        // 获取默认路径模式
        Set<String> defaultPatterns = defaultMapping.getPatternsCondition().getPatterns();
        String[] patterns = new String[defaultPatterns.size()];

        // 处理每个路径模式，支持自定义映射
        int i = 0;
        for (String pattern : defaultPatterns) {
            // 获取自定义路径或使用默认路径
            patterns[i] = this.getPath(pattern);
            // 记录已注册的路径
            this.paths.add(pattern);
            i++;
        }
        // 创建新的路径条件
        PatternsRequestCondition patternsInfo = new PatternsRequestCondition(patterns);

        // 返回新的请求映射信息，保留其他所有条件
        return new RequestMappingInfo(patternsInfo,
            defaultMapping.getMethodsCondition(),
            defaultMapping.getParamsCondition(),
            defaultMapping.getHeadersCondition(),
            defaultMapping.getConsumesCondition(),
            defaultMapping.getProducesCondition(),
            defaultMapping.getCustomCondition());

    }

}
