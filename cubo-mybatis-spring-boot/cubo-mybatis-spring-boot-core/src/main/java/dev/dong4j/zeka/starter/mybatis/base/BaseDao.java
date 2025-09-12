package dev.dong4j.zeka.starter.mybatis.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import java.io.Serializable;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

/**
 * 基础数据访问接口
 * <p>
 * 该接口扩展了 MyBatis Plus 的 BaseMapper，提供了额外的数据库操作方法。
 * 主要功能包括：
 * <p>
 * 1. 扩展插入操作：
 * - insertIgnore：插入时忽略重复数据
 * - replace：替换插入操作
 * <p>
 * 2. 通用查询操作：
 * - 支持分页查询（带/不带 count 查询）
 * - 支持流式查询，防止大数据量 OOM
 * - 支持通用计数查询
 * <p>
 * 3. 泛型支持：
 * - 支持 DTO 和 Query 对象的泛型操作
 * - 提供类型安全的查询接口
 * <p>
 * 注意：流式查询需要在对应的 Mapper.xml 中定义相应的 SQL 语句。
 *
 * @param <T> 实体类型参数
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:42
 * @since 1.0.0
 */
public interface BaseDao<T> extends BaseMapper<T> {

    /**
     * 插入忽略操作
     * <p>
     * 该方法实现 MySQL 的 INSERT IGNORE 语法，当插入的数据与现有数据冲突时，
     * 会忽略当前插入操作而不是抛出异常。适用于以下场景：
     * - 批量插入时避免重复数据导致的异常
     * - 幂等性操作，重复执行不会产生副作用
     * - 数据同步场景，避免重复数据的插入
     * <p>
     * 注意：该操作依赖于表的主键或唯一索引约束
     *
     * @param entity 要插入的实体对象
     * @return int 实际插入的记录数（0 表示被忽略，1 表示插入成功）
     * @since 1.0.0
     */
    int insertIgnore(T entity);

    /**
     * 替换插入操作
     * <p>
     * 该方法实现 MySQL 的 REPLACE INTO 语法，具有以下行为：
     * - 如果数据不存在，则执行插入操作
     * - 如果数据已存在（基于主键或唯一索引），则先删除旧数据，再插入新数据
     * <p>
     * 适用场景：
     * - 数据更新插入（upsert）操作
     * - 配置数据的覆盖更新
     * - 缓存数据的刷新操作
     * <p>
     * 注意：该操作要求表必须有主键或唯一索引约束
     *
     * @param entity 要插入或替换的实体对象
     * @return int 影响的记录数（1 表示插入，2 表示替换）
     * @since 1.0.0
     */
    int replace(T entity);

    /**
     * 通用列表查询接口
     * <p>
     * 该方法提供通用的列表查询功能，内部使用分页查询的 SQL 语句，
     * 但不执行 count 查询，适用于只需要数据列表而不需要总数的场景。
     * <p>
     * 特点：
     * - 使用分页查询的 SQL 模板，但不统计总数
     * - 性能优于带 count 的分页查询
     * - 支持泛型 DTO 和 Query 对象
     * - 查询条件通过 Query 对象传递
     *
     * @param <D>   DTO 类型，继承自 BaseDTO
     * @param <Q>   查询条件类型，继承自 BaseQuery
     * @param query 查询参数对象，包含查询条件和分页信息
     * @return List<D> 查询结果列表
     * @since 1.0.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<?>> List<D> page(@Param("query") Q query);

    /**
     * 通用分页查询接口
     * <p>
     * 该方法提供完整的分页查询功能，包括数据查询和总数统计。
     * 当传入 IPage 参数时，会自动执行 count 查询来获取总记录数。
     * <p>
     * 功能特点：
     * - 自动执行 count 查询统计总记录数
     * - 支持多种分页参数配置
     * - 返回完整的分页信息（数据 + 总数 + 分页参数）
     * - 支持泛型 DTO 和 Query 对象
     * <p>
     * 适用场景：
     * - 需要显示总页数的分页查询
     * - 前端分页组件需要总记录数的场景
     * - 完整的分页数据展示
     *
     * @param <D>   DTO 类型，继承自 BaseDTO
     * @param <Q>   查询条件类型，继承自 BaseQuery
     * @param page  分页参数对象，包含页码、页大小等信息
     * @param query 查询参数对象，包含查询条件
     * @return IPage<D> 分页结果对象，包含数据列表和分页信息
     * @since 1.0.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<?>> IPage<D> page(@Param("page") IPage<D> page,
                                                                                      @Param("query") Q query);

    /**
     * 流式查询接口
     * <p>
     * 该方法提供流式查询功能，用于处理大数据量查询场景，避免一次性加载
     * 大量数据到内存中导致 OOM（内存溢出）问题。
     * <p>
     * 工作原理：
     * - 使用数据库游标（Cursor）逐行读取数据
     * - 数据按需加载，不会一次性加载到内存
     * - 适合大数据量的批处理场景
     * <p>
     * 使用要求：
     * - 必须在对应的 Mapper.xml 中定义 stream 方法的 SQL 语句
     * - 需要手动关闭 Cursor 资源，建议使用 try-with-resources
     * - 不适合在事务中长时间持有
     * <p>
     * 适用场景：
     * - 大数据量的数据导出
     * - 批量数据处理
     * - 数据迁移操作
     *
     * @param <D>   DTO 类型，继承自 BaseDTO
     * @param <Q>   查询条件类型，继承自 BaseQuery
     * @param query 查询参数对象
     * @return Cursor<D> 数据库游标，需要手动关闭
     * @since 1.0.0
     */
    <D extends BaseDTO<? extends Serializable>, Q extends BaseQuery<?>> Cursor<D> stream(@Param("query") Q query);

    /**
     * 通用计数查询接口
     * <p>
     * 该方法提供通用的记录计数功能，根据查询条件统计符合条件的记录数量。
     * <p>
     * 功能特点：
     * - 支持复杂查询条件的计数
     * - 性能优化的 count 查询
     * - 支持泛型 Query 对象
     * - 返回精确的记录数量
     * <p>
     * 适用场景：
     * - 数据统计分析
     * - 条件查询的记录数统计
     * - 业务规则验证（如检查数据是否存在）
     * - 分页查询前的总数预估
     *
     * @param <Q>   查询条件类型，继承自 BaseQuery
     * @param query 查询参数对象，包含统计条件
     * @return int 符合条件的记录数量
     * @since 1.0.0
     */
    <Q extends BaseQuery<?>> int count(Q query);
}
