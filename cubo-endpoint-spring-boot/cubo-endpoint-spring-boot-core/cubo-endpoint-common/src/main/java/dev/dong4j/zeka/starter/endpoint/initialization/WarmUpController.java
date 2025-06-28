package dev.dong4j.zeka.starter.endpoint.initialization;


import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:27
 * @since 2022.1.1
 */
@RestController
@RequestMapping(
    path = "/warmup",
    consumes = APPLICATION_JSON_VALUE
)
public class WarmUpController {

    /**
     * Post
     *
     * @param dto dto
     * @return the result
     * @since 2022.1.1
     */
    @PostMapping
    public Result<WarmUpRequestDTO> post(@RequestBody @Valid WarmUpRequestDTO dto) {
        return R.succeed(dto);
    }
}
