package dev.dong4j.zeka.starter.dict;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.test.ZekaTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * <p> 单元测试主类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Slf4j
@ZekaTest
public class DictApplicationTest {
    /**
     * Set up
     *
     * @since 1.0.0
     */
    @BeforeEach
    void setUp() {
        log.info("start DictApplication test");
    }

    /**
     * Tear down
     *
     * @since 1.0.0
     */
    @AfterEach
    void tearDown() {
        log.info("end DictApplication test");
    }

    /**
     * Test
     *
     * @since 1.0.0
     */
    @Test
    void test() {
        log.info("{}", SpringContext.getApplicationContext().getEnvironment().getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME));
    }
}
