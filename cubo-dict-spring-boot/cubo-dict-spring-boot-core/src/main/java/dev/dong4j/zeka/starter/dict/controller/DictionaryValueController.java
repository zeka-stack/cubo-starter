package dev.dong4j.zeka.starter.dict.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
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
 * <p> 字典值表 控制器 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Tag(name = "字典值表接口")
@AllArgsConstructor
@RestControllerWrapper("/dictionary-value")
public class DictionaryValueController extends ServletController {

    /** DictionaryValue service */
    private final DictionaryValueService dictionaryValueService;

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
    public List<DictionaryValueDTO> list(@ParameterObject DictionaryValueQuery query) {
        return this.dictionaryValueService.list(query);
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
    public IPage<DictionaryValueDTO> pages(@ParameterObject DictionaryValueQuery query) {
        return this.dictionaryValueService.page(query);
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
    public void create(@Validated @RequestBody DictionaryValueForm form) {
        this.dictionaryValueService.create(form);
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
    public DictionaryValueDTO detail(@PathVariable("id") Long id) {
        return this.dictionaryValueService.detail(id);
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
    public void edit(@PathVariable("id") Long id, @Validated({UpdateGroup.class, Default.class}) @RequestBody DictionaryValueForm form) {
        BaseCodes.PARAM_VERIFY_ERROR.isTrue(id.equals(form.getId()), "id 不一致");
        BaseCodes.DATA_ERROR.notNull(dictionaryValueService.getById(form.getId()), "指定的数据不存在: " + id);
        this.dictionaryValueService.edit(form);
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
        this.dictionaryValueService.removeByIds(ids);
    }

}
