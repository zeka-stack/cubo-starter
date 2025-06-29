package dev.dong4j.zeka.starter.rest.autoconfigure;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.rest.autoconfigure.reactive.WebFluxAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.reactive.WebfluxGlobalExceptionAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.ServletAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.ServletGlobalExceptionAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.ServletWebAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.UndertowAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.WebMvcStringTrimAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.supportss.JacksonConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.supportss.RestTemplateAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 自动装配测试
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 10:44
 * @since 1.0.0
 */
@Slf4j
@ZekaTest(classes = {
    RestAutoConfiguration.class,
    RestTemplateAutoConfiguration.class,
    JacksonConfiguration.class,
    ServletAutoConfiguration.class,
    ServletGlobalExceptionAutoConfiguration.class,
    ServletWebAutoConfiguration.class,
    UndertowAutoConfiguration.class,
    WebMvcStringTrimAutoConfiguration.class,
    WebFluxAutoConfiguration.class,
    WebfluxGlobalExceptionAutoConfiguration.class,
})
class RestAutoConfigurationTest {

    @Test
    void test() {
        log.info("hello tester");
    }
}
