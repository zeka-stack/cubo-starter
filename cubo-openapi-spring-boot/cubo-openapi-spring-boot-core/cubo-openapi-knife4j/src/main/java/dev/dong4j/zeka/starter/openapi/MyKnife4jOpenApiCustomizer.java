package dev.dong4j.zeka.starter.openapi;

import com.google.common.collect.Maps;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import com.github.xiaoymin.knife4j.core.conf.GlobalConstants;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jSetting;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jOpenApiCustomizer;
import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;

import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义 Knife4j OpenAPI 配置类
 * <p> 该类继承自 Knife4jOpenApiCustomizer, 用于扩展和定制 OpenAPI 文档的生成逻辑, 支持添加扩展信息,Markdown 文件, 标签排序等功能, 适用于需要对 Swagger 文档进行深度定制的场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
public class MyKnife4jOpenApiCustomizer extends Knife4jOpenApiCustomizer {
    /** Knife4j 配置属性, 用于控制 Knife4j 的行为和设置 */
    final Knife4jProperties knife4jProperties;
    /** SpringDoc 配置属性, 用于获取分组配置等信息 */
    final SpringDocConfigProperties properties;

    /**
     * 构造函数, 用于初始化 MyKnife4jOpenApiCustomizer 实例
     * <p> 该构造函数接收 Knife4jProperties 和 SpringDocConfigProperties 作为参数, 并将其传递给父类构造函数进行初始化, 同时保存为当前类的成员变量.
     *
     * @param knife4jProperties Knife4j 的配置属性
     * @param properties        SpringDoc 的配置属性
     */
    public MyKnife4jOpenApiCustomizer(Knife4jProperties knife4jProperties, SpringDocConfigProperties properties) {
        super(knife4jProperties, properties);
        this.knife4jProperties = knife4jProperties;
        this.properties = properties;
    }

    /**
     * 自定义 OpenAPI 配置
     * <p> 根据 Knife4j 配置启用状态, 初始化扩展解析器并添加扩展信息到 OpenAPI 对象中, 同时调用 addOrderExtension 方法为标签添加排序信息.
     *
     * @param openApi OpenAPI 对象, 用于添加扩展信息
     */
    @Override
    public void customise(OpenAPI openApi) {
        log.debug("Knife4j OpenApiCustomizer");
        if (knife4jProperties.isEnable()) {
            Knife4jSetting setting = knife4jProperties.getSetting();
            OpenApiExtensionResolver openApiExtensionResolver = new OpenApiExtensionResolver(setting, knife4jProperties.getDocuments());
            // 解析初始化
            openApiExtensionResolver.start();
            Map<String, Object> objectMap = Maps.newHashMapWithExpectedSize(2);
            objectMap.put(GlobalConstants.EXTENSION_OPEN_SETTING_NAME, setting);
            objectMap.put(GlobalConstants.EXTENSION_OPEN_MARKDOWN_NAME, openApiExtensionResolver.getMarkdownFiles());
            openApi.addExtension(GlobalConstants.EXTENSION_OPEN_API_NAME, objectMap);
            addOrderExtension(openApi);
        }
    }

    /**
     * 向 OpenAPI 的 tags 字段添加 x-order 属性
     * <p> 根据配置扫描指定包路径下的控制器类, 提取带有 @ApiSupport 注解的类的标签信息, 并将对应的 x-order 属性添加到 OpenAPI 的 tags 中.
     *
     * @param openApi 要扩展的 OpenAPI 对象
     */
    private void addOrderExtension(OpenAPI openApi) {
        if (CollectionUtils.isEmpty(properties.getGroupConfigs())) {
            return;
        }
        // 获取包扫描路径
        Set<String> packagesToScan =
            properties.getGroupConfigs().stream()
                .map(SpringDocConfigProperties.GroupConfig::getPackagesToScan)
                .filter(toScan -> !CollectionUtils.isEmpty(toScan))
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(packagesToScan)) {
            return;
        }
        // 扫描包下被ApiSupport注解的RestController Class
        Set<Class<?>> classes =
            packagesToScan.stream()
                .map(packageToScan -> scanPackageByAnnotation(packageToScan, RestController.class))
                .flatMap(Set::stream)
                .filter(clazz -> clazz.isAnnotationPresent(ApiSupport.class))
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(classes)) {
            // ApiSupport oder值存入tagSortMap<Tag.name,ApiSupport.order>
            Map<String, Integer> tagOrderMap = Maps.newHashMapWithExpectedSize(128);
            classes.forEach(
                clazz -> {
                    Tag tag = getTag(clazz);
                    if (Objects.nonNull(tag)) {
                        ApiSupport apiSupport = clazz.getAnnotation(ApiSupport.class);
                        tagOrderMap.putIfAbsent(tag.name(), apiSupport.order());
                    }
                });
            // 往openApi tags字段添加x-order增强属性
            if (openApi.getTags() != null) {
                openApi
                    .getTags()
                    .forEach(
                        tag -> {
                            if (tagOrderMap.containsKey(tag.getName())) {
                                tag.addExtension(
                                    ExtensionsConstants.EXTENSION_ORDER, tagOrderMap.get(tag.getName()));
                            }
                        });
            }
        }
    }

    /**
     * 从类或其接口中获取 Tag 注解
     * <p> 首先检查类本身是否包含 {@link Tag} 注解, 如果没有, 则检查其所有接口是否包含该注解.
     *
     * @param clazz 要检查注解的类
     * @return 返回找到的 Tag 注解对象, 如果未找到则返回 null
     */
    private Tag getTag(Class<?> clazz) {
        // 从类上获取
        Tag tag = clazz.getAnnotation(Tag.class);
        if (Objects.isNull(tag)) {
            // 从接口上获取
            Class<?>[] interfaces = clazz.getInterfaces();
            if (ArrayUtils.isNotEmpty(interfaces)) {
                for (Class<?> interfaceClazz : interfaces) {
                    Tag anno = interfaceClazz.getAnnotation(Tag.class);
                    if (Objects.nonNull(anno)) {
                        tag = anno;
                        break;
                    }
                }
            }
        }
        return tag;
    }

    /**
     * 扫描指定包路径下包含特定注解的所有类
     * <p> 使用 Spring 的类路径扫描功能, 查找指定包中包含指定注解的所有类, 并返回这些类的集合.
     *
     * @param packageName     要扫描的包路径
     * @param annotationClass 要查找的注解类型
     * @return 包含指定注解的类集合
     */
    private Set<Class<?>> scanPackageByAnnotation(
        String packageName, final Class<? extends Annotation> annotationClass) {
        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        Set<Class<?>> classes = new HashSet<>();
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(packageName)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                classes.add(clazz);
            } catch (ClassNotFoundException ignore) {

            }
        }
        return classes;
    }
}
