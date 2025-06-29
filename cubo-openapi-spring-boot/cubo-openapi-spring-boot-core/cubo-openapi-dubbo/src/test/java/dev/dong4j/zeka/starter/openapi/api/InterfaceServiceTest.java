package dev.dong4j.zeka.starter.openapi.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;

/**
 * <p>Company: 成都返空汇网络技术有限公司 </p>
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:16
 * @since 1.4.0
 */
public interface InterfaceServiceTest {

    /**
     * Test list
     *
     * @param para para
     * @param code code
     * @return the list
     * @since 1.4.0
     */
    @ApiOperation(value = "查询用户", notes = "通过城市地区code取用户信息", response = String.class, responseContainer = "List")
    List<String> test(@ApiParam(value = "参数", required = true) String para, String code);

    /**
     * Login string
     *
     * @param name name
     * @return the string
     * @since 1.4.0
     */
    String login(String name);

    /**
     * Login string
     *
     * @param name     name
     * @param password password
     * @return the string
     * @since 1.4.0
     */
    @ApiOperation(nickname = "bypwd", value = "")
    String login(String name, String password);

    /**
     * Get string
     *
     * @param id id
     * @return the string
     * @since 1.4.0
     */
    String get(String id);

    /**
     * Get string
     *
     * @param name  name
     * @param phone phone
     * @return the string
     * @since 1.4.0
     */
    String get(String name, String phone);

}
