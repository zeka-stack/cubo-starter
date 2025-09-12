package dev.dong4j.zeka.starter.dict.cache;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;

/**
 * 字典缓存预热器
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class DictionaryCachePreloader implements ApplicationRunner {

    private final DictionaryService dictionaryService;
    private final DictionaryCache dictionaryCache;

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
