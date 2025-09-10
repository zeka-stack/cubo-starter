package dev.dong4j.zeka.starter.dict.autoconfigure;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 自动装配测试
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 22:49
 * @since 1.0.0
 */
@Slf4j
@ZekaTest(classes = {DictAutoConfiguration.class})
class DictAutoConfigurationTest {

    @Test
    void test() {
        log.info("hello tester");
    }
}
