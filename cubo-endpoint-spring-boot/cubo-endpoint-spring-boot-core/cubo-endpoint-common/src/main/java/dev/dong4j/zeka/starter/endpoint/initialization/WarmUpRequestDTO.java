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
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 16:59
 * @since 2022.1.1
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WarmUpRequestDTO extends BaseDTO<String> {
    private static final long serialVersionUID = -946407216751970529L;
    /** Warm up string */
    @NotBlank
    @Pattern(regexp = "warm me up")
    private String warmUpString;

    /** Warm up number */
    @Min(10)
    @Max(20)
    private Integer warmUpNumber;

    /** Warm up enum dto */
    @Valid
    private WarmUpEnum warmUpEnumDto;

    /** Warm up big decimal */
    @NotNull
    private BigDecimal warmUpBigDecimal;

}
