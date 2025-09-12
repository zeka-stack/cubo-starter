package dev.dong4j.zeka.starter.dict.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典选项DTO（用于前端下拉框等展示）
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(name = "字典选项")
public class DictionaryOption implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 选项值 */
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

    /** 排序 */
    @Schema(description = "排序")
    private Integer sortOrder;
}
