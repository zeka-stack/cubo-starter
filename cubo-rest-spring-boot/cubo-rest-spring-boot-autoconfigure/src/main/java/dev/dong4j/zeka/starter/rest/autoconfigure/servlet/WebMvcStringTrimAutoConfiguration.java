package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import javax.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>Description: 统一处理首尾空白字符串问题</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.05.25 13:55
 * @since 1.9.0
 */
@Slf4j
@Configuration
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class, ZekaServletExceptionErrorAttributes.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WebMvcStringTrimAutoConfiguration implements ZekaAutoConfiguration {

    public WebMvcStringTrimAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.05.25 13:55
     * @since 1.9.0
     */
    @ControllerAdvice
    public static class ControllerStringParamTrimConfig {

        /**
         * 处理 url 或 form 表单中的参数.
         * {@link StringTrimmerEditor}: 构造方法中 boolean 参数含义为如果是空白字符串,是否转换为null, 即如果为true,那么 " " 会被转换为 null,否者为 ""
         *
         * @param binder binder
         * @since 1.9.0
         */
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            StringTrimmerEditor propertyEditor = new StringTrimmerEditor(false);
            binder.registerCustomEditor(String.class, propertyEditor);
        }
    }

}
