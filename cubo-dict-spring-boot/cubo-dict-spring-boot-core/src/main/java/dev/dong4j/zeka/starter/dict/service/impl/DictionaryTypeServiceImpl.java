package dev.dong4j.zeka.starter.dict.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.starter.dict.dao.DictionaryTypeMapper;
import dev.dong4j.zeka.starter.dict.entity.converter.DictionaryTypeConverter;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryTypeDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryTypeForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.service.DictionaryTypeService;
import dev.dong4j.zeka.starter.mybatis.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典类型服务实现类
 * <p> 提供字典类型相关的业务逻辑处理, 包括字典类型的查询, 创建和更新等操作. 该类继承自 BaseServiceImpl, 实现了 DictionaryTypeService 接口, 用于操作字典类型数据.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class DictionaryTypeServiceImpl extends BaseServiceImpl<DictionaryTypeMapper, DictionaryType> implements DictionaryTypeService {

    /**
     * 根据 ID 获取详细信息
     * <p> 通过给定的主键 ID 查找字典类型, 并返回对应的字典类型 DTO 对象. 如果字典类型不存在, 则抛出异常.</p>
     *
     * @param id 主键
     * @return 字典类型 DTO 对象
     * @since 1.0.0
     */
    @Override
    public DictionaryTypeDTO detail(Long id) {
        final DictionaryType po = this.baseMapper.selectById(id);
        BaseCodes.DATA_ERROR.notNull(po);
        return DictionaryTypeConverter.INSTANCE.p2d(po);
    }

    /**
     * 新增字典类型数据
     * <p> 将传入的表单数据转换为实体对象, 并插入到数据库中. 若插入失败则抛出异常.</p>
     *
     * @param form 参数实体, 包含需要插入的字典类型数据
     * @since 1.0.0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictionaryTypeForm form) {
        final DictionaryType po = DictionaryTypeConverter.INSTANCE.f2p(form);
        final int savedCount = this.baseMapper.insertIgnore(po);
        BaseCodes.OPTION_FAILURE.isTrue(savedCount == 1);
    }

    /**
     * 更新字典类型数据
     * <p> 根据传入的表单参数更新对应的字典类型记录, 若更新行数不为 1 则抛出异常 </p>
     *
     * @param form 字典类型表单参数, 包含需要更新的数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(DictionaryTypeForm form) {
        final int updatedCount = this.baseMapper.updateById(DictionaryTypeConverter.INSTANCE.f2p(form));
        BaseCodes.OPTION_FAILURE.isTrue(updatedCount == 1);
    }
}


