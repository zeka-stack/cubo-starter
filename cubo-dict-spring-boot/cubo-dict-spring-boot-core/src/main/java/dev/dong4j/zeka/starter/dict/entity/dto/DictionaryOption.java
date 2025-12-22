package dev.dong4j.zeka.starter.dict.entity.dto;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典选项类
 * <p> 用于表示字典中的一个选项项, 包含选项值, 标签, 描述, 是否禁用以及排序信息, 通常用于下拉选择等场景
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(name = "字典选项")
public class DictionaryOption implements Serializable {

    /** 序列化 ID, 用于序列化和反序列化时保持版本一致 */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 选项值
     * <p> 表示字典选项的具体值
     *
     * @see DictionaryOption
     */
    @Schema(description = "选项值")
    private String value;

    /** 选项标签 */
    @Schema(description = "选项标签")
    private String label;

    /** 选项描述 */
    @Schema(description = "选项描述")
    private String description;

    /** 是否禁用 */
    @Schema(description = "是否禁用")
    private boolean disabled = false;

    /**
     * 排序
     * <p> 用于定义选项在列表中的顺序
     *
     * @see Integer
     */
    @Schema(description = "排序")
    private Integer sortOrder;
}
