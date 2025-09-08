package dev.dong4j.zeka.starter.mybatis.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import dev.dong4j.zeka.kernel.common.asserts.Assertions;
import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import dev.dong4j.zeka.starter.mybatis.base.BaseDao;
import dev.dong4j.zeka.starter.mybatis.injector.MybatisSqlMethod;
import dev.dong4j.zeka.starter.mybatis.service.BaseService;
import dev.dong4j.zeka.starter.mybatis.support.Condition;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基础服务实现类
 *
 * 该类提供了 BaseService 接口的默认实现，继承自 MyBatis Plus 的 ServiceImpl。
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
 * 3. 批量操作优化：
 *    - 使用批量 SQL 会话提高性能
 *    - 支持自定义批次大小
 *    - 自动事务管理
 *
 * 4. 查询条件处理：
 *    - 自动处理时间范围查询
 *    - 支持动态分页参数
 *    - 参数校验和异常处理
 *
 * @param <DAO> 数据访问对象类型，继承自 BaseDao
 * @param <PO> 持久化对象类型
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.10 17:48
 * @since 1.0.0
 */
public class BaseServiceImpl<DAO extends BaseDao<PO>, PO> extends ServiceImpl<DAO, PO> implements BaseService<PO> {

    /**
     * Save ignore boolean
     *
     * @param entity entity
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean saveIgnore(PO entity) {
        Assertions.notNull(entity);
        return SqlHelper.retBool(this.baseMapper.insertIgnore(entity));
    }

    /**
     * Save replace boolean
     *
     * @param entity entity
     * @return the boolean
     * @since 1.0.0
     */
    @Override
    public boolean saveReplace(PO entity) {
        Assertions.notNull(entity);
        return SqlHelper.retBool(this.baseMapper.replace(entity));
    }

    /**
     * Save ignore batch boolean
     *
     * @param entityList entity list
     * @param batchSize  batch size
     * @return the boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveIgnoreBatch(Collection<PO> entityList, int batchSize) {
        Assertions.notEmpty(entityList);
        return this.saveBatch(entityList, batchSize, MybatisSqlMethod.INSERT_IGNORE_ONE);
    }

    /**
     * Save replace batch boolean
     *
     * @param entityList entity list
     * @param batchSize  batch size
     * @return the boolean
     * @since 1.0.0
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveReplaceBatch(Collection<PO> entityList, int batchSize) {
        Assertions.notEmpty(entityList);
        return this.saveBatch(entityList, batchSize, MybatisSqlMethod.REPLACE_ONE);
    }

    /**
     * Save batch boolean
     *
     * @param entityList entity list
     * @param batchSize  batch size
     * @param sqlMethod  sql method
     * @return the boolean
     * @since 1.0.0
     */
    private boolean saveBatch(@NotNull Collection<PO> entityList, int batchSize, MybatisSqlMethod sqlMethod) {
        Assertions.notEmpty(entityList);
        String sqlStatement = this.mybatisSqlStatement(sqlMethod);
        try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(getEntityClass())) {
            int i = 0;
            for (PO anEntityList : entityList) {
                batchSqlSession.insert(sqlStatement, anEntityList);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    /**
     * Mybatis sql statement string.
     *
     * @param sqlMethod ignore
     * @return sql string
     * @since 1.0.0
     */
    protected String mybatisSqlStatement(@NotNull MybatisSqlMethod sqlMethod) {
        return SqlHelper.table(getEntityClass()).getSqlStatement(sqlMethod.getMethod());
    }

    /**
     * 分页查询接口
     *
     * @param <DTO> parameter
     * @param <Q>   {@link BaseQuery} 子类
     * @param page  分页参数
     * @param query 业务查询参数
     * @return the {@link IPage} 的子类 {@link Page}
     * @since 1.0.0
     * @deprecated 使用 {@link BaseServiceImpl#page(BaseQuery)}
     */
    @Override
    @Deprecated
    public <DTO extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<DTO>
    page(IPage<DTO> page, @NotNull Q query) {
        Assertions.notNull(query);
        return this.baseMapper.page(Condition.getPage(query), query);
    }

    /**
     * 分页查询接口
     *
     * @param <DTO> parameter
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 业务查询参数
     * @return the {@link IPage} 的子类 {@link Page}
     * @since 1.0.0
     */
    @Override
    public <DTO extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> IPage<DTO> page(@NotNull Q query) {
        Assertions.notNull(query);
        return this.baseMapper.page(Condition.getPage(query), query);
    }

    /**
     * 根据条件查询所有记录
     *
     * @param <DTO> parameter
     * @param <Q>   {@link BaseQuery} 子类
     * @param query 业务查询参数
     * @return the list
     * @since 1.0.0
     */
    @Override
    public <DTO extends BaseDTO<? extends Serializable>, Q extends BaseQuery<? extends Serializable>> List<DTO> list(@NotNull Q query) {
        Assertions.notNull(query);
        Condition.checkTime(query);
        return this.baseMapper.page(query);
    }

    /**
     * Count
     *
     * @param query query
     * @return the int
     * @since 1.0.0
     */
    @Override
    public <Q extends BaseQuery<? extends Serializable>> int count(@NotNull Q query) {
        Assertions.notNull(query);
        Condition.checkTime(query);
        return this.baseMapper.count(query);
    }

    /**
     * 流式查询
     *
     * @param <DTO> parameter
     * @param <Q>   parameter
     * @param query query
     * @return the cursor
     * @since 1.0.0
     */
    @Override
    public <DTO extends BaseDTO<? extends Serializable>,
        Q extends BaseQuery<? extends Serializable>> Cursor<DTO> stream(@NotNull Q query) {
        Assertions.notNull(query);
        Condition.checkTime(query);
        return this.baseMapper.stream(query);
    }

}
