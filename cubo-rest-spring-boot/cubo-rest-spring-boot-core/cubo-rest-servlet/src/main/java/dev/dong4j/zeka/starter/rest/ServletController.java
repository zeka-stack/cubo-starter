package dev.dong4j.zeka.starter.rest;

import dev.dong4j.zeka.kernel.common.api.GeneralResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.24 20:42
 * @since 1.5.0
 */
@Validated
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class ServletController implements GeneralResult {

    @Autowired
    @Lazy
    protected ObjectFactory<HttpServletRequest> requestFactory;

    @Autowired
    @Lazy
    protected ObjectFactory<HttpServletResponse> responseFactory;

    protected HttpServletRequest getRequest() {
        return requestFactory.getObject();
    }

    protected HttpServletResponse getResponse() {
        return responseFactory.getObject();
    }

}
