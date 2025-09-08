package dev.dong4j.zeka.starter.endpoint.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.endpoint.ServletEndpointLauncherInitiation;
import dev.dong4j.zeka.starter.endpoint.autoconfigure.EndpointProperties;
import dev.dong4j.zeka.starter.endpoint.initialization.InitializationService;
import dev.dong4j.zeka.starter.endpoint.servlet.ServletInitializationService;
import jakarta.annotation.Resource;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Servlet 环境下的启动信息自动配置类
 *
 * 该类为传统的 Servlet Web 环境提供启动信息和 Git 信息相关的端点配置。
 * 主要包括：
 *
 * 1. 创建用于显示应用版本和 Git 信息的 Actuator 端点
 * 2. 配置 Servlet 环境下的初始化服务
 * 3. 支持根据是否存在 git.properties 文件来提供不同的信息展示
 *
 * 仅在 Servlet Web 环境下生效，使用条件注解确保兼容性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:20
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnEnabled(value = EndpointProperties.PREFIX)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, ServletEndpointLauncherInitiation.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletStartInfoAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造方法
     * <p>
     * 输出启动日志，标识该自动配置类已被加载。
     */
    public ServletStartInfoAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建启动信息端点
     *
     * 创建用于显示应用启动信息和 Git 相关信息的 Actuator 端点。
     * 该端点可以通过 /actuator/start 访问。
     *
     * @return StartInfoEndpoint 实例
     * @since 1.0.0
     */
    @Bean
    public StartInfoEndpoint startInfoEndpoint() {
        return new StartInfoEndpoint();
    }

    /**
     * 启动信息 Actuator 端点内部类
     *
     * 提供用于显示应用版本信息和 Git 信息的 Web 端点。
     * 通过 @WebEndpoint 注解注册为 id 为 "start" 的 Actuator 端点。
     * 支持根据 git.properties 文件的存在情况提供不同的响应。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.12.22 09:31
     * @since 1.0.0
     */
    @WebEndpoint(id = "start")
    public static class StartInfoEndpoint {
        /** HTTP 响应对象，用于重定向 */
        @Resource
        private HttpServletResponse response;

        /**
         * 获取应用版本信息
         *
         * 显示 Git 相关信息，如果本地编译运行没有生成 git.properties 文件，
         * 则直接重定向到 /actuator/info 端点。
         * 如果存在 git.properties 文件，则读取并返回其中的所有属性。
         *
         * @return Git 属性信息或 null（重定向情况下）
         * @throws IOException 文件读取异常
         * @since 1.0.0
         */
        @ReadOperation
        public Result<Properties> versionInformation() throws IOException {
            // 获取类加载器
            ClassLoader classLoader = this.getClass().getClassLoader();
            // 尝试加载 git.properties 文件
            InputStream inputStream = classLoader.getResourceAsStream(App.GIT_CONFIG_FILE_NAME);
            // 如果文件不存在，重定向到 /actuator/info
            if (inputStream == null) {
                this.response.sendRedirect(ConfigKit.getContextPath() + LibraryEnum.START_URL);
                return null;
            } else {
                // 读取 git.properties 文件内容
                Properties properties = new Properties();
                try {
                    properties.load(inputStream);
                } catch (IOException ignored) {
                    // 忽略读取异常
                }
                return R.succeed(properties);
            }
        }
    }

    /**
     * 创建 Servlet 初始化服务
     *
     * 在 Servlet 环境下创建初始化服务实现，用于应用预热。
     * 仅在没有其他 InitializationService 实现时才会创建。
     *
     * @return ServletInitializationService 实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(InitializationService.class)
    public InitializationService servletInitializationService() {
        return new ServletInitializationService();
    }
}
