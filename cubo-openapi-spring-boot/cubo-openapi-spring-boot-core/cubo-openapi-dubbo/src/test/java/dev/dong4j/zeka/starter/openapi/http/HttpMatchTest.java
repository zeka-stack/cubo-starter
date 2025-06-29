package dev.dong4j.zeka.starter.openapi.http;

import dev.dong4j.zeka.starter.openapi.api.InterfaceServiceImplTest;
import dev.dong4j.zeka.starter.openapi.api.InterfaceServiceTest;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:16
 * @since 1.4.0
 */
class HttpMatchTest {

    /** Http match */
    private final HttpMatch httpMatch = new HttpMatch(InterfaceServiceTest.class, InterfaceServiceImplTest.class);

    /**
     * Test find interface methods
     *
     * @since 1.4.0
     */
    @Test
    void testFindInterfaceMethods() {
        Method[] findInterfaceMethods = this.httpMatch.findInterfaceMethods("login");
        Assertions.assertEquals(2, findInterfaceMethods.length);

        findInterfaceMethods = this.httpMatch.findInterfaceMethods("get");
        Assertions.assertEquals(2, findInterfaceMethods.length);

        findInterfaceMethods = this.httpMatch.findInterfaceMethods("test");
        Assertions.assertEquals(1, findInterfaceMethods.length);

    }

    /**
     * Test find ref methods *
     *
     * @throws NoSuchMethodException no such method exception
     * @throws SecurityException     security exception
     * @since 1.4.0
     */
    @Test
    void testFindRefMethods() throws NoSuchMethodException, SecurityException {
        Method[] findInterfaceMethods = this.httpMatch.findInterfaceMethods("login");
        Assertions.assertEquals(2, findInterfaceMethods.length);

        Method[] findRefMethods = this.httpMatch.findRefMethods(findInterfaceMethods, null, "POST");
        Assertions.assertEquals(1, findRefMethods.length);
        Assertions.assertEquals(findRefMethods[0], InterfaceServiceImplTest.class.getDeclaredMethod("login", String.class));

        findRefMethods = this.httpMatch.findRefMethods(findInterfaceMethods, "bypwd", "POST");
        Assertions.assertEquals(1, findRefMethods.length);
        Assertions.assertEquals(findRefMethods[0], InterfaceServiceImplTest.class.getDeclaredMethod("login", String.class, String.class));

        findRefMethods = this.httpMatch.findRefMethods(findInterfaceMethods, "other", "POST");
        Assertions.assertEquals(0, findRefMethods.length);

        findRefMethods = this.httpMatch.findRefMethods(findInterfaceMethods, null, "GET");
        Assertions.assertEquals(0, findRefMethods.length);

        findInterfaceMethods = this.httpMatch.findInterfaceMethods("get");
        Assertions.assertEquals(2, findInterfaceMethods.length);
        findRefMethods = this.httpMatch.findRefMethods(findInterfaceMethods, null, "POST");
        Assertions.assertEquals(2, findRefMethods.length);
    }

    /**
     * Test match ref methods *
     *
     * @throws NoSuchMethodException no such method exception
     * @throws SecurityException     security exception
     * @since 1.4.0
     */
    @Test
    void testMatchRefMethods() throws NoSuchMethodException, SecurityException {
        Method[] findInterfaceMethods = this.httpMatch.findInterfaceMethods("get");
        Method[] findRefMethods = this.httpMatch.findRefMethods(findInterfaceMethods, null, "POST");
        Set<String> params = new HashSet<>();
        params.add("name");
        params.add("phone");
        Method matchRefMethod = this.httpMatch.matchRefMethod(findRefMethods, "get", params);
        Assertions.assertEquals(matchRefMethod, InterfaceServiceImplTest.class.getDeclaredMethod("get", String.class, String.class));

        params = new HashSet<>();
        params.add("id");
        matchRefMethod = this.httpMatch.matchRefMethod(findRefMethods, "get", params);
        Assertions.assertEquals(matchRefMethod, InterfaceServiceImplTest.class.getDeclaredMethod("get", String.class));
    }
}
