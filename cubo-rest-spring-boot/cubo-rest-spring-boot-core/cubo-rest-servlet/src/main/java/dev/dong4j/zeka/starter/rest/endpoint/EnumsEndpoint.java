package dev.dong4j.zeka.starter.rest.endpoint;

import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnumCache;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.starter.rest.annotation.RestControllerWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 *     <li>{@code GET /zeka-stack/enums}: 获取所有已注册的枚举列表.</li>
 *     <li>{@code GET /zeka-stack/enums/{type}}: 根据枚举的类型查询特定枚举的详细信息.</li>
 * </ul>
 *
 * <p><b>返回数据结构示例 ({@code GET /zeka-stack/enums}):</b></p>
 * <pre>{@code
 * [
 *   {
 *     "name": "逻辑删除枚举枚举",
 *     "type": "deleted_enum",
 *     "items": [
 *       {
 *         "name": "N",
 *         "desc": "未删除",
 *         "value": false
 *       },
 *       {
 *         "name": "Y",
 *         "desc": "已删除",
 *         "value": true
 *       }
 *     ]
 *   }
 * ]
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

    /**
     * 缓存所有已注册的枚举信息列表
     * <p>
     * 用于存储所有已注册的枚举信息，初始容量为128，以优化内存分配。
     * 主要用于响应获取所有枚举的API请求。
     */
    private static final List<EnumInfo> TOTAL_ENUM_LIST = new ArrayList<>(128);

    /**
     * 缓存所有已注册的枚举信息. Key: 枚举的类型(type), Value: 枚举的详细信息.
     * <p>
     * 使用Map结构存储，便于通过枚举类型快速查找对应的枚举信息。
     * 初始容量为128，以优化内存分配。
     * 主要用于响应根据类型查询枚举的API请求。
     */
    private static final Map<String, EnumInfo> TOTAL_ENUM_MAP = new HashMap<>(128);

    /**
     * 初始化枚举缓存.
     * <p>
     * 此方法在 Spring Bean 初始化后执行. 它会扫描 {@link SerializeEnumCache#SUB_ENUMS} 中所有
     * 实现了 {@link SerializeEnum} 接口的枚举类.
     * 对于每个枚举类, 它会使用 EnumUtils 转换为新的结构, 并将其存储在缓存中.
     *
     * @since 2.0.0
     */
    @PostConstruct
    public void init() {
        TOTAL_ENUM_LIST.clear();
        TOTAL_ENUM_MAP.clear();

        if (CollectionUtils.isNotEmpty(SerializeEnumCache.SUB_ENUMS)) {
            SerializeEnumCache.SUB_ENUMS.forEach(clazz -> {
                EnumInfo enumInfo = EnumUtils.toEnumInfo(clazz);
                TOTAL_ENUM_LIST.add(enumInfo);
                TOTAL_ENUM_MAP.put(enumInfo.getType(), enumInfo);
            });
        }
    }

    /**
     * 通过枚举的类型查询单个枚举的详细信息.
     * <p>
     * 该方法提供了一个RESTful API端点，允许客户端通过枚举的类型查询特定枚举的详细信息。
     * 枚举类型通常是枚举类名称的下划线形式，例如DeletedEnum对应的类型是"deleted_enum"。
     * <p>
     * 请求示例: GET /zeka-stack/enums/deleted_enum
     *
     * @param type 枚举的类型 (e.g., "deleted_enum").
     * @return 返回一个包含该枚举所有值和描述的 Result<EnumInfo> 对象. 如果未找到, 则返回包含null的Result对象.
     * @since 1.0.0
     */
    @GetMapping("/{type}")
    @Operation(summary = "通过枚举类型查询枚举")
    public Result<EnumInfo> findByType(@PathVariable("type") String type) {
        return R.succeed(TOTAL_ENUM_MAP.get(type));
    }

    /**
     * 获取所有已注册的枚举字典.
     * <p>
     * 该方法提供了一个RESTful API端点，返回系统中所有已注册的枚举信息。
     * 这对于前端应用初始化时加载所有枚举数据非常有用，可以一次性获取所有枚举，
     * 而不需要多次调用单个枚举查询API。
     * <p>
     * 请求示例: GET /zeka-stack/enums
     *
     * @return 返回一个包含所有枚举详细信息的Result<List<EnumInfo>>对象.
     * @since 2.0.0
     */
    @GetMapping
    @Operation(summary = "获取所有枚举字典")
    public Result<List<EnumInfo>> findAll() {
        return R.succeed(TOTAL_ENUM_LIST);
    }
}
