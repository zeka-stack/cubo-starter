package dev.dong4j.zeka.starter.rest;

import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnumCache;
import dev.dong4j.zeka.kernel.common.util.ClassUtils;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.starter.rest.annotation.RestControllerWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 提供一个 RESTful 端点, 用于查询在系统中注册的所有实现了 {@link SerializeEnum} 接口的枚举.
 * 该端点会自动扫描所有 {@link SerializeEnum} 的子类, 并将它们的值和描述缓存起来, 以便通过 HTTP API 进行查询.
 * 主要用于前端动态生成下拉框、单选按钮等需要枚举数据的场景.
 *
 * <p><b>API 端点:</b></p>
 * <ul>
 *     <li>{@code GET /zeka-stack/enums}: 获取所有已注册的枚举. 返回一个 Map, Key 为枚举的简单类名, Value 为该枚举的详细信息.</li>
 *     <li>{@code GET /zeka-stack/enums/{name}}: 根据枚举的简单类名查询特定枚举的详细信息.</li>
 * </ul>
 *
 * <p><b>返回数据结构示例 ({@code GET /zeka-stack/enums}):</b></p>
 * <pre>{@code
 * {
 *   "YourEnumName": {
 *     "VALUE1": "Description1",
 *     "VALUE2": "Description2",
 *     "type": "String"
 *   },
 *   "AnotherEnumName": {
 *     "KEY_A": "Description A",
 *     "KEY_B": "Description B",
 *     "type": "Integer"
 *   }
 * }
 * }</pre>
 * </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.11.23 11:04
 * @see SerializeEnum
 * @see SerializeEnumCache
 * @since 1.0.0
 */
@Slf4j
@RestControllerWrapper("/zeka-stack/enums")
@Tag(name = "枚举值查询服务")
public class EnumsEndpoint {

    /** 缓存所有已注册的枚举信息. Key: 枚举的简单类名, Value: 枚举的 (value -> description) 映射. */
    private static final Map<String, Map<Object, Object>> TOTAL_ENUM = new HashMap<>(128);

    /**
     * 初始化枚举缓存.
     * <p>
     * 此方法在 Spring Bean 初始化后执行. 它会扫描 {@link SerializeEnumCache#SUB_ENUMS} 中所有
     * 实现了 {@link SerializeEnum} 接口的枚举类.
     * 对于每个枚举类, 它会提取枚举的 value 和 description, 并将其存储在 {@link #TOTAL_ENUM} 映射中.
     * 同时, 它还会记录枚举的父类信息, 以便进行分类.
     *
     * @since 2.0.0
     */
    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(SerializeEnumCache.SUB_ENUMS)) {
            SerializeEnumCache.SUB_ENUMS.forEach(clazz -> {
                Map<Object, Object> map = Arrays.stream(clazz.getEnumConstants())
                    .collect(Collectors.toMap(SerializeEnum::getValue, SerializeEnum::getDesc, (k1, k2) -> k2));
                //  记录枚举的 value 类型
                map.put("type", ClassUtils.getInterfaceT(clazz, SerializeEnum.class, 0).getSimpleName());
                TOTAL_ENUM.put(clazz.getSimpleName(), map);
            });
        }
    }

    /**
     * 通过枚举的简单类名查询单个枚举的详细信息.
     *
     * @param name 枚举的简单类名 (e.g., "GenderEnum").
     * @return 返回一个包含该枚举所有值和描述的 Map. 如果未找到, 则返回 null.
     * @since 1.0.0
     */
    @GetMapping("/{name}")
    @Operation(summary = "通过枚举名称查询枚举")
    public Result<Map<Object, Object>> findByName(@PathVariable("name") String name) {
        return R.succeed(TOTAL_ENUM.get(name));
    }

    /**
     * 获取所有已注册的枚举字典.
     *
     * @return 返回一个 Map, Key 是枚举的简单类名, Value 是包含该枚举详细信息的 Map.
     * @since 2.0.0
     */
    @GetMapping
    @Operation(summary = "获取所有枚举字典")
    public Result<Map<String, Map<Object, Object>>> findAll() {
        return R.succeed(TOTAL_ENUM);
    }
}
