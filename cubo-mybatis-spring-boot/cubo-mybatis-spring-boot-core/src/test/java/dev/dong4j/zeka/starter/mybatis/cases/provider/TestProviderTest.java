package dev.dong4j.zeka.starter.mybatis.cases.provider;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.mybatis.entity.dto.TestDTO;
import dev.dong4j.zeka.starter.mybatis.entity.form.TestQuery;
import dev.dong4j.zeka.starter.mybatis.provider.TestProvider;
import jakarta.annotation.Resource;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.16 16:58
 * @since 1.8.0
 */
@ZekaTest
public class TestProviderTest {

    /** Test provider */
    @Resource
    private TestProvider testProvider;

    /**
     * Test 1
     *
     * @since 1.8.0
     */
    @SneakyThrows
    @Test
    void test_1() {
        this.testProvider.create(TestDTO.builder().build());
        this.testProvider.find(1L);
        this.testProvider.delete(1L);
        List<Long> longs = new ArrayList<Long>() {
            @Serial
            private static final long serialVersionUID = 429666079236455736L;

            {
                this.add(1L);
            }
        };

        this.testProvider.delete(longs);
        this.testProvider.update(TestDTO.builder().build());
        this.testProvider.find();
        this.testProvider.page(TestQuery.builder().build());

        this.testProvider.counts();

    }
}
