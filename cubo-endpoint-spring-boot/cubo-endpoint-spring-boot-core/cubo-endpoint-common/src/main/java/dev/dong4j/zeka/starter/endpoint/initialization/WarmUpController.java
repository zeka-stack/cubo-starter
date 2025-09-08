package dev.dong4j.zeka.starter.endpoint.initialization;


import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * 应用预热控制器
 *
 * 该控制器提供一个用于应用预热的 REST 接口，通过接收和处理
 * 预热请求来触发 Spring Boot 应用的各项初始化操作。
 *
 * 包含 JSON 序列化、参数验证、异常处理等功能的测试，
 * 确保用户第一次真实访问时能够获得更快的响应速度。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:27
 * @since 1.0.0
 */
@RestController
@RequestMapping(
    path = "/warmup",
    consumes = APPLICATION_JSON_VALUE
)
public class WarmUpController {

    /**
     * 处理预热 POST 请求
     *
     * 接收并验证预热请求数据，测试 JSON 序列化/反序列化、
     * 参数验证、异常处理等功能的正常工作，确保应用的核心组件
     * 已经被正常初始化和加载。
     *
     * @param dto 预热请求数据，会自动进行参数验证
     * @return 返回原始请求数据，表示预热成功
     * @since 1.0.0
     */
    @PostMapping
    public Result<WarmUpRequestDTO> post(@RequestBody @Valid WarmUpRequestDTO dto) {
        return R.succeed(dto);
    }
}
