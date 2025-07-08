package dev.dong4j.zeka.starter.mybatis.handler;

import dev.dong4j.zeka.kernel.common.event.BaseEventHandler;
import dev.dong4j.zeka.kernel.common.event.SqlExecuteTimeoutEvent;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * <p>Description: 异步监听日志事件 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.05.30 14:22
 * @since 1.9.5
 */
public class SqlExecuteTimeoutHandler extends BaseEventHandler<SqlExecuteTimeoutEvent> {
    /** 日志输出到指定的文件 */
    private static final Logger log = LoggerFactory.getLogger("sql.timing");

    /**
     * 发送日志
     *
     * @param event event
     * @since 1.0.0
     */
    @Order
    @Async
    @Override
    @EventListener
    public void handler(@NotNull SqlExecuteTimeoutEvent event) {
        Map<String, Object> source = event.getSource();
        log.warn("{}", source.get("formatSql"));
    }
}
