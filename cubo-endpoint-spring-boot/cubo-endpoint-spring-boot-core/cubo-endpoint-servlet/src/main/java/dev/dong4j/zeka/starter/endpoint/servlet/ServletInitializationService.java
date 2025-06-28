package dev.dong4j.zeka.starter.endpoint.servlet;

import cn.hutool.http.HttpUtil;
import dev.dong4j.zeka.kernel.common.util.JsonUtils;
import dev.dong4j.zeka.starter.endpoint.initialization.InitializationService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:07
 * @since 2022.1.1
 */
@Slf4j
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class ServletInitializationService implements InitializationService {

    /**
     * Request
     *
     * @param warmUpEndpoint warm up endpoint
     * @since 2022.1.1
     */
    @Override
    public void request(String warmUpEndpoint) {
        try {
            HttpUtil.post(warmUpEndpoint, JsonUtils.toJson(this.createSampleMessage()));
        } catch (Exception e) {
            log.debug("WarmUp Endpoint request error. warmUpEndpoint: [{}] {}", warmUpEndpoint, e.getMessage());
        }
    }
}
