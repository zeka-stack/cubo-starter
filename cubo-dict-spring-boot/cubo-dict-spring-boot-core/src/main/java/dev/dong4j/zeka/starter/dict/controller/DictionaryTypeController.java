package dev.dong4j.zeka.starter.dict.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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
import java.util.List;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p> 字典类型表 控制器 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Tag(name = "字典类型表接口")
@AllArgsConstructor
@RestControllerWrapper("/dictionary-type")
public class DictionaryTypeController extends ServletController {

    /** DictionaryType service */
    private final DictionaryTypeService dictionaryTypeService;

    /**
     * 根据条件查询全部数据
     *
     * @param query 查询参数
     * @return 对象集合
     * @since 1.0.0
     */
    @GetMapping("/list")
    @Operation(summary = "列表查询")
    @ApiOperationSupport(order = 1)
    public List<DictionaryTypeDTO> list(@ParameterObject DictionaryTypeQuery query) {
        return this.dictionaryTypeService.list(query);
    }

    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页数据
     * @since 1.0.0
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiOperationSupport(order = 2)
    public IPage<DictionaryTypeDTO> pages(@ParameterObject DictionaryTypeQuery query) {
        return this.dictionaryTypeService.page(query);
    }

    /**
     * 新增数据
     *
     * @param form 参数实体
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
     *
     * @param id 主键
     * @return dto 单条数据
     * @since 1.0.0
     */
    @GetMapping("/{id}")
    @Operation(summary = "详情")
    @ApiOperationSupport(order = 4)
    public DictionaryTypeDTO detail(@PathVariable("id") Long id) {
        return this.dictionaryTypeService.detail(id);
    }

    /**
     * 修改数据
     *
     * @param id   主键
     * @param form 参数实体
     * @since 1.0.0
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改数据")
    @ApiOperationSupport(order = 5)
    public void edit(@PathVariable("id") Long id, @Validated({UpdateGroup.class, Default.class}) @RequestBody DictionaryTypeForm form) {
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(id.equals(form.getId()), "id 不一致");
        BaseCodes.DATA_ERROR.notNull(dictionaryTypeService.getById(form.getId()), "指定的数据不存在: " + id);
        this.dictionaryTypeService.edit(form);
    }

    /**
     * 删除数据
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
