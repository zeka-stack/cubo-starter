package dev.dong4j.zeka.starter.dict.entity.dto;

import java.io.Serial;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 字典类型表的数据传输对象
 * <p> 用于表示字典类型的详细信息, 包括字典类型编码, 名称, 描述, 状态, 排序, 租户 ID 和客户端 ID 等属性
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
@Schema(name = "字典类型表-数传传输对象")
public class DictionaryTypeDTO extends BaseDTO<Long> {
    /**
     * 序列化版本标识符
     * <p> 用于序列化和反序列化对象时保持兼容性
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典类型编码 */
    @Schema(description = "字典类型编码")
    private String code;
    /** 字典类型名称 */
    @Schema(description = "字典类型名称")
    private String name;
    /** 描述 */
    @Schema(description = "描述")
    private String description;
    /**
     * 字典状态
     * <p> 表示字典类型的当前状态
     *
     * @see DictionaryTypeState
     */
    @Schema(description = "字典状态")
    private DictionaryTypeState state;
    /**
     * 排序
     * <p> 用于定义字典类型的排序顺序 </p>
     *
     * @see DictionaryTypeDTO
     */
    @Schema(description = "排序")
    private Integer order;
    /**
     * 租户 ID
     * <p> 用于标识不同的租户, 确保数据隔离和权限控制 </p>
     *
     * @see String
     */
    @Schema(description = "租户ID")
    private String tenantId;
    /** 客户端 ID */
    @Schema(description = "客户端ID")
    private String clientId;
}
