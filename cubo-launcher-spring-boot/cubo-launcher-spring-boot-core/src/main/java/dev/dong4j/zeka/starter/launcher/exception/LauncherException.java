package dev.dong4j.zeka.starter.launcher.exception;

import dev.dong4j.zeka.kernel.common.exception.LowestException;
import java.io.Serial;

/**
 * 启动器相关异常类，继承自 LowestException
 *
 * 该类用于封装启动过程中可能出现的各种异常情况，
 * 包括但不限于：
 * 1. 配置加载异常
 * 2. 初始化失败
 * 3. 依赖检查失败
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.11.17 10:35
 * @since 1.0.0
 */
public class LauncherException extends LowestException {

    @Serial
    private static final long serialVersionUID = -815914023239408247L;

    /**
     * 构造方法，创建带有错误消息的异常实例
     *
     * @param message 错误消息
     * @since 1.0.0
     */
    public LauncherException(String message) {
        super(message);
    }

    /**
     * 构造方法，创建带有错误消息和原因的异常实例
     *
     * @param message 错误消息
     * @param cause   异常原因
     * @since 1.0.0
     */
    public LauncherException(String message, Throwable cause) {
        super(message, cause);
    }
}
