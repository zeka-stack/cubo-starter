package dev.dong4j.zeka.starter.rest;

import dev.dong4j.zeka.kernel.common.api.GeneralResult;

/**
 * 反应式 Web 控制器基类
 *
 * 该抽象类为基于 WebFlux 的反应式 Web 应用提供了统一的控制器基类。
 * 它继承了框架的 GeneralResult 接口，为反应式控制器提供了统一的
 * 响应结果处理能力和标准化的开发模式。
 *
 * 主要功能：
 * 1. 提供统一的响应结果处理能力
 * 2. 为反应式控制器提供基础的开发约束
 * 3. 集成框架的标准响应格式和处理逻辑
 * 4. 简化反应式 Web 应用的开发流程
 * 5. 为后续的功能扩展提供基础支持
 *
 * 设计特点：
 * - 抽象类设计，强制子类继承而非直接使用
 * - 实现 GeneralResult 接口，与框架的响应处理机制集成
 * - 遵循反应式编程模型，支持非阻塞式处理
 * - 与 WebFlux 的自动配置机制无缝集成
 *
 * 使用方式：
 * ```java
 * @RestController
 * @RequestMapping("/api/v1")
 * public class UserController extends ReactiveController {
 *
 *     @GetMapping("/users")
 *     public Mono<R<List<User>>> getUsers() {
 *         // 利用继承的 GeneralResult 方法构建响应
 *         return Mono.just(success(userList));
 *     }
 * }
 * ```
 *
 * 继承优势：
 * - 自动获得框架的响应结果构建方法
 * - 统一的错误处理和响应格式
 * - 与框架其他组件的无缝集成
 * - 符合框架的开发规范和最佳实践
 *
 * 注意事项：
 * - 该类只适用于 WebFlux 反应式环境
 * - 子类应该遵循反应式编程的最佳实践
 * - 建议与框架的其他反应式组件配合使用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.24 20:42
 * @since 1.0.0
 */
public abstract class ReactiveController implements GeneralResult {
}
