package dev.dong4j.zeka.starter.openapi.reader;

import dev.dong4j.zeka.starter.openapi.api.InterfaceServiceImplTest;
import dev.dong4j.zeka.starter.openapi.api.InterfaceServiceTest;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class ReaderTest {


    /**
     * Test apply parameters
     *
     * @since 1.4.0
     */
    @Test
    void testApplyParameters() {
        Swagger swagger = new Swagger();
        Reader.read(swagger, new HashMap<Class<?>, Object>() {{
            this.put(InterfaceServiceTest.class, new InterfaceServiceImplTest());
        }}, "/h");

        Map<String, Path> paths = swagger.getPaths();
        Assertions.assertEquals(4, paths.size());

        Path path = swagger.getPaths().get("/h/dev.dong4j.zeka.starter.openapi.api.InterfaceServiceTest/test");
        Assertions.assertNotNull(path);
        Operation operation = path.getOperationMap().get(HttpMethod.POST);
        Assertions.assertNotNull(operation);
        Assertions.assertNotNull(operation.getParameters());
        Assertions.assertEquals("查询用户", operation.getSummary());

        List<Parameter> parameters = operation.getParameters();
        Assertions.assertEquals("para", parameters.get(0).getName());
        Assertions.assertEquals("code", parameters.get(1).getName());

        Assertions.assertTrue(parameters.get(0).getRequired());
        Assertions.assertEquals("参数", parameters.get(0).getDescription());


        path = swagger.getPaths().get("/h/dev.dong4j.zeka.starter.openapi.api.InterfaceServiceTest/login/bypwd");
        Assertions.assertNotNull(path);
        path = swagger.getPaths().get("/h/dev.dong4j.zeka.starter.openapi.api.InterfaceServiceTest/login");
        Assertions.assertNotNull(path);

    }

}
