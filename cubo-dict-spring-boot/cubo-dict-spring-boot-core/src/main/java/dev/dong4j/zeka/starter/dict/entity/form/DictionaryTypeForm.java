package dev.dong4j.zeka.starter.dict.entity.form;

import dev.dong4j.zeka.kernel.common.base.BaseForm;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p> 字典类型表 入参实体 (根据业务需求添加字段) </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "字典类型表-新增与更新")
public class DictionaryTypeForm extends BaseForm<Long> {
    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典类型编码 */
    @Schema(description = "字典类型编码")
    @NotBlank(message = "[字典类型编码] 必填)")
    private String code;
    /** 字典类型名称 */
    @Schema(description = "字典类型名称")
    @NotBlank(message = "[字典类型名称] 必填)")
    private String name;
    /** 描述 */
    @Schema(description = "描述")
    @NotBlank(message = "[描述] 必填)")
    private String description;
    /** 字典状态 */
    @Schema(description = "字典状态")
    @NotNull(message = "[字典状态] 必填)")
    private DictionaryTypeState state;
    /** 排序 */
    @Schema(description = "排序")
    @NotNull(message = "[排序] 必填)")
    private Integer order;
    /** 租户ID */
    @Schema(description = "租户ID")
    @NotBlank(message = "[租户ID] 必填)")
    private String tenantId;
    /** 客户端ID */
    @Schema(description = "客户端ID")
    @NotBlank(message = "[客户端ID] 必填)")
    private String clientId;
}
