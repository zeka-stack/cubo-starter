package dev.dong4j.zeka.starter.endpoint.initialization;


import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 预热请求数据传输对象
 *
 * 该类用于应用启动后的预热请求，通过模拟真实的 HTTP 请求来触发
 * Spring Boot 应用的各项初始化操作，如 Jackson 序列化、参数验证、缓存加载等，
 * 从而减少用户第一次访问时的响应延迟。
 *
 * 包含多种类型的数据字段和校验注解，用于测试不同场景下的参数处理。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 16:59
 * @since 1.0.0
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WarmUpRequestDTO extends BaseDTO<String> {
    private static final long serialVersionUID = -946407216751970529L;
    /** 预热字符串，必须为 "warm me up" */
    @NotBlank
    @Pattern(regexp = "warm me up")
    private String warmUpString;

    /** 预热数字，取值范围 10-20 */
    @Min(10)
    @Max(20)
    private Integer warmUpNumber;

    /** 预热枚举值 */
    @Valid
    private WarmUpEnum warmUpEnumDto;

    /** 预热大数字，不能为空 */
    @NotNull
    private BigDecimal warmUpBigDecimal;

}
