package dev.dong4j.zeka.starter.dict.service;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryTypeDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryTypeForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.mybatis.service.BaseService;

/**
 * 字典类型服务接口
 * <p> 提供字典类型相关的业务逻辑处理, 包括字典类型的查询, 创建和编辑等操作
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
public interface DictionaryTypeService extends

                                       BaseService<DictionaryType> {

    /**
     * 根据 ID 获取详细信息
     * <p> 通过给定的主键 ID 查找并返回字典类型详细信息的对象
     *
     * @param id 主键
     * @return 字典类型详细信息的对象
     * @since 1.0.0
     */
    DictionaryTypeDTO detail(Long id);

    /**
     * 新增数据
     * <p> 根据提供的参数实体创建新的字典类型记录 </p>
     *
     * @param form 包含字典类型信息的参数实体
     * @since 1.0.0
     */
    void create(DictionaryTypeForm form);

    /**
     * 更新数据
     * <p> 根据传入的参数实体更新字典类型数据 </p>
     *
     * @param form 参数实体, 包含需要更新的数据信息
     * @since 1.0.0
     */
    void edit(DictionaryTypeForm form);

}

