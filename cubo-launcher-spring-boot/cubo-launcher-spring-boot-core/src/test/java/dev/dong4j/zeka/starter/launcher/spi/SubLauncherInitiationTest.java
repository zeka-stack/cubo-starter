package dev.dong4j.zeka.starter.launcher.spi;

import dev.dong4j.zeka.kernel.common.util.EnumUtils;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.24 22:05
 * @since 1.0.0
 */
@Slf4j
class SubLauncherInitiationTest {
    SubLauncherInitiation initiation = new SubLauncherInitiation();

    @Test
    public void testInvalidEnumValueConflict() {
        // 动态新增一个相同的枚举值
        log.info("{}", Arrays.toString(ErrorEnum.values()));
        EnumUtils.DynamicEnum.addEnum(ErrorEnum.class,
            "B",
            new Class<?>[]{Integer.class, String.class},
            new Object[]{1, "相同的 value"});

        log.info("{}", Arrays.toString(ErrorEnum.values()));
        log.info("{}", ErrorEnum.valueOf("B"));

        // 测试是否存在相同的枚举 value
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            initiation.before("conflict-test")
        );
        assertTrue(ex.getMessage().contains("存在相同的枚举 value"));
    }
}
