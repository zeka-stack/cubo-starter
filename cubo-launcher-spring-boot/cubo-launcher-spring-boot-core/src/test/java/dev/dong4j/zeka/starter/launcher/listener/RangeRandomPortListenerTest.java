package dev.dong4j.zeka.starter.launcher.listener;

import dev.dong4j.zeka.kernel.common.exception.PropertiesException;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.kernel.test.ZekaTest;
import jakarta.annotation.Resource;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * 随机端口监听器测试类
 *
 * 该类测试 RangeRandomPortListener 的功能，包括：
 * 1. 随机端口生成功能
 * 2. 随机字符串生成功能
 * 3. 异常情况处理
 *
 * 测试用例覆盖了各种边界条件和错误场景，确保功能的正确性和健壮性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:48
 * @since 1.0.0
 */
@Slf4j
@Import(RangeRandomPortListener.class)
@ZekaTest(classes = RangeRandomPortListenerTest.TestAppConfiguration.class, properties = {
    "server.port=${range.random.int}",
    "server.port_1=${range.random.int(1024, 1024)}",
    "server.port_2=${range.random.int(1024, xxx)}",
    "server.port_3=${range.random.int(1024, xxx, yyy)}",
    "test.port_1=${range.random.int(1024,10000 )}",
    "test.port_2=${range.random.int( 1024 , 10000 )}",
    "test.port_3=${range.random.key( 64 )}",
    "test.port_4=${range.random.key(12 )}",
    "test.port_6=${range.random.key(xxxx )}",
    "test.port_7=${range.random.key}",
})
class RangeRandomPortListenerTest {

    /** Environment */
    @Resource
    private Environment environment;

    /**
     * 集成测试方法
     *
     * 测试随机端口和随机字符串生成功能，
     * 验证各种边界条件和错误场景。
     *
     * @since 1.0.0
     */
    @Test
    void test_() {
        Assertions.assertTrue(18080 > Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("server.port")))
            && Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("server.port"))) > 8080);

        Assertions.assertEquals(1024, Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("server.port_1"))));

        Assertions.assertThrows(PropertiesException.class, () -> {
            this.environment.getProperty("server.port_2");
        });

        Assertions.assertThrows(PropertiesException.class, () -> {
            this.environment.getProperty("server.port_3");
        });

        Assertions.assertEquals(12, Objects.requireNonNull(this.environment.getProperty("test.port_4")).length());

        Assertions.assertThrows(PropertiesException.class, () -> {
            this.environment.getProperty("test.port_6");
        });

        Assertions.assertEquals(64, Objects.requireNonNull(this.environment.getProperty("test.port_7")).length());
    }

    /**
     * 测试应用配置类
     *
     * 该类提供了测试所需的 Spring 配置，
     * 主要用于初始化测试环境。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.23 15:00
     * @since 1.0.0
     */
    @AutoConfiguration
    static class TestAppConfiguration {
    }

    /**
     * 单元测试内部类
     *
     * 该类包含针对特定方法的单元测试，
     * 主要用于测试正则表达式匹配逻辑。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.23 17:04
     * @since 1.0.0
     */
    static class UnitTest {
        /**
         * 单元测试方法
         *
         * 测试正则表达式匹配逻辑，
         * 验证各种输入格式的正确性。
         *
         * @since 1.0.0
         */
        @Test
        void test_1() {
            Assertions.assertTrue(this.checkPattern("[61000,61100]"));
            // 不能出现空格
            Assertions.assertFalse(this.checkPattern("[61000, 61100]"));
            Assertions.assertFalse(this.checkPattern("[-1,xxx]"));
            Assertions.assertFalse(this.checkPattern("[-1,-2]"));
        }

        /**
         * 检查输入范围是否符合预期格式
         *
         * 使用正则表达式验证输入字符串是否符合 "[数字,数字]" 的格式。
         *
         * @param range 输入的范围字符串
         * @return 如果符合格式返回 true，否则返回 false
         * @since 1.0.0
         */
        private boolean checkPattern(String range) {
            if (StringUtils.isBlank(range)) {
                return false;
            }
            String pattern = "^(\\[\\d+),(\\d+])$";
            return Pattern.matches(pattern, range);
        }
    }
}
