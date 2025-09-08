package dev.dong4j.zeka.starter.mybatis.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.dong4j.zeka.kernel.common.asserts.Assertions;
import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.cursor.Cursor;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基础服务接口
 *
 * 该接口扩展了 MyBatis Plus 的 IService 接口，提供了额外的业务层操作方法。
 * 主要功能包括：
 *
 * 1. 扩展插入操作：
 *    - saveIgnore：插入时忽略重复数据
 *    - saveReplace：替换插入操作
 *    - 支持批量操作，提高性能
 *
 * 2. 通用查询操作：
 *    - 支持分页查询（带/不带总数统计）
 *    - 支持列表查询
 *    - 支持流式查询，防止大数据量 OOM
 *    - 支持计数查询
 *
 * 3. 泛型支持：
 *    - 支持 DTO 和 Query 对象的泛型操作
 *    - 提供类型安全的查询接口
 *
 * 4. 事务支持：
 *    - 批量操作自动使用事务
 *    - 支持异常回滚
 *
 * 该接口为业务层提供了统一的数据访问规范，简化了常见的 CRUD 操作。
 *
 * @param <PO> 持久化对象类型参数
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:21
 * @since 1.0.0
 */
public interface BaseService<PO> extends IService<PO> {
    /**
     * 插入忽略操作
     *
     * 该方法实现插入忽略功能，当插入的数据与现有数据冲突时，会忽略当前插入操作。
     * 底层使用 MySQL 的 INSERT IGNORE 语法，适用于以下场景：
     * - 批量插入时避免重复数据导致的异常
     * - 幂等性操作，重复执行不会产生副作用
     * - 数据同步场景，避免重复数据的插入
     *
     * @param entity 要插入的实体对象
     * @return boolean 是否成功（true：插入成功，false：被忽略或失败）
     * @since 1.0.0
     */
    boolean saveIgnore(PO entity);

    /**
     * 替换插入操作
     *
     * 该方法实现替换插入功能，具有以下行为：
     * - 如果数据不存在，则执行插入操作
     * - 如果数据已存在（基于主键或唯一索引），则先删除旧数据，再插入新数据
     *
     * 底层使用 MySQL 的 REPLACE INTO 语法，适用于：
     * - 数据更新插入（upsert）操作
     * - 配置数据的覆盖更新
     * - 缓存数据的刷新操作
     *
     * 注意：该操作要求表必须有主键或唯一索引约束
     *
     * @param entity 要插入或替换的实体对象
     * @return boolean 是否成功
     * @since 1.0.0
     */
    boolean saveReplace(PO entity);

    /**
     * 插入 (批量) ,插入如果中已经存在相同的记录,则忽略当前新数据
     *
     * @param entityList 实体对象集合
     * @return 是否成功 boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean saveIgnoreBatch(Collection<PO> entityList) {
        Assertions.notEmpty(entityList);
        return this.saveIgnoreBatch(entityList, 1000);
    }

    /**
     * 批量插入忽略操作
     *
     * 该方法实现批量插入忽略功能，当插入的数据与现有数据冲突时，会忽略冲突的数据。
     * 支持自定义批次大小，提高大数据量插入的性能。
     *
     * 特点：
     * - 支持自定义批次大小，避免单次操作数据量过大
     * - 使用事务保证数据一致性
     * - 冲突数据会被忽略，不会抛出异常
     *
     * @param entityList 实体对象集合
     * @param batchSize 批次大小，建议 500-1000
     * @return boolean 是否成功
     * @since 1.0.0
     */
    boolean saveIgnoreBatch(Collection<PO> entityList, int batchSize);

    /**
     * 批量替换插入操作（默认批次大小）
     *
     * 该方法实现批量替换插入功能，使用默认批次大小 1000。
     * 对于每条数据：如果不存在则插入，如果存在则替换。
     *
     * 注意：
     * - 使用事务保证操作的原子性
     * - 要求表必须有主键或唯一索引约束
     * - 默认批次大小为 1000，适合大多数场景
     *
     * @param entityList 实体对象集合
     * @return boolean 是否成功
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean saveReplaceBatch(Collection<PO> entityList) {
        Assertions.notEmpty(entityList);
        return this.saveReplaceBatch(entityList, 1000);
    }

    /**
     * 批量替换插入操作（自定义批次大小）
     *
     * 该方法实现批量替换插入功能，支持自定义批次大小。
     * 对于每条数据：如果不存在则插入，如果存在则先删除再插入。
     *
     * 适用场景：
     * - 配置数据的批量更新
     * - 缓存数据的批量刷新
     * - 数据同步场景的批量处理
     *
     * 注意：该操作要求表必须有主键或唯一索引约束
     *
     * @param entityList 实体对象集合
     * @param batchSize 批次大小，建议 500-1000
     * @return boolean 是否成功
     * @since 1.0.0
     */
    boolean saveReplaceBatch(Collection<PO> entityList, int batchSize);

    /**
     * 分页查询接口（已废弃）
     *
     * 该方法提供分页查询功能，但已被标记为废弃。
     * 建议使用不需要传入 IPage 参数的 page(Q query) 方法。
     *
     * @param <D> DTO 类型，继承自 BaseDTO
     * @param <Q> 查询条件类型，继承自 BaseQuery
     * @param page 分页参数对象
     * @param query 业务查询参数对象
     * @return IPage<D> 分页结果对象
     * @deprecated 建议使用 page(Q query) 方法
     * @since 1.0.0
     */
    @Deprecated
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<D> page(IPage<D> page, @NotNull Q query);

    /**
     * 分页查询接口
     *
     * 该方法提供完整的分页查询功能，包括数据查询和总数统计。
     * 分页参数从 Query 对象中获取，简化了方法调用。
     *
     * 功能特点：
     * - 自动执行 count 查询统计总记录数
     * - 支持多种分页参数配置
     * - 返回完整的分页信息（数据 + 总数 + 分页参数）
     * - 支持泛型 DTO 和 Query 对象
     *
     * @param <D> DTO 类型，继承自 BaseDTO
     * @param <Q> 查询条件类型，继承自 BaseQuery
     * @param query 业务查询参数对象，包含分页信息和查询条件
     * @return IPage<D> 分页结果对象
     * @since 1.0.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<D> page(@NotNull Q query);

    /**
     * 列表查询接口
     *
     * 该方法提供列表查询功能，返回符合条件的数据列表。
     * 不执行 count 查询，性能优于分页查询，适用于不需要总数的场景。
     *
     * 适用场景：
     * - 下拉框数据查询
     * - 导出数据查询
     * - 不需要分页的列表展示
     *
     * @param <D> DTO 类型，继承自 BaseDTO
     * @param <Q> 查询条件类型，继承自 BaseQuery
     * @param query 业务查询参数对象
     * @return List<D> 查询结果列表
     * @since 1.0.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> List<D> list(@NotNull Q query);

    /**
     * 计数查询接口
     *
     * 该方法提供记录计数功能，根据查询条件统计符合条件的记录数量。
     *
     * 适用场景：
     * - 数据统计分析
     * - 条件查询的记录数统计
     * - 业务规则验证（如检查数据是否存在）
     * - 分页查询前的总数预估
     *
     * @param <Q> 查询条件类型，继承自 BaseQuery
     * @param query 查询参数对象，包含统计条件
     * @return int 符合条件的记录数量
     * @since 1.0.0
     */
    <Q extends BaseQuery<? extends Serializable>> int count(@NotNull Q query);

    /**
     * 流式查询接口
     *
     * 该方法提供流式查询功能，用于处理大数据量查询场景。
     * 使用数据库游标逐行读取数据，避免一次性加载大量数据到内存中。
     *
     * 工作原理：
     * - 使用数据库游标（Cursor）逐行读取数据
     * - 数据按需加载，不会一次性加载到内存
     * - 适合大数据量的批处理场景
     *
     * 使用注意事项：
     * - 需要手动关闭 Cursor 资源，建议使用 try-with-resources
     * - 不适合在事务中长时间持有
     * - 适用于大数据量的数据导出和批量处理
     *
     * @param <D> DTO 类型，继承自 BaseDTO
     * @param <Q> 查询条件类型，继承自 BaseQuery
     * @param query 查询参数对象
     * @return Cursor<D> 数据库游标，需要手动关闭
     * @since 1.0.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> Cursor<D> stream(@NotNull Q query);

}
