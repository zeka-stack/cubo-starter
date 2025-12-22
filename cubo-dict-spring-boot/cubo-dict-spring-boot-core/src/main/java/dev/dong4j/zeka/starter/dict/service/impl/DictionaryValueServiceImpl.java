package dev.dong4j.zeka.starter.dict.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * 字典值服务实现类
 * <p> 该类继承自 BaseServiceImpl, 并实现了 DictionaryValueService 接口. 提供了字典值的详细信息查询, 创建和编辑等功能.
 * <p> 具体功能包括:
 * <ul>
 * <li> 通过给定的 ID 查询字典值的详细信息 </li>
 * <li> 创建新的字典值记录 </li>
 * <li> 编辑现有的字典值记录 </li>
 * </ul>
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
public class DictionaryValueServiceImpl extends BaseServiceImpl<DictionaryValueMapper, DictionaryValue> implements DictionaryValueService {

    /**
     * 根据 ID 获取详细信息
     * <p> 通过主键查询字典值表数据, 并转换为对应的 DTO 对象返回 </p>
     *
     * @param id 主键
     * @return 字典值对象
     */
    @Override
    public DictionaryValueDTO detail(Long id) {
        final DictionaryValue po = this.baseMapper.selectById(id);
        BaseCodes.DATA_ERROR.notNull(po);
        return DictionaryValueConverter.INSTANCE.p2d(po);
    }

    /**
     * 新增数据
     * <p> 将字典值表的数据插入数据库中. 如果插入失败, 则回滚事务.</p>
     *
     * @param form 包含新增数据的参数实体
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
     * <p> 根据传入的表单参数更新字典值表中的数据. 如果更新失败, 则回滚事务.</p>
     *
     * @param form 包含更新数据的表单实体
     * @since 1.0.0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(DictionaryValueForm form) {
        final int updatedCount = this.baseMapper.updateById(DictionaryValueConverter.INSTANCE.f2p(form));
        BaseCodes.OPTION_FAILURE.isTrue(updatedCount == 1);
    }
}


