package dev.dong4j.zeka.starter.endpoint.initialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.StopWatch;

/**
 * 应用预加载组件
 *
 * 该组件在应用启动完成后自动执行预热逻辑，通过模拟真实的请求场景
 * 来触发 Spring Boot 应用的各项初始化操作，包括：
 *
 * 1. Jackson JSON 序列化/反序列化器的初始化
 * 2. Bean Validation 验证器的初始化
 * 3. HTTP 客户端和连接池的初始化
 * 4. 其他可能导致第一次访问慢的组件
 *
 * 这样可以显著减少用户第一次访问时的响应延迟。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:27
 * @since 1.0.0
 */
@Slf4j
public class PreloadComponent {
    /** 初始化服务接口实现 */
    private final InitializationService service;
    /** JSON 序列化器实例 */
    private final ObjectMapper objectMapper = Jsons.getInstance();

    /**
     * 构造方法
     *
     * @param service 初始化服务实例，可能为 null（如果未找到对应的 Bean）
     * @since 1.0.0
     */
    public PreloadComponent(InitializationService service) {
        this.service = service;
    }

    /**
     * 应用启动完成事件处理器
     *
     * 在接收到 ApplicationReadyEvent 事件后，执行预热逻辑，
     * 包括手动验证、JSON 序列化和发送预热请求。
     * 使用 StopWatch 统计预热耗时。
     *
     * @param event 应用就绪事件
     * @since 1.0.0
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("应用启动完成, 开启执行预热逻辑");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 执行手动验证预热
        this.manualValidation();
        // 执行 JSON 序列化预热
        this.mapJson();
        // 发送 HTTP 预热请求
        this.service.sendWarmUpRestRequest();
        stopWatch.stop();
        log.info("服务预热完成, 第一次请求: {} ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * 执行 JSON 序列化预热
     *
     * 通过反序列化一个测试 JSON 字符串来预热 Jackson 序列化器，
     * 确保第一次真实请求时 JSON 处理已经被初始化。
     * 忽略可能的序列化异常，仅用于预热目的。
     *
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:OperatorWrap")
    private void mapJson() {
        // 测试用的 JSON 字符串，包含多种数据类型
        final String json = "{\n" +
            "    \"inputMessage\": \"abc\",\n" +
            "    \"someNumber\": 123.4,\n" +
            "    \"patternString\": \"this is a fixed string\",\n" +
            "    \"selectOne\": \"TWO\"\n" +
            "}";

        try {
            // 执行 JSON 反序列化预热
            this.objectMapper.readValue(json, WarmUpRequestDTO.class);
        } catch (JsonProcessingException ignored) {
            // 忽略序列化异常，仅为预热目的
        }
    }

    /**
     * 执行手动验证预热
     *
     * 通过创建 Bean Validation 验证器并执行一次验证操作来预热验证组件，
     * 确保第一次真实请求时验证器已经被初始化和加载。
     * 忽略验证结果，仅用于预热目的。
     *
     * @since 1.0.0
     */
    private void manualValidation() {
        // 创建验证器工厂
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // 获取验证器实例
        Validator validator = factory.getValidator();
        // 执行一次验证操作来预热验证组件
        Set<ConstraintViolation<WarmUpRequestDTO>> violations = validator.validate(this.service.createSampleMessage());
    }

}
