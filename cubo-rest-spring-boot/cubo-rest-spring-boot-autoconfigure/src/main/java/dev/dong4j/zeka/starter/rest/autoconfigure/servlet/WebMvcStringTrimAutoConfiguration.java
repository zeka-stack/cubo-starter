package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 字符串去空格自动配置类
 *
 * 该自动配置类专门用于统一处理 Spring MVC 中字符串参数的首尾空白字符问题。
 * 在 Web 应用中，用户输入常常包含意外的空格或制表符，这个配置类提供了
 * 统一的解决方案来自动清理这些不必要的空白字符。
 *
 * 主要功能：
 * 1. 自动去除字符串参数的首尾空白字符
 * 2. 支持 URL 参数和表单参数的统一处理
 * 3. 保持空字符串为空字符串，不转换为 null
 * 4. 提供全局的字符串处理策略
 *
 * 处理范围：
 * - URL 参数（Query Parameter）
 * - 表单数据（Form Data）
 * - 路径参数（Path Variable）
 * - 矩阵变量（Matrix Variable）
 *
 * 处理策略：
 * - 使用 StringTrimmerEditor 进行字符串编辑
 * - 构造参数设置为 false，意味着空白字符串会被转换为空字符串 ""，而不是 null
 * - 通过 @ControllerAdvice 实现全局生效
 * - 在 Web 数据绑定阶段进行处理
 *
 * 使用场景：
 * - 用户输入表单的数据清理
 * - API 参数的统一预处理
 * - 防止因空格导致的数据校验失败
 * - 提升数据处理的一致性和可靠性
 *
 * 加载限制：
 * - 仅在 Servlet Web 环境下生效
 * - 在 WebMvcAutoConfiguration 之后加载
 * - 支持通过配置属性开关功能
 *
 * 注意事项：
 * - 该配置不影响 @RequestBody 中的 JSON 数据
 * - 只处理 String 类型的参数
 * - 保持与现有验证规则的兼容性
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.05.25 13:55
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class, ZekaServletExceptionErrorAttributes.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WebMvcStringTrimAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * Web MVC 字符串去空格自动配置构造函数
     * <p>
     * 初始化配置类并记录加载信息。
     *
     * @since 1.0.0
     */
    public WebMvcStringTrimAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 控制器字符串参数去空格配置类
     *
     * 这个内部静态类使用 @ControllerAdvice 注解，为整个应用的所有 Controller
     * 提供统一的字符串参数处理逻辑。通过 @InitBinder 注解，它会在 Web 数据绑定
     * 阶段自动对所有 String 类型的参数进行首尾空白字符的去除处理。
     *
     * 设计特点：
     * - 使用 @ControllerAdvice 全局生效
     * - 通过 @InitBinder 在数据绑定阶段拦截
     * - 使用 StringTrimmerEditor 处理字符串的裁剪
     * - 只对 String 类型生效，不影响其他数据类型
     *
     * 处理逻辑：
     * StringTrimmerEditor 的 boolean 参数表示当字符串为空白字符串时的处理策略：
     * - true：空白字符串会被转换为 null
     * - false：空白字符串会被转换为空字符串 ""
     *
     * 这里设置为 false，意味着保持空字符串为空字符串，不转换为 null，
     * 这样可以保持与现有业务逻辑的兼容性。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.05.25 13:55
     * @since 1.0.0
     */
    @ControllerAdvice
    public static class ControllerStringParamTrimConfig {

        /**
         * 初始化 Web 数据绑定器
         *
         * 这个方法会在每个 Controller 方法执行前被调用，用于配置参数绑定规则。
         * 它为所有 String 类型的参数注册了一个 StringTrimmerEditor，该编辑器会自动
         * 去除字符串参数的首尾空白字符。
         *
         * 处理范围：
         * - URL 查询参数（Query Parameters）
         * - 表单数据（Form Data）
         * - 路径参数（Path Variables）
         * - 矩阵变量（Matrix Variables）
         *
         * 注意：该方法不处理 @RequestBody 中的 JSON 数据，
         * JSON 数据的处理由 Jackson 的 HttpMessageConverter 负责。
         *
         * {@link StringTrimmerEditor}：构造方法中 boolean 参数含义为如果是空白字符串，
         * 是否转换为null，即如果为true，那么 " " 会被转换为 null，否者为 ""
         *
         * @param binder Web 数据绑定器，用于注册自定义的属性编辑器
         * @since 1.0.0
         */
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            StringTrimmerEditor propertyEditor = new StringTrimmerEditor(false);
            binder.registerCustomEditor(String.class, propertyEditor);
        }
    }

}
