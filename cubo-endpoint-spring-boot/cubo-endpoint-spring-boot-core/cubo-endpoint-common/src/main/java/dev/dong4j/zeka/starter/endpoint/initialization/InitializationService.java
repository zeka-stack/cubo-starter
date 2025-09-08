package dev.dong4j.zeka.starter.endpoint.initialization;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;

/**
 * 应用初始化服务接口
 *
 * 该接口定义了应用预热初始化的核心方法，主要用于在应用启动完成后
 * 执行预热操作，以减少用户第一次访问时的响应延迟。
 *
 * 支持 Servlet 和 Reactive 两种不同的 Web 技术栈实现，通过发送 HTTP 请求
 * 来触发各项组件的初始化加载。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:06
 * @since 1.0.0
 */
public interface InitializationService {

    /**
     * 发送预热 REST 请求
     *
     * 默认实现，通过发送 POST 请求到本地的 /warmup 端点来触发预热操作。
     * 该方法会根据当前应用的端口配置自动构建请求 URL。
     *
     * @since 1.0.0
     */
    default void sendWarmUpRestRequest() {
        // 构建本地服务器的基础 URL
        final String baseUrl = "http://localhost:" + ConfigKit.getRestPort();
        // 构建预热端点的完整 URL
        final String warmUpEndpoint = baseUrl + "/warmup";
        // 发送预热请求
        request(warmUpEndpoint);
    }

    /**
     * 发送 HTTP 请求的具体实现
     *
     * 由子类实现，支持不同的 Web 技术栈（Servlet 或 Reactive）。
     * 用于向指定的预热端点发送 POST 请求。
     *
     * @param warmUpEndpoint 预热端点的完整 URL
     * @since 1.0.0
     */
    void request(String warmUpEndpoint);

    /**
     * 创建示例预热请求数据
     *
     * 默认实现，创建一个包含各种数据类型的示例对象，
     * 用于测试 JSON 序列化、参数验证等功能的正常工作。
     *
     * @return 预热请求数据传输对象
     * @since 1.0.0
     */
    default @NotNull WarmUpRequestDTO createSampleMessage() {
        return WarmUpRequestDTO.builder()
            .warmUpString("warm me up")
            .warmUpNumber(15)
            .warmUpBigDecimal(BigDecimal.TEN)
            .warmUpEnumDto(WarmUpEnum.WARM)
            .build();
    }
}
