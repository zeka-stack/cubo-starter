package dev.dong4j.zeka.starter.endpoint.reactive;

import dev.dong4j.zeka.starter.endpoint.initialization.InitializationService;
import dev.dong4j.zeka.starter.endpoint.initialization.WarmUpRequestDTO;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Reactive 环境下的初始化服务实现
 *
 * 在 Spring WebFlux Reactive Web 环境下实现预热初始化功能。
 * 使用 Spring WebClient 发送异步非阻塞的 HTTP POST 请求来触发预热操作。
 *
 * 通过向本地的 /warmup 端点发送包含示例数据的 POST 请求，
 * 来预加载 WebFlux、Jackson、验证器等组件。
 *
 * 支持请求超时控制，默认超时时间为 5 秒。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:07
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class ReactiveInitializationService implements InitializationService {
    /** WebClient 构建器，用于创建 HTTP 客户端 */
    private final WebClient.Builder webClientBuilder;

    /**
     * 构造方法
     *
     * 初始化 Reactive 初始化服务，接收 WebClient.Builder 实例
     * 用于创建具有默认配置的 HTTP 客户端。
     *
     * @param webClientBuilder WebClient 构建器实例
     * @since 1.0.0
     */
    public ReactiveInitializationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * 发送 Reactive HTTP 预热请求
     *
     * 使用 WebClient 向指定的预热端点发送异步的 POST 请求。
     * 请求体为 JSON 格式的示例数据，用于触发各项 Reactive 组件的初始化。
     * 设置 5 秒超时时间，并使用 block() 等待请求完成。
     *
     * @param warmUpEndpoint 预热端点的完整 URL
     * @since 1.0.0
     */
    @Override
    public void request(String warmUpEndpoint) {
        // 使用 WebClient 发送异步 POST 请求
        this.webClientBuilder.build().post()
            // 设置请求 URI
            .uri(warmUpEndpoint)
            // 设置 Content-Type 请求头
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            // 设置请求体为 JSON 格式的示例数据
            .body(Mono.just(this.createSampleMessage()), WarmUpRequestDTO.class)
            // 获取响应
            .retrieve()
            // 将响应转换为字符串
            .bodyToMono(String.class)
            // 设置 5 秒超时
            .timeout(Duration.ofSeconds(5))
            // 阻塞等待请求完成
            .block();
    }
}
