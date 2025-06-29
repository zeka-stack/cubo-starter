package dev.dong4j.zeka.starter.{{name}}.autoconfigure;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 自动装配测试
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date {{date}}
 * @since 1.0.0
 */
@Slf4j
@ZekaTest(classes = {{{Name}}AutoConfiguration.class})
class {{Name}}AutoConfigurationTest {

    @Test
    void test() {
        log.info("hello tester");
    }
}
