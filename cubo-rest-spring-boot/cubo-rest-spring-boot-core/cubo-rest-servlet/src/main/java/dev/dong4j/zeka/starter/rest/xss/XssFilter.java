package dev.dong4j.zeka.starter.rest.xss;

import dev.dong4j.zeka.kernel.web.support.CacheRequestEnhanceWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * <p>Description: XSS过滤 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.11 17:01
 * @since 1.0.0
 */
@AllArgsConstructor
public class XssFilter implements Filter {

    /** Xss properties */
    private final List<String> excludePatterns;

    /**
     * Init *
     *
     * @param config config
     * @since 1.0.0
     */
    @Override
    public void init(FilterConfig config) {
    }

    /**
     * Do filter *
     *
     * @param request  request
     * @param response response
     * @param chain    chain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     * @since 1.0.0
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();
        if (this.excludePatterns.stream().anyMatch(path::contains)) {
            chain.doFilter(request, response);
        } else {
            if (request instanceof CacheRequestEnhanceWrapper) {
                chain.doFilter(new XssHttpServletRequestWrapper(((CacheRequestEnhanceWrapper) request).getCachingRequestWrapper()),
                    response);
            } else {
                chain.doFilter(new XssHttpServletRequestWrapper(new ContentCachingRequestWrapper((HttpServletRequest) request)),
                    response);
            }
        }
    }

    /**
     * Destroy
     *
     * @since 1.0.0
     */
    @Override
    public void destroy() {
    }

}
