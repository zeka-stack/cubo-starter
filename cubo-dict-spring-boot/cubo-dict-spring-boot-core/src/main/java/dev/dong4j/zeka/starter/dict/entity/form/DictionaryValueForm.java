package dev.dong4j.zeka.starter.dict.entity.form;

import java.io.Serial;

import dev.dong4j.zeka.kernel.common.base.BaseForm;
import dev.dong4j.zeka.starter.dict.enums.DictionaryValueState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 字典值表表单数据类
 * <p> 用于字典值的新增与更新操作, 封装了字典值相关的基础信息, 包括类型编码, 值编码, 名称, 描述, 排序, 状态, 租户 ID 和客户端 ID 等字段.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "字典值表-新增与更新")
public class DictionaryValueForm extends BaseForm<Long> {
    /** 序列化版本标识符 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典类型编码 */
    @Schema(description = "字典类型编码")
    @NotBlank(message = "[字典类型编码] 必填)")
    private String typeCode;
    /**
     * 字典值编码
     * <p> 必填字段, 用于唯一标识字典值
     */
    @Schema(description = "字典值编码")
    @NotBlank(message = "[字典值编码] 必填)")
    private String code;
    /** 字典值名称 */
    @Schema(description = "字典值名称")
    @NotBlank(message = "[字典值名称] 必填)")
    private String name;
    /** 字典值描述 */
    @Schema(description = "字典值描述")
    @NotBlank(message = "[字典值描述] 必填)")
    private String description;
    /**
     * 排序
     * <p> 用于指定字典值的顺序 </p>
     *
     * @see DictionaryValueForm
     */
    @Schema(description = "排序")
    @NotNull(message = "[排序] 必填)")
    private Integer order;
    /** 字典值状态 */
    @Schema(description = "字典值状态")
    @NotNull(message = "[字典值状态] 必填)")
    private DictionaryValueState state;
    /** 租户 ID */
    @Schema(description = "租户ID")
    @NotBlank(message = "[租户ID] 必填)")
    private String tenantId;
    /** 客户端 ID */
    @Schema(description = "客户端ID")
    @NotBlank(message = "[客户端ID] 必填)")
    private String clientId;
}
