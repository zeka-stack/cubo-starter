package dev.dong4j.zeka.starter.dict.service;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryTypeDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryTypeForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.mybatis.service.BaseService;

/**
 * <p> 字典类型表 服务接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
public interface DictionaryTypeService extends

    BaseService<DictionaryType> {

    /**
     * 根据 ID 获取详细信息
     *
     * @param id 主键
     * @return 实体对象
     * @since 1.0.0
     */
    DictionaryTypeDTO detail(Long id);

    /**
     * 新增数据
     *
     * @param form 参数实体
     * @since 1.0.0
     */
    void create(DictionaryTypeForm form);

    /**
     * 更新数据
     *
     * @param form 参数实体
     * @since 1.0.0
     */
    void edit(DictionaryTypeForm form);

}

