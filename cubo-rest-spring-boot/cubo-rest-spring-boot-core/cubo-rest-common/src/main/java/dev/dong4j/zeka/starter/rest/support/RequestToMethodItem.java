package dev.dong4j.zeka.starter.rest.support;

import lombok.Builder;
import lombok.Data;

/**
 * 请求到方法的映射信息实体类
 *
 * 该类用于封装 HTTP 请求与 Controller 方法之间的映射关系信息。
 * 主要用于路由分析、接口文档生成、性能监控等场景。
 *
 * 包含信息：
 * 1. 请求类型（GET、POST、PUT、DELETE 等）
 * 2. 请求 URL 路径
 * 3. 控制器类名
 * 4. 处理方法名
 * 5. 方法参数类型列表
 *
 * 使用场景：
 * - 接口文档自动生成（如 Swagger）
 * - 请求路由分析和监控
 * - API 权限控制和安全检查
 * - 接口调用统计和性能分析
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Data
@Builder
public class RequestToMethodItem {
    /** HTTP 请求类型（GET、POST、PUT、DELETE 等） */
    private String requestType;
    /** 请求的 URL 路径 */
    private String requestUrl;
    /** 处理请求的 Controller 类名 */
    private String controllerName;
    /** 处理请求的方法名 */
    private String requestMethodName;
    /** 方法参数类型数组 */
    private Class<?>[] methodParamTypes;
}
