package dev.dong4j.zeka.starter.mybatis.dict;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 13:37
 * @since 2.0.0
 */
@Data
@AllArgsConstructor
public class FieldProperty {
    /** 属性名 */
    private String name;
    /** 属性绑定的注解 */
    private FieldBind fieldBind;
}
