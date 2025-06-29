package dev.dong4j.zeka.starter.openapi.reader;

import dev.dong4j.zeka.starter.openapi.api.InterfaceServiceImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p>Company: 成都返空汇网络技术有限公司 </p>
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:19
 * @since 1.4.0
 */
public class NameDiscoverTest {

    /**
     * Test get param name *
     *
     * @throws NoSuchMethodException no such method exception
     * @throws SecurityException     security exception
     * @since 1.4.0
     */
    @Test
    public void testGetParamName() throws NoSuchMethodException, SecurityException {
        String[] parameterNames = NameDiscover.PARAMETER_NAME_DISCOVERER.getParameterNames(InterfaceServiceImplTest.class.getDeclaredMethod("login", String.class, String.class));
        Assertions.assertArrayEquals(new String[]{"name", "password"}, parameterNames);
    }

}
