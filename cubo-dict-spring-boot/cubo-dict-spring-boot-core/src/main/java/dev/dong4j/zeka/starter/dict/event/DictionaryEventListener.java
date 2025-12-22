package dev.dong4j.zeka.starter.dict.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典事件监听器类
 * <p> 用于监听字典更新事件, 并根据事件类型执行相应的缓存清除或刷新操作, 确保字典数据在缓存中的一致性.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
@AllArgsConstructor
public class DictionaryEventListener {

    /** 字典服务, 用于处理字典相关的业务逻辑 */
    private final DictionaryService dictionaryService;
    /**
     * 字典缓存服务实例
     * <p>
     * 用于存储和管理字典数据的缓存, 支持根据字典类型码进行缓存操作, 如移除和刷新缓存.
     *
     * @see DictionaryCache
     */
    private final DictionaryCache dictionaryCache;

    /**
     * 监听字典更新事件并处理相应的缓存操作
     * <p> 当接收到字典更新事件时, 根据操作类型执行对应的缓存清除或刷新操作.
     * 支持的操作类型包括创建, 更新, 删除和刷新. 对于未知的操作类型会记录警告日志.
     *
     * @param event 字典更新事件对象, 包含事件类型码, 操作类型和描述信息
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
