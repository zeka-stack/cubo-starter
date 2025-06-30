package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.WallConfig;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.06 22:27
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {DruidDataSourceAutoConfigure.class, DruidStatProperties.class})
@EnableConfigurationProperties(DruidStatProperties.class)
public class DruidAutoConfiguration implements ZekaAutoConfiguration {

    public DruidAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Wall config wall config
     *
     * @return the wall config
     * @since 1.0.0
     */
    @Bean
    public WallConfig wallConfig() {
        WallConfig wallConfig = new WallConfig();
        // 允许一次执行多条语句
        wallConfig.setMultiStatementAllow(true);
        // 允许一次执行多条语句
        wallConfig.setNoneBaseStatementAllow(true);
        return wallConfig;
    }

    /**
     * 除去页面底部的广告
     *
     * @param properties 特性
     * @return {@link FilterRegistrationBean }<{@link Filter }>
     */
    @Bean
    public FilterRegistrationBean<Filter> removeDruidAdFilterRegistrationBean(DruidStatProperties properties) {
        log.debug("加载 Druid 去广告过滤器 [{dev.dong4j.zeka.starter.mybatis.autoconfigure.DruidAutoConfiguration.removeDruidAdFilterRegistrationBean]");

        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        // 获取 common.js 的实际路径，默认是 /druid/js/common.js
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        String urlPattern = Optional.ofNullable(config.getUrlPattern()).orElse("/druid/*");
        String commonJsPattern = urlPattern.replaceAll("\\*", "js/common.js");

        // 定义过滤器逻辑
        Filter filter = (request, response, chain) -> {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            // 正常执行过滤器链，common.js 内容会先输出
            chain.doFilter(request, response);

            // 重置内容缓冲区，但不重置响应头
            resp.resetBuffer();

            // 禁用 gzip，防止 Nginx 代理下乱码或内容损坏
            resp.setHeader("Content-Encoding", "identity");
            resp.setContentType("application/javascript;charset=UTF-8");

            try {
                // 读取并修改 common.js 内容
                String text = Utils.readFromResource("support/http/resources/js/common.js");
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");

                resp.getWriter().write(text);
            } catch (IOException e) {
                // 写入失败则打印日志，并输出原始错误提示
                log.error("读取并修改 common.js 内容失败: {}", e.getMessage());
                resp.getWriter().write("// Failed to load modified common.js");
            }
        };

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        registrationBean.setName("druidAdRemoveFilter");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
