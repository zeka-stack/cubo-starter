package dev.dong4j.zeka.starter.dict.service;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryValueDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryValueForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.mybatis.service.BaseService;

/**
 * 字典值服务接口
 * <p> 提供字典值相关的业务操作, 包括根据 ID 查询字典值详情, 创建字典值, 编辑字典值等功能
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
public interface DictionaryValueService extends

                                        BaseService<DictionaryValue> {

    /**
     * 根据 ID 获取详细信息
     * <p> 通过指定的主键查询字典值表的详细信息, 并返回对应的 DTO 对象 </p>
     *
     * @param id 主键
     * @return 实体对象, 若未找到则可能返回 null
     * @since 1.0.0
     */
    DictionaryValueDTO detail(Long id);

    /**
     * 新增数据
     * <p> 用于创建新的字典值数据, 将传入的参数实体保存到数据库中 </p>
     *
     * @param form 参数实体, 包含需要创建的数据信息
     * @since 1.0.0
     */
    void create(DictionaryValueForm form);

    /**
     * 更新数据
     *
     * @param form 参数实体
     * @since 1.0.0
     */
    void edit(DictionaryValueForm form);

}

