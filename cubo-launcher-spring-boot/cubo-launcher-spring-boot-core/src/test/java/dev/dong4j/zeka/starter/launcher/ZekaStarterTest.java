package dev.dong4j.zeka.starter.launcher;

import dev.dong4j.zeka.starter.launcher.app.ApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.07 21:24
 * @since 1.0.0
 */
@Slf4j
class ZekaStarterTest {

    /**
     * 直接运行一个 @SpringBootApplication 应用
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    void test_run() throws Exception {
        ZekaStarter.run(ApplicationTest.class);
    }

}
