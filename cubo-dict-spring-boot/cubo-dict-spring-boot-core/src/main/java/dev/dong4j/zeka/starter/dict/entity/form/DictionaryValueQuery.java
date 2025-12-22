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
 * 字典值表查询类
 * <p> 用于封装字典值表相关的查询条件和参数, 继承自基础查询类 BaseQuery, 提供对字典值表数据的查询支持.
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
@Schema(name = "字典值表-查询")
public class DictionaryValueQuery extends BaseQuery<Long> {
    /**
     * 序列化版本 UID
     * <p>
     * 用于支持序列化的版本控制, 确保在反序列化时与序列化时的类版本一致.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自动生成的字段, 需要删除!!!
     *
     * @deprecated 该字段为自动生成, 无实际用途, 建议删除.
     */
    @Schema(description = "自动生成的字段, 需要删除!!!")
    private String autoField;
}
