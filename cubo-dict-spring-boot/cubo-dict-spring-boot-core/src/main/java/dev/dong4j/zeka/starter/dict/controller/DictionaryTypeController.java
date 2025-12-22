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
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryTypeDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryTypeForm;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryTypeQuery;
import dev.dong4j.zeka.starter.dict.service.DictionaryTypeService;
import dev.dong4j.zeka.starter.rest.ServletController;
import dev.dong4j.zeka.starter.rest.annotation.RestControllerWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;

/**
 * 字典类型表控制器类
 * <p> 提供字典类型相关的业务逻辑处理, 包括字典类型的列表查询, 分页查询, 新增, 详情, 修改和删除等操作
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Tag(name = "字典类型表接口")
@AllArgsConstructor
@RestControllerWrapper("/dicts/type")
public class DictionaryTypeController extends ServletController {

    /** DictionaryType 服务接口, 用于操作字典类型相关数据 */
    private final DictionaryTypeService dictionaryTypeService;

    /**
     * 根据给定的查询参数查询字典类型表的所有数据
     * <p> 此方法用于根据查询条件获取字典类型表中的所有记录, 并返回一个包含字典类型数据的对象集合.</p>
     *
     * @param query 包含查询条件的参数对象
     * @return 包含字典类型数据的对象集合
     * @since 1.0.0
     */
    @GetMapping("/list")
    @Operation(summary = "列表查询")
    @ApiOperationSupport(order = 1)
    public List<DictionaryTypeDTO> list(@ParameterObject DictionaryTypeQuery query) {
        return this.dictionaryTypeService.list(query);
    }

    /**
     * 分页查询字典类型数据
     * <p> 根据查询参数进行分页查询, 返回分页结果数据 </p>
     *
     * @param query 查询参数对象, 用于指定查询条件
     * @return 分页数据对象, 包含分页结果信息
     * @since 1.0.0
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiOperationSupport(order = 2)
    public IPage<DictionaryTypeDTO> pages(@ParameterObject DictionaryTypeQuery query) {
        return this.dictionaryTypeService.page(query);
    }

    /**
     * 新增字典类型数据
     * <p> 根据传入的参数实体创建新的字典类型记录 </p>
     *
     * @param form 包含字典类型信息的参数实体
     * @since 1.0.0
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @ApiOperationSupport(order = 3)
    public void create(@Validated @RequestBody DictionaryTypeForm form) {
        this.dictionaryTypeService.create(form);
    }

    /**
     * 通过主键查询单条数据
     * <p> 根据给定的主键 ID 查询并返回单条数据
     *
     * @param id 主键 ID
     * @return 单条数据
     * @since 1.0.0
     */
    @GetMapping("/{id}")
    @Operation(summary = "详情")
    @ApiOperationSupport(order = 4)
    public DictionaryTypeDTO detail(@PathVariable Long id) {
        return this.dictionaryTypeService.detail(id);
    }

    /**
     * 修改数据
     * <p> 通过主键 ID 更新数据, 验证参数并调用服务层进行修改操作
     *
     * @param id   主键 ID
     * @param form 参数实体
     * @since 1.0.0
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改数据")
    @ApiOperationSupport(order = 5)
    public void edit(@PathVariable Long id, @Validated(value = {UpdateGroup.class, Default.class}) @RequestBody DictionaryTypeForm form) {
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(id.equals(form.getId()), "id 不一致");
        BaseCodes.DATA_ERROR.notNull(dictionaryTypeService.getById(form.getId()), "指定的数据不存在: " + id);
        this.dictionaryTypeService.edit(form);
    }

    /**
     * 删除数据
     * <p> 根据主键集合删除数据, 若传入的主键集合为空则抛出异常 </p>
     *
     * @param ids 主键集合
     * @since 1.0.0
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @ApiOperationSupport(order = 6)
    public void remove(@RequestBody List<Long> ids) {
        BaseCodes.DATA_ERROR.notEmpty(ids, "带删除的数据标识不能为空");
        this.dictionaryTypeService.removeByIds(ids);
    }

}
