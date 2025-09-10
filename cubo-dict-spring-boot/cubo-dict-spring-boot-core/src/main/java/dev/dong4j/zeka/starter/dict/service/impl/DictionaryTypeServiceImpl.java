package dev.dong4j.zeka.starter.dict.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p> 字典类型表 服务接口实现类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class DictionaryTypeServiceImpl extends BaseServiceImpl<DictionaryTypeMapper, DictionaryType> implements DictionaryTypeService {

    /**
     * 根据 ID 获取详细信息
     *
     * @param id 主键
     * @return 实体对象
     * @since 1.0.0
     */
    @Override
    public DictionaryTypeDTO detail(Long id) {
        final DictionaryType po = this.baseMapper.selectById(id);
        BaseCodes.DATA_ERROR.notNull(po);
        return DictionaryTypeConverter.INSTANCE.dto(po);
    }

    /**
     * 新增数据
     *
     * @param form 参数实体
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
     * 更新数据
     *
     * @param form 参数实体
     * @since 1.0.0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(DictionaryTypeForm form) {
        final int updatedCount = this.baseMapper.updateById(DictionaryTypeConverter.INSTANCE.f2p(form));
        BaseCodes.OPTION_FAILURE.isTrue(updatedCount == 1);
    }
}


