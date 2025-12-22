package dev.dong4j.zeka.starter.dict.cache;

import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典缓存预热器
 * <p> 在应用启动时预热字典缓存, 提高后续字典数据访问的效率. 如果字典缓存未启用, 则跳过预热过程.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
@AllArgsConstructor
public class DictionaryCachePreloader implements ApplicationRunner {

    /** 字典服务, 用于获取字典类型和字典值数据 */
    private final DictionaryService dictionaryService;
    /** 字典缓存实例, 用于预热字典数据到缓存中 */
    private final DictionaryCache dictionaryCache;

    /**
     * 执行字典缓存预热操作
     * <p> 在应用启动时加载所有字典类型及其对应的字典值, 并存入缓存中. 如果字典缓存未启用, 则跳过预热.</p>
     *
     * @param args 应用启动参数
     */
    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        if (!dictionaryCache.isEnabled()) {
            log.info("字典缓存未启用，跳过预热");
            return;
        }

        log.info("开始预热字典缓存...");
        try {
            // 预热所有字典数据
            List<DictionaryType> types = dictionaryService.listDictionaryTypes();
            Map<String, List<DictionaryValue>> allData = types.stream()
                .collect(Collectors.toMap(
                    DictionaryType::getCode,
                    type -> dictionaryService.getDictionaryValues(type.getCode())
                                         ));

            dictionaryCache.putAll(allData);
            log.info("字典缓存预热完成，共加载 {} 个字典类型", allData.size());
        } catch (Exception e) {
            log.error("字典缓存预热失败", e);
        }
    }
}
