package dev.dong4j.zeka.starter.dict.service.impl;

import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.starter.dict.dao.DictionaryValueMapper;
import dev.dong4j.zeka.starter.dict.entity.converter.DictionaryValueConverter;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryValueDTO;
import dev.dong4j.zeka.starter.dict.entity.form.DictionaryValueForm;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.service.DictionaryValueService;
import dev.dong4j.zeka.starter.mybatis.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p> 字典值表 服务接口实现类 </p>
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
public class DictionaryValueServiceImpl extends BaseServiceImpl<DictionaryValueMapper, DictionaryValue> implements DictionaryValueService {

    /**
     * 根据 ID 获取详细信息
     *
     * @param id 主键
     * @return 实体对象
     * @since 1.0.0
     */
    @Override
    public DictionaryValueDTO detail(Long id) {
        final DictionaryValue po = this.baseMapper.selectById(id);
        BaseCodes.DATA_ERROR.notNull(po);
        return DictionaryValueConverter.INSTANCE.dto(po);
    }

    /**
     * 新增数据
     *
     * @param form 参数实体
     * @since 1.0.0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictionaryValueForm form) {
        final DictionaryValue po = DictionaryValueConverter.INSTANCE.f2p(form);
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
    public void edit(DictionaryValueForm form) {
        final int updatedCount = this.baseMapper.updateById(DictionaryValueConverter.INSTANCE.f2p(form));
        BaseCodes.OPTION_FAILURE.isTrue(updatedCount == 1);
    }
}


