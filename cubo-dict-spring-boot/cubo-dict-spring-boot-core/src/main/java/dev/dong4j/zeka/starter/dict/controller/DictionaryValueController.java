package dev.dong4j.zeka.starter.dict.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.validation.group.UpdateGroup;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryValueDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryValueForm;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryValueQuery;
import dev.dong4j.zeka.starter.dict.service.DictionaryValueService;
import dev.dong4j.zeka.starter.rest.ServletController;
import dev.dong4j.zeka.starter.rest.annotation.RestControllerWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;

/**
 * 字典值表接口控制器
 * <p> 提供字典值表相关的 RESTful 接口, 支持列表查询, 分页查询, 新增, 详情查看, 修改和删除等操作, 用于管理字典值数据.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Tag(name = "字典值表接口")
@AllArgsConstructor
@RestControllerWrapper("/dicts/value")
public class DictionaryValueController extends ServletController {

    /** DictionaryValue 服务接口, 用于操作字典值相关数据 */
    private final DictionaryValueService dictionaryValueService;

    /**
     * 根据条件查询全部数据
     * <p> 通过给定的查询参数查询字典值表的所有记录, 并返回结果列表 </p>
     *
     * @param query 查询参数, 用于指定查询条件
     * @return 包含查询结果的字典值表对象集合
     * @since 1.0.0
     */
    @GetMapping("/list")
    @Operation(summary = "列表查询")
    @ApiOperationSupport(order = 1)
    public List<DictionaryValueDTO> list(@ParameterObject DictionaryValueQuery query) {
        return this.dictionaryValueService.list(query);
    }

    /**
     * 分页查询字典值表数据
     * <p> 根据给定的查询参数进行分页查询, 并返回分页数据
     *
     * @param query 查询参数, 包含分页信息和其他查询条件
     * @return 包含分页信息的字典值表数据集合
     * @since 1.0.0
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiOperationSupport(order = 2)
    public IPage<DictionaryValueDTO> pages(@ParameterObject DictionaryValueQuery query) {
        return this.dictionaryValueService.page(query);
    }

    /**
     * 新增数据
     * <p> 根据传入的参数实体新增一条数据 </p>
     *
     * @param form 参数实体, 包含新增数据的详细信息
     * @since 1.0.0
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @ApiOperationSupport(order = 3)
    public void create(@Validated @RequestBody DictionaryValueForm form) {
        this.dictionaryValueService.create(form);
    }

    /**
     * 通过主键查询单条数据
     * <p> 根据给定的主键 ID 查找并返回对应的字典值表数据对象 </p>
     *
     * @param id 主键, 用于标识要查询的字典值表记录
     * @return 字典值表数据对象 DictionaryValueDTO
     * @since 1.0.0
     */
    @GetMapping("/{id}")
    @Operation(summary = "详情")
    @ApiOperationSupport(order = 4)
    public DictionaryValueDTO detail(@PathVariable Long id) {
        return this.dictionaryValueService.detail(id);
    }

    /**
     * 修改数据
     * <p> 通过主键 ID 修改字典值表中的数据. 首先验证传入的 ID 与表单中的 ID 是否一致, 并检查指定的数据是否存在.</p>
     *
     * @param id   数据的主键 ID
     * @param form 包含更新信息的字典值表表单实体
     * @since 1.0.0
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改数据")
    @ApiOperationSupport(order = 5)
    public void edit(@PathVariable Long id, @Validated(value = {UpdateGroup.class, Default.class}) @RequestBody DictionaryValueForm form) {
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(id.equals(form.getId()), "id 不一致");
        BaseCodes.DATA_ERROR.notNull(dictionaryValueService.getById(form.getId()), "指定的数据不存在: " + id);
        this.dictionaryValueService.edit(form);
    }

    /**
     * 删除数据
     * <p> 根据提供的主键集合删除对应的字典值数据 </p>
     *
     * @param ids 主键集合, 包含要删除的数据标识
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @ApiOperationSupport(order = 6)
    public void remove(@RequestBody List<Long> ids) {
        BaseCodes.DATA_ERROR.notEmpty(ids, "带删除的数据标识不能为空");
        this.dictionaryValueService.removeByIds(ids);
    }

}
