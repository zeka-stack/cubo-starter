package dev.dong4j.zeka.starter.mybatis.cases.service;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.mybatis.repository.TestRepositoryService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 18:06
 * @since 1.8.0
 */
@ZekaTest
public class TestRepositoryServiceTest {

    /** Test repository service */
    @Resource
    private TestRepositoryService testRepositoryService;

    /**
     * Test 1
     *
     * @since 1.8.0
     */
    @SneakyThrows
    @Test
    void test_1() {
        this.testRepositoryService.delete(1L);
    }

}
