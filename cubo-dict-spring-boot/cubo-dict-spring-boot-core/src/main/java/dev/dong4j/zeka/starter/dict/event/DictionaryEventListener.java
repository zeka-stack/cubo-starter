package dev.dong4j.zeka.starter.dict.event;

import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * 字典事件监听器
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class DictionaryEventListener {

    private final DictionaryService dictionaryService;
    private final DictionaryCache dictionaryCache;

    /**
     * 监听字典更新事件
     *
     * @param event 字典更新事件
     */
    @EventListener
    @Async
    public void handleDictionaryUpdateEvent(DictionaryUpdateEvent event) {
        log.info("收到字典更新事件: typeCode={}, operationType={}, description={}",
            event.getTypeCode(), event.getOperationType(), event.getDescription());

        try {
            // 根据操作类型处理缓存
            switch (event.getOperationType()) {
                case CREATE:
                case UPDATE:
                case DELETE:
                    // 删除缓存，下次查询时重新加载
                    dictionaryCache.remove(event.getTypeCode());
                    log.debug("已清除字典类型 [{}] 的缓存", event.getTypeCode());
                    break;
                case REFRESH:
                    // 刷新缓存
                    dictionaryService.refreshCache(event.getTypeCode());
                    log.debug("已刷新字典类型 [{}] 的缓存", event.getTypeCode());
                    break;
                default:
                    log.warn("未知的操作类型: {}", event.getOperationType());
            }
        } catch (Exception e) {
            log.error("处理字典更新事件时发生错误: typeCode={}, operationType={}",
                event.getTypeCode(), event.getOperationType(), e);
        }
    }
}
