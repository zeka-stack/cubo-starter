package dev.dong4j.zeka.starter.rest;

import dev.dong4j.zeka.kernel.common.api.GeneralResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    /**
     * The Request.
     */
    @Resource
    protected HttpServletRequest request;
    /**
     * The Response.
     */
    @Resource
    protected HttpServletResponse response;

}
