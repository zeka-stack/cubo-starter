package dev.dong4j.zeka.starter.openapi.spi;

import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.processor.annotation.AutoService;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * OpenAPI启动初始化器
 *
 * 该类负责在应用启动时设置OpenAPI相关的默认配置。
 * 通过LauncherInitiation机制，在系统启动时自动配置OpenAPI和Knife4j的相关参数。
 *
 * 主要功能包括：
 * 1. 设置OpenAPI功能的启用状态
 * 2. 配置Swagger UI的访问路径和排序规则
 * 3. 配置API文档的访问路径和分组
 * 4. 设置Knife4j的各种增强功能配置
 * 5. 配置UI个性化设置和调试功能
 *
 * 使用场景：
 * - 应用启动时的默认配置设置
 * - OpenAPI文档的自动配置
 * - Knife4j功能的初始化
 * - 开发环境的API文档配置
 *
 * 设计意图：
 * 通过启动初始化器提供OpenAPI和Knife4j的开箱即用配置，
 * 简化开发人员的配置工作，提供标准化的API文档功能。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class OpenAPILauncherInitiation implements LauncherInitiation {

    /**
     * 设置默认属性配置
     * <p>
     * 在应用启动时设置OpenAPI和Knife4j的默认配置属性。
     * 包括Swagger UI配置、API文档配置、Knife4j增强功能配置等。
     * <p>
     * 配置内容包括：
     * 1. OpenAPI功能启用配置
     * 2. Swagger UI访问路径和排序规则
     * 3. API文档访问路径和分组配置
     * 4. Knife4j基础开关和安全配置
     * 5. Basic认证配置
     * 6. 自定义文档配置
     * 7. UI个性化设置和调试功能
     *
     * @param env           可配置的环境对象
     * @param appName       应用名称
     * @param isLocalLaunch 是否为本地启动
     * @return 配置属性映射
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {

        return ChainMap.build(16)
            // 开启 openapi 功能
            .put(ConfigKey.PREFIX + "openapi.enabled", "true")

            // swagger-ui 配置
            .put("springdoc.swagger-ui.path", "/swagger-ui.html")          // UI 访问路径
            .put("springdoc.swagger-ui.tags-sorter", "alpha")              // tag 按字母排序
            .put("springdoc.swagger-ui.operations-sorter", "alpha")        // API 接口按字母排序

            // api-docs 配置
            .put("springdoc.api-docs.path", "/v3/api-docs")                // API 文档访问路径

            // 分组配置（group-configs）
            .put("springdoc.group-configs[0].group", appName)              // 分组名称（用应用名）
            .put("springdoc.group-configs[0].paths-to-match", "/**")       // 匹配路径
            .put("springdoc.group-configs[0].packages-to-scan", App.BASE_PACKAGES) // 扫描的基础包

            // knife4j 配置
            .put("knife4j.enable", "true")                                 // 启用 knife4j 增强
            // ============ 基础开关 ============
            .put("knife4j.cors", "false")                          // 是否开启默认跨域配置，默认false
            .put("knife4j.production", "false")                    // 是否开启生产环境保护策略，默认false

            // ============ Basic Http 认证 ============
            .put("knife4j.basic", "")                              // 开启Basic认证模式
            .put("knife4j.basic.enable", "false")                  // 是否启用Basic认证，默认false
            .put("knife4j.basic.username", "")                     // Basic用户名
            .put("knife4j.basic.password", "")                     // Basic密码

            // ============ 自定义文档 ============
            .put("knife4j.documents[0].group", "")                 // 文档所属分组
            .put("knife4j.documents[0].name", "")                  // 文档名称（类似tag）
            .put("knife4j.documents[0].locations", "")             // Markdown文件路径（支持文件夹/单文件）

            // ============ 前端UI个性化设置 ============
            .put("knife4j.setting.enable-after-script", "true")    // 是否显示AfterScript调试Tab，默认true
            .put("knife4j.setting.language", "zh-CN")              // UI默认语言 zh-CN / en-US，默认zh-CN
            .put("knife4j.setting.enable-swagger-models", "true")  // 是否显示Swagger Models，默认true
            .put("knife4j.setting.swagger-model-name", "Swagger Models") // 重命名Swagger Models
            .put("knife4j.setting.enable-document-manage", "true") // 是否显示"文档管理"，默认true
            .put("knife4j.setting.enable-reload-cache-parameter", "false") // 是否显示刷新变量按钮，默认false
            .put("knife4j.setting.enable-version", "false")        // 是否开启接口版本控制，默认false
            .put("knife4j.setting.enable-request-cache", "true")   // 是否开启请求参数缓存，默认true
            .put("knife4j.setting.enable-filter-multipart-apis", "false") // 是否过滤多类型RequestMapping，默认false
            .put("knife4j.setting.enable-filter-multipart-api-method-type", "POST") // 多请求类型过滤策略，默认POST
            .put("knife4j.setting.enable-host", "false")           // 是否启用Host显示，默认false
            .put("knife4j.setting.enable-host-text", "false")      // Host地址内容
            .put("knife4j.setting.enable-home-custom", "false")    // 是否开启自定义主页，默认false
            .put("knife4j.setting.home-custom-path", "")           // 自定义主页Markdown路径
            .put("knife4j.setting.enable-search", "false")         // 是否禁用搜索框，默认false
            .put("knife4j.setting.enable-footer", "true")          // 是否显示Footer，默认true
            .put("knife4j.setting.enable-footer-custom", "false")  // 是否启用自定义Footer，默认false
            .put("knife4j.setting.footer-custom-content", "false") // 自定义Footer内容
            .put("knife4j.setting.enable-dynamic-parameter", "false") // 是否开启动态参数调试功能，默认false
            .put("knife4j.setting.enable-debug", "true")           // 是否启用调试功能，默认true
            .put("knife4j.setting.enable-open-api", "true")        // 是否显示OpenAPI规范，默认true
            .put("knife4j.setting.enable-group", "true");          // 是否显示服务分组，默认true
    }

    /**
     * 获取初始化器执行顺序
     *
     * 返回初始化器的执行顺序，使用最高优先级加1000，确保在大部分初始化器之前执行。
     *
     * @return 执行顺序值
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }

    /**
     * 获取初始化器名称
     *
     * 返回初始化器的名称，用于标识和日志记录。
     *
     * @return 初始化器名称
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "cubo-openapi-knife4j-spring-boot-starter";
    }

}
