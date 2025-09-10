package dev.dong4j.zeka.starter.rest.endpoint;

import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 15:12
 * @since x.x.x
 */
@Slf4j
class EnumUtilsTest {

    @Test
    void test1() throws Exception {
        EnumInfo enumInfo = EnumUtils.toEnumInfo(DeletedEnum.class);
        // 这里可以使用Jackson或Gson等库将enumInfo转换为JSON
        log.info("\n{}", Jsons.toJson(enumInfo, true));
    }

}
