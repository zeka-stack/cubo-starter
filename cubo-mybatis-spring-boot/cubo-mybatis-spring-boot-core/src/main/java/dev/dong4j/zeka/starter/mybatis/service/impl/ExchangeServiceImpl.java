package dev.dong4j.zeka.starter.mybatis.service.impl;

import dev.dong4j.zeka.kernel.common.asserts.Assertions;
import dev.dong4j.zeka.kernel.common.base.AbstractBaseEntity;
import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import dev.dong4j.zeka.kernel.common.mapstruct.ServiceConverter;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.starter.mybatis.base.BaseDao;
import dev.dong4j.zeka.starter.mybatis.base.BasePO;
import dev.dong4j.zeka.starter.mybatis.service.IExchangeService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 实体转换服务实现类
 *
 * 该类提供了 PO（持久化对象）和 DTO（数据传输对象）之间的转换服务实现。
 * 为了避免与 MyBatis Plus 的 IService 方法名冲突，使用了相近的单词代替：
 *
 * 方法名映射：
 * - save -> create（创建）
 * - updateById -> update（更新）
 * - getById -> find（查找）
 * - removeById -> delete（删除）
 *
 * 主要功能：
 * 1. 提供 DTO 到 PO 的自动转换
 * 2. 支持各种插入操作（普通、忽略、替换）
 * 3. 支持批量操作，提高性能
 * 4. 提供完整的 CRUD 操作
 * 5. 自动处理 ID 回写
 *
 * 设计特点：
 * - 使用 ServiceConverter 进行对象转换
 * - 支持泛型，类型安全
 * - 自动参数校验和异常处理
 * - 支持事务管理
 *
 * @param <DAO> 数据访问对象类型，继承自 BaseDao
 * @param <PO> 持久化对象类型，继承自 BasePO
 * @param <DTO> 数据传输对象类型，继承自 AbstractBaseEntity
 * @param <C> 服务转换器类型，继承自 ServiceConverter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:08
 * @since 1.0.0
 */
@Slf4j
public class ExchangeServiceImpl<DAO extends BaseDao<PO>, PO extends BasePO<?, PO>, DTO extends AbstractBaseEntity<?>,
    C extends ServiceConverter<DTO, PO>>
    extends BaseServiceImpl<DAO, PO> implements IExchangeService<PO, DTO> {

    /** Service converter */
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected C serviceConverter;

    /**
     * 通过 dto 保存数据, 操作成功后会将 id 写入到 dto.
     *
     * @param <I> parameter
     * @param dto dto
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> I create(@NotNull DTO dto) {
        Assertions.notNull(dto);
        PO po = this.serviceConverter.po(dto);
        Assertions.isTrue(super.save(po));
        dto.setId(this.getId(po));
        return this.getId(po);
    }

    /**
     * 插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param <I> parameter
     * @param dto dto
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> I createIgnore(@NotNull DTO dto) {
        Assertions.notNull(dto);
        PO po = this.serviceConverter.po(dto);
        Assertions.isTrue(super.saveIgnore(po));
        dto.setId(this.getId(po));
        return this.getId(po);
    }

    /**
     * 表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param <I> parameter
     * @param dto dto
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> I createReplace(@NotNull DTO dto) {
        Assertions.notNull(dto);
        PO po = this.serviceConverter.po(dto);
        Assertions.isTrue(super.saveReplace(po));
        dto.setId(this.getId(po));
        return this.getId(po);
    }

    /**
     * Create or update
     *
     * @param <I> parameter
     * @param dto dto
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> I createOrUpdate(@NotNull DTO dto) {
        Assertions.notNull(dto);
        PO po = this.serviceConverter.po(dto);
        Assertions.isTrue(super.saveOrUpdate(po));
        dto.setId(this.getId(po));
        return this.getId(po);
    }

    /**
     * Create batch
     *
     * @param dtos dtos
     * @since 1.0.0
     */
    @Override
    public void createBatch(Collection<DTO> dtos) {
        this.createBatch(dtos, 1000);
    }

    /**
     * Create batch
     *
     * @param dtos      dtos
     * @param batchSize batch size
     * @since 1.0.0
     */
    @Override
    public void createBatch(Collection<DTO> dtos, int batchSize) {
        Assertions.notEmpty(dtos);
        Assertions.isTrue(super.saveBatch(this.serviceConverter.po(dtos), batchSize));
    }

    /**
     * Create ignore batch
     *
     * @param dtos dtos
     * @since 1.0.0
     */
    @Override
    public void createIgnoreBatch(Collection<DTO> dtos) {
        this.createIgnoreBatch(dtos, dtos.size());
    }

    /**
     * 插入 (批量) ,插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param dtos      实体对象集合
     * @param batchSize 批次大小
     * @since 1.0.0
     */
    @Override
    public void createIgnoreBatch(Collection<DTO> dtos, int batchSize) {
        Assertions.notEmpty(dtos);
        Assertions.isTrue(super.saveIgnoreBatch(this.serviceConverter.po(dtos), batchSize));
    }

    /**
     * Create replace batch
     *
     * @param dtos dtos
     * @since 1.0.0
     */
    @Override
    public void createReplaceBatch(Collection<DTO> dtos) {
        this.createReplaceBatch(dtos, dtos.size());
    }

    /**
     * 插入 (批量) ,表示插入替换数据,需求表中有PrimaryKey,或者unique索引,如果数据库已经存在数据,则用新数据替换,如果没有数据效果则和insert into一样;
     *
     * @param dtos      实体对象集合
     * @param batchSize 批次大小
     * @since 1.0.0
     */
    @Override
    public void createReplaceBatch(Collection<DTO> dtos, int batchSize) {
        Assertions.notEmpty(dtos);
        Assertions.isTrue(super.saveReplaceBatch(this.serviceConverter.po(dtos), batchSize));
    }

    /**
     * 通过 DTO 更新数据.
     *
     * @param dto dto
     * @since 1.0.0
     */
    @Override
    public void update(@NotNull DTO dto) {
        Assertions.notNull(dto);
        Assertions.isTrue(super.updateById(this.serviceConverter.po(dto)));
    }

    /**
     * Update batch
     *
     * @param dtos dtos
     * @since 1.0.0
     */
    @Override
    public void updateBatch(Collection<DTO> dtos) {
        this.updateBatch(dtos, 1000);
    }

    /**
     * Update batch
     * fixme-dong4j : (2025.06.30 20:31) [修复事务失效问题]
     *
     * @param dtos      dtos
     * @param batchSize batch size
     * @since 1.0.0
     */
    @Override
    public void updateBatch(Collection<DTO> dtos, int batchSize) {
        Assertions.notEmpty(dtos);
        Assertions.isTrue(super.updateBatchById(this.serviceConverter.po(dtos), batchSize));
    }

    /**
     * Create or update batch
     *
     * @param dtos dtos
     * @since 1.0.0
     */
    @Override
    public void createOrUpdateBatch(Collection<DTO> dtos) {
        this.createOrUpdateBatch(dtos, 1000);
    }

    /**
     * Create or update batch
     *
     * @param dtos      dtos
     * @param batchSize batch size
     * @since 1.0.0
     */
    @Override
    public void createOrUpdateBatch(Collection<DTO> dtos, int batchSize) {
        Assertions.notEmpty(dtos);
        Assertions.isTrue(super.saveOrUpdateBatch(this.serviceConverter.po(dtos), batchSize));
    }

    /**
     * 通过 id 删除 DTO.
     *
     * @param <I> parameter
     * @param id  id
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> void delete(@NotNull I id) {
        Assertions.notNull(id);
        Assertions.isTrue(super.removeById(id));
    }

    /**
     * Delete
     *
     * @param <I> parameter
     * @param ids ids
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> void delete(Collection<I> ids) {
        Assertions.notEmpty(ids);
        Assertions.isTrue(super.removeByIds(ids));
    }

    /**
     * Remove
     *
     * @param columnMap column map
     * @since 1.0.0
     */
    @Override
    public void delete(@NotNull Map<String, Object> columnMap) {
        Assertions.notEmpty(columnMap);
        Assertions.isTrue(super.removeByMap(columnMap));
    }

    /**
     * Count
     *
     * @return the int
     * @since 1.0.0
     */
    @Override
    public long counts() {
        return super.count();
    }

    /**
     * 查询总记录数
     *
     * @param <Q>   parameter
     * @param query query
     * @return the int
     * @since 1.0.0
     */
    @Override
    public <Q extends BaseQuery<? extends Serializable>> int counts(@NotNull Q query) {
        return super.count(query);
    }

    /**
     * 通过 id 查询 DTO.
     *
     * @param <I> parameter
     * @param id  id
     * @return the d
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> DTO find(@NotNull I id) {
        Assertions.notNull(id);
        return this.serviceConverter.dto(super.getById(id));
    }

    /**
     * Find
     *
     * @param <I> parameter
     * @param ids ids
     * @return the list
     * @since 1.0.0
     */
    @Override
    public <I extends Serializable> List<DTO> find(Collection<I> ids) {
        Assertions.notEmpty(ids);
        List<PO> pos = super.listByIds(ids);
        return this.serviceConverter.dto(pos);
    }

    /**
     * 查询所有的 DTO 数据
     *
     * @return the list
     * @since 1.0.0
     */
    @Override
    public List<DTO> find() {
        return this.serviceConverter.dto(super.list());
    }

    /**
     * Find
     *
     * @param <Q>   parameter
     * @param query query
     * @return the dto
     * @since 1.0.0
     */
    @Override
    public <Q extends BaseQuery<? extends Serializable>> DTO find(@NotNull Q query) {
        Assertions.notNull(query);
        query.setLimit(2);
        List<BaseDTO<? extends Serializable>> list = super.list(query);
        Assertions.isTrue(list.size() <= 1, "数据不唯一");
        // noinspection unchecked
        return CollectionUtils.isNotEmpty(list) ? (DTO) list.get(0) : null;
    }

    /**
     * 获取 PO 的 id 值
     *
     * @param <I> id
     * @param po  数据库操作实体
     * @return 返回 id 值
     * @since 1.0.0
     */
    private <I extends Serializable> I getId(@NotNull PO po) {
        // noinspection unchecked
        return (I) po.getId();
    }
}
