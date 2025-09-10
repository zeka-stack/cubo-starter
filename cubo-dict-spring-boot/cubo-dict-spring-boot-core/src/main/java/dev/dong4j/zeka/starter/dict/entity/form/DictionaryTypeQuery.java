package dev.dong4j.zeka.starter.dict.entity.form;

import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p> 字典类型表 分页查询参数实体 (根据业务需求添加字段) </p>
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
@Schema(name = "字典类型表-查询")
public class DictionaryTypeQuery extends BaseQuery<Long> {
    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** todo: [自动生成的字段, 避免此实体没有字段导致启动失败的问题, 可删除] */
    @Schema(description = "自动生成的字段, 需要删除!!!")
    private String autoField;
}
