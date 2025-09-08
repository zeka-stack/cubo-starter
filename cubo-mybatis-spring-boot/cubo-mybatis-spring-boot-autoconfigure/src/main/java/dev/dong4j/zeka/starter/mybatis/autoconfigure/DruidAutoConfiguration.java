package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.WallConfig;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Druid 数据源自动配置类
 *
 * 该配置类主要用于自动配置 Druid 数据源相关功能，包括：
 * 1. 配置 SQL 防火墙规则，允许执行多条语句
 * 2. 移除 Druid 监控页面底部的广告信息
 * 3. 提供 Druid 相关的 Bean 配置
 *
 * 注意：该配置类只有在类路径中存在 DruidDataSourceAutoConfigure 等相关类时才会生效
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.06 22:27
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(value = {DruidDataSourceAutoConfigure.class, DruidStatProperties.class, Filter.class})
@EnableConfigurationProperties(DruidStatProperties.class)
public class DruidAutoConfiguration implements ZekaAutoConfiguration {

    public DruidAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 配置 Druid SQL 防火墙规则
     *
     * 该方法创建并配置 WallConfig 对象，用于设置 SQL 防火墙的安全规则。
     * 主要配置包括：
     * - 允许一次执行多条 SQL 语句
     * - 允许执行非基础语句（如存储过程调用等）
     *
     * @return WallConfig 防火墙配置对象
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
     * 创建移除 Druid 监控页面广告的过滤器
     *
     * 该方法创建一个过滤器，用于移除 Druid 监控页面底部的广告信息。
     * 过滤器会拦截对 common.js 文件的请求，并移除其中的广告相关内容。
     *
     * 主要功能：
     * - 拦截 /druid/js/common.js 请求
     * - 读取原始 common.js 内容并移除广告代码
     * - 禁用 gzip 压缩以防止内容损坏
     * - 设置正确的响应头和内容类型
     *
     * @param properties Druid 统计属性配置
     * @return FilterRegistrationBean<Filter> 过滤器注册 Bean
     * @since 1.0.0
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
