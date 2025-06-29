package dev.dong4j.zeka.starter.openapi.json;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;
import springfox.documentation.spring.web.json.Json;

/**
 * <p>Company: 成都返空汇网络技术有限公司 </p>
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.15 19:18
 * @since 1.4.0
 */
class FastJsonHttpMessageConverterTest {

    /**
     * Test swagger *
     *
     * @throws HttpMessageNotWritableException http message not writable exception
     * @throws IOException                     io exception
     * @since 1.4.0
     */
    @Test
    void testSwagger() throws HttpMessageNotWritableException, IOException {
        Json value = new Json("{\"swagger\":\"2.0\"");
        HttpOutputMessage outMessage = new MockHttpOutputMessage() {

            @NotNull
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return httpHeaders;
            }
        };
        new FastJsonHttpMessageConverter().write(value, null, outMessage);
        Assertions.assertTrue((outMessage.getBody().toString().startsWith("{\"swagger\":\"2.0\"")));
    }

}
