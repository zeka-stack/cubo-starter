package dev.dong4j.zeka.starter.endpoint.servlet;

import cn.hutool.http.HttpUtil;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import dev.dong4j.zeka.starter.endpoint.initialization.InitializationService;
import lombok.extern.slf4j.Slf4j;

/**
 * Servlet 环境下的初始化服务实现
 *
 * 在传统的 Servlet Web 环境下实现预热初始化功能。
 * 使用 Hutool 的 HttpUtil 工具类发送 HTTP POST 请求来触发预热操作。
 *
 * 通过向本地的 /warmup 端点发送包含示例数据的 POST 请求，
 * 来预加载 Spring MVC、Jackson、验证器等组件。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2022.01.21 17:07
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
public class ServletInitializationService implements InitializationService {

    /**
     * 发送 HTTP 预热请求
     *
     * 使用 Hutool 的 HttpUtil 工具类向指定的预热端点发送 POST 请求。
     * 请求体为 JSON 格式的示例数据，用于触发各项组件的初始化。
     * 如果请求失败会记录调试日志但不会抛出异常。
     *
     * @param warmUpEndpoint 预热端点的完整 URL
     * @since 1.0.0
     */
    @Override
    public void request(String warmUpEndpoint) {
        try {
            // 使用 Hutool 发送 POST 请求，请求体为 JSON 格式的示例数据
            HttpUtil.post(warmUpEndpoint, Jsons.toJson(this.createSampleMessage()));
        } catch (Exception e) {
            // 记录调试日志但不中断应用启动
            log.debug("WarmUp Endpoint request error. warmUpEndpoint: [{}] {}", warmUpEndpoint, e.getMessage());
        }
    }
}
