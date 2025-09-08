package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

import org.springframework.web.client.RestTemplate;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.12.30 19:58
 * @since 1.0.0
 */
public interface RestTemplateCustomizer {

    /**
     * Customize
     *
     * @param restTemplate rest template
     * @since 1.0.0
     */
    void customize(RestTemplate restTemplate);

}
