package dev.dong4j.zeka.starter.rest.runner;

import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.kernel.common.util.SystemUtils;
import dev.dong4j.zeka.kernel.common.util.ThreadUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

/**
 * 自动打开浏览器运行器
 *
 * 该组件用于在应用启动完成后自动打开浏览器访问应用首页。
 * 主要用于开发环境下提高开发者的体验，在本地调试时无需手动输入 URL。
 *
 * 功能特点：
 * 1. 支持多平台：macOS、Windows、Linux 等
 * 2. 仅在本地开发环境下启用，生产环境自动禁用
 * 3. 可通过配置开关控制是否启用
 * 4. 在应用启动后延迟 2 秒打开，确保服务完全就绪
 *
 * 触发条件：
 * - 配置了 zeka-stack.rest.enable-browser=true
 * - 当前为本地开发环境
 * - 不是 JUnit 测试启动
 * - START_URL 系统属性不为空
 *
 * 安全考虑：
 * 仅在本地环境启用，生产环境不会执行打开浏览器操作。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.06 19:08
 * @since 1.0.0
 */
@Slf4j
@Order
public class OpenBrowserRunner implements CommandLineRunner {

    /** 是否允许自动打开浏览器的标识 */
    private final boolean isEnableBrowser;

    /**
     * 构造方法，初始化浏览器打开功能
     *
     * 通过构造参数接收配置信息，决定是否在应用启动后自动打开浏览器。
     * 该参数通常来自 Spring Boot 的配置属性。
     *
     * @param isEnableBrowser 是否允许自动打开浏览器，true 表示允许
     * @since 1.0.0
     */
    @Contract(pure = true)
    public OpenBrowserRunner(boolean isEnableBrowser) {
        this.isEnableBrowser = isEnableBrowser;
    }

    /**
     * 在应用启动完成后执行浏览器打开逻辑
     *
     * 该方法作为 Spring Boot 的 CommandLineRunner 实现，
     * 会在应用启动完成后被自动调用。
     *
     * 执行流程：
     * 1. 在独立线程中执行，避免阻塞主线程
     * 2. 检查各种条件（本地环境、非测试启动等）
     * 3. 延迟 2 秒后打开浏览器，确保服务完全就绪
     * 4. 捕获并处理可能出现的异常
     *
     * @param gs 命令行参数数组（本方法中未使用）
     * @since 1.0.0
     */
    @Override
    public void run(String... gs) {
        ThreadUtils.execute(() -> {
            // 设置线程名称，方便调试和监控
            Thread.currentThread().setName("browse");
            // 获取启动 URL，该值通常由框架自动设置
            String url = System.getProperty(App.START_URL);
            // 检查是否为本地开发环境
            boolean isLocalLaunch = ConfigKit.isLocalLaunch();
            if (isLocalLaunch
                && !App.START_JUNIT.equals(System.getProperty(App.START_TYPE))
                && StringUtils.isNotBlank(url)
                && this.isEnableBrowser) {
                log.debug("will open browse to access {}, "
                    + "you can set zeka-stack.rest.enable-browser=false to close this function, "
                    + "not local env default close", url);
                // 延迟 2 秒后打开浏览器，确保服务完全就绪
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                OpenBrowserRunner.browse(url);
            }
        });
    }

    /**
     * 在不同操作系统上打开浏览器访问指定 URL
     *
     * 该静态方法封装了各种操作系统下打开浏览器的逻辑，
     * 实现了跨平台的浏览器打开功能。
     *
     * 支持的平台：
     * 1. macOS：使用 Apple 的 FileManager API
     * 2. Windows：使用 rundll32 命令
     * 3. Linux/Unix：尝试多个常见浏览器
     *
     * 异常处理：
     * 所有异常都会被捕获并记录日志，不会影响应用正常运行。
     *
     * @param url 要在浏览器中打开的 URL 地址
     * @since 1.0.0
     */
    private static void browse(String url) {
        // 获取操作系统的名称，根据不同系统采用不同的打开策略
        try {
            if (SystemUtils.isMac()) {
                // macOS 系统：使用 Apple 原生 API 打开 URL
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                fileMgr.getDeclaredMethod("openURL", String.class).invoke(null, url);
            } else if (SystemUtils.isWindows()) {
                // Windows 系统：使用 rundll32 命令调用默认浏览器
                Runtime.getRuntime().exec(
                    "rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                // Unix 或 Linux 系统：尝试多个常见浏览器
                String[] browsers = {
                    "firefox",
                    "opera",
                    "konqueror",
                    "epiphany",
                    "mozilla",
                    "netscape"
                };
                String browser = null;
                // 遍历浏览器列表，找到第一个可用的浏览器
                // 这里是如果进程创建成功了，==0 表示正常结束
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime()
                        .exec(new String[]{"which", browsers[count]})
                        .waitFor() == 0) {
                        browser = browsers[count];
                    }
                }

                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    // 使用找到的浏览器打开 URL
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }

        } catch (Exception e) {
            // 非业务错误, 吞掉异常堆栈信息
            log.error("open browse error: {}", e.getMessage());
        }
    }
}
