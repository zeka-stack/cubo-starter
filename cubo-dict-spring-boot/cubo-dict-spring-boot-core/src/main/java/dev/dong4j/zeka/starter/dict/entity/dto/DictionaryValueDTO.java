package dev.dong4j.zeka.starter.dict.entity.dto;

import java.io.Serial;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.starter.dict.enums.DictionaryValueState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 字典值传输对象
 * <p> 用于表示字典值的相关信息, 包括字典类型编码, 字典值编码, 字典值名称, 字典值描述, 排序, 字典值状态, 租户 ID 和客户端 ID 等属性
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
@Schema(name = "字典值表-数传传输对象")
public class DictionaryValueDTO extends BaseDTO<Long> {
    /**
     * 序列化版本标识符
     * <p> 用于序列化和反序列化对象时保持兼容性
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典类型编码 */
    @Schema(description = "字典类型编码")
    private String typeCode;
    /**
     * 字典值编码
     * <p> 用于唯一标识一个字典值
     */
    @Schema(description = "字典值编码")
    private String code;
    /** 字典值名称 */
    @Schema(description = "字典值名称")
    private String name;
    /** 字典值描述 */
    @Schema(description = "字典值描述")
    private String description;
    /** 排序顺序 */
    @Schema(description = "排序")
    private Integer order;
    /** 字典值状态 */
    @Schema(description = "字典值状态")
    private DictionaryValueState state;
    /**
     * 租户 ID
     * <p> 用于标识当前字典值所属的租户 </p>
     *
     * @see DictionaryValueDTO
     */
    @Schema(description = "租户ID")
    private String tenantId;
    /** 客户端 ID */
    @Schema(description = "客户端ID")
    private String clientId;
}
