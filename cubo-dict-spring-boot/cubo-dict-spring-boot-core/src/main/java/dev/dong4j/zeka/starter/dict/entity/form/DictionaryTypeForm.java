package dev.dong4j.zeka.starter.dict.entity.form;

import java.io.Serial;

import dev.dong4j.zeka.kernel.common.base.BaseForm;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
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
 * 字典类型表表单类
 * <p> 用于字典类型数据的新增与更新操作, 封装了字典类型的基本信息和校验规则, 包括编码, 名称, 描述, 状态, 排序, 租户 ID 和客户端 ID 等字段.
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
@Schema(name = "字典类型表-新增与更新")
public class DictionaryTypeForm extends BaseForm<Long> {
    /** 序列化版本号 */
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
    /**
     * 字典状态
     * <p> 表示该字典类型的当前状态 </p>
     *
     * @see DictionaryTypeState
     */
    @Schema(description = "字典状态")
    @NotNull(message = "[字典状态] 必填)")
    private DictionaryTypeState state;
    /** 排序序号, 用于控制字典类型在列表中的显示顺序 */
    @Schema(description = "排序")
    @NotNull(message = "[排序] 必填)")
    private Integer order;
    /** 租户 ID */
    @Schema(description = "租户ID")
    @NotBlank(message = "[租户ID] 必填)")
    private String tenantId;
    /** 客户端 ID */
    @Schema(description = "客户端ID")
    @NotBlank(message = "[客户端ID] 必填)")
    private String clientId;
}
