package dev.dong4j.zeka.starter.launcher.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.24 22:19
 * @since x.x.x
 */
@Slf4j
class ClassScannerTest {
    @Test
    void test_scanner() {
        Set<Class<?>> classes = ClassScanner.getClasses("dev.dong4j.zeka.starter.launcher");

        log.info("{}", classes);
    }

    @Test
    public void testScanClasses() {
        Set<Class<?>> classes = ClassScanner.getClasses("dev.dong4j.zeka.starter.launcher");
        classes.forEach(System.out::println);
        assertTrue(classes.stream().anyMatch(cls -> cls.getSimpleName().equals("ZekaStarter")));
    }
}
