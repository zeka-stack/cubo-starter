package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.07 21:25
 * @since 1.0.0
 */
@Slf4j
@ZekaTest(classes = LauncherAutoConfiguration.class)
class LauncherAutoConfigurationTest {

    /**
     * Test start
     *
     * @since 1.0.0
     */
    @Test
    void test_start() {
        log.info("");
    }
}
