package dev.dong4j.zeka.starter.dict;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.test.ZekaTest;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典应用测试类
 * <p> 用于测试字典相关功能的单元测试, 包含测试前后的初始化和清理操作, 以及具体的测试方法
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Slf4j
@ZekaTest
public class DictApplicationTest {
    /**
     * 初始化测试环境
     * <p> 在每个测试方法执行前运行, 用于设置测试所需的初始状态 </p>
     *
     * @since 1.0.0
     */
    @BeforeEach
    void setUp() {
        log.info("start DictApplication test");
    }

    /**
     * 测试方法执行后的清理操作
     * <p> 在每个测试方法执行后记录日志, 表示测试结束
     *
     * @since 1.0.0
     */
    @AfterEach
    void tearDown() {
        log.info("end DictApplication test");
    }

    /**
     * 测试获取应用程序名称功能
     * <p>
     * 测试场景: 从 Spring 上下文中读取应用程序名称
     * 预期结果: 日志中应记录正确的应用程序名称
     * <p>
     * 该测试方法验证了通过 Spring 上下文获取应用程序名称的功能是否正常工作. 它从 Spring 的环境配置中读取指定的属性, 并将其记录到日志中.
     */
    @Test
    void test() {
        log.info("{}", SpringContext.getApplicationContext().getEnvironment().getProperty(ConfigKey.SpringConfigKey.APPLICATION_NAME));
    }
}
