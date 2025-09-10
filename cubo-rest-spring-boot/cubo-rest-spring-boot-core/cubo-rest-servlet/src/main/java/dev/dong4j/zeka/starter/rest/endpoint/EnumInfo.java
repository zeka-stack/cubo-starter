package dev.dong4j.zeka.starter.rest.endpoint;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 枚举信息包装类
 * <p>
 * 该类用于封装枚举类的信息，包括枚举类的名称、类型以及所有枚举项的详细信息。
 * 主要用于将枚举类转换为前端可以直接使用的JSON格式，便于前端动态生成下拉框、单选按钮等UI组件。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.11.23 11:04
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnumInfo {
    private String name;
    private String type;
    private List<EnumItem> items;

    /**
     * 枚举项信息类
     * <p>
     * 用于存储单个枚举项的详细信息，包括枚举项的名称、描述和值。
     * 其中：
     * - name: 枚举项的名称，通常是枚举常量的名称
     * - desc: 枚举项的描述，通常是对该枚举项的文字说明
     * - value: 枚举项的值，可以是任意类型，取决于枚举类的实现
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class EnumItem {
        /** 枚举项名称 */
        private String name;
        /** 枚举项描述 */
        private String desc;
        /** 枚举项值 */
        private Object value;
    }
}


