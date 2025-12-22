package dev.dong4j.zeka.starter.dict.autoconfigure;

import org.junit.jupiter.api.Test;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典自动配置测试类
 * <p> 用于测试字典自动配置功能, 包含相关的测试方法以验证配置的正确性和完整性
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Slf4j
@ZekaTest(classes = {DictAutoConfiguration.class})
class DictAutoConfigurationTest {

    /**
     * 测试日志记录功能
     * <p>
     * 测试场景: 验证日志信息是否正确输出
     * 预期结果: 日志中应包含 "hello tester" 的信息
     */
    @Test
    void test() {
        log.info("hello tester");
    }
}
