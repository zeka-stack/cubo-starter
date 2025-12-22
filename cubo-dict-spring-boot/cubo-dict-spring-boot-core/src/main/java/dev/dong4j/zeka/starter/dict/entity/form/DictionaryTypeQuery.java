package dev.dong4j.zeka.starter.dict.entity.form;

import java.io.Serial;

import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 字典类型查询类
 * <p> 用于字典类型的查询操作, 继承自 BaseQuery 类, 并提供了链式调用和序列化等功能
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
@Schema(name = "字典类型表-查询")
public class DictionaryTypeQuery extends BaseQuery<Long> {
    /** 序列化版本标识符, 用于兼容性控制 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 自动生成的字段, 需要删除!!! */
    @Schema(description = "自动生成的字段, 需要删除!!!")
    private String autoField;
}
