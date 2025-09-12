package dev.dong4j.zeka.starter.dict.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import dev.dong4j.zeka.starter.rest.ServletController;
import dev.dong4j.zeka.starter.rest.annotation.RestControllerWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 字典控制器（前端接口）
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Tag(name = "字典管理接口")
@AllArgsConstructor
@RestControllerWrapper("/dicts")
public class DictionaryController extends ServletController {

    private final DictionaryService dictionaryService;

    /**
     * 根据类型编码获取字典选项列表
     *
     * @param typeCode 字典类型编码
     * @return 字典选项列表
     * @since 1.0.0
     */
    @GetMapping("/options/{typeCode}")
    @Operation(summary = "根据类型编码获取字典选项列表")
    @ApiOperationSupport(order = 1)
    public Result<List<DictionaryOption>> getDictionaryOptions(
        @Parameter(description = "字典类型编码", required = true)
        @PathVariable String typeCode) {
        List<DictionaryOption> options = dictionaryService.getDictionaryOptions(typeCode);
        return R.succeed(options);
    }

    /**
     * 获取所有字典选项
     *
     * @return 所有字典选项
     * @since 1.0.0
     */
    @GetMapping("/options")
    @Operation(summary = "获取所有字典选项")
    @ApiOperationSupport(order = 2)
    public Result<Map<String, List<DictionaryOption>>> getAllDictionaryOptions() {
        Map<String, List<DictionaryOption>> options = dictionaryService.getAllDictionaryOptions();
        return R.succeed(options);
    }

    /**
     * 刷新指定字典类型缓存
     *
     * @param typeCode 字典类型编码
     * @return 操作结果
     * @since 1.0.0
     */
    @PostMapping("/refresh/{typeCode}")
    @Operation(summary = "刷新指定字典类型缓存")
    @ApiOperationSupport(order = 3)
    public Result<Void> refreshCache(
        @Parameter(description = "字典类型编码", required = true)
        @PathVariable String typeCode) {
        dictionaryService.refreshCache(typeCode);
        return R.succeed();
    }

    /**
     * 刷新所有字典缓存
     *
     * @return 操作结果
     * @since 1.0.0
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新所有字典缓存")
    @ApiOperationSupport(order = 4)
    public Result<Void> refreshAllCache() {
        dictionaryService.refreshAllCache();
        return R.succeed();
    }

    /**
     * 清除指定字典类型缓存
     *
     * @param typeCode 字典类型编码
     * @return 操作结果
     * @since 1.0.0
     */
    @PostMapping("/clear/{typeCode}")
    @Operation(summary = "清除指定字典类型缓存")
    @ApiOperationSupport(order = 5)
    public Result<Void> clearCache(
        @Parameter(description = "字典类型编码", required = true)
        @PathVariable String typeCode) {
        dictionaryService.clearCache(typeCode);
        return R.succeed();
    }

    /**
     * 清除所有字典缓存
     *
     * @return 操作结果
     * @since 1.0.0
     */
    @PostMapping("/clear")
    @Operation(summary = "清除所有字典缓存")
    @ApiOperationSupport(order = 6)
    public Result<Void> clearAllCache() {
        dictionaryService.clearAllCache();
        return R.succeed();
    }
}
