package dev.dong4j.zeka.starter.mybatis.service.cqrs;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 基础命令 Mapper 接口
 *
 * 该接口定义了 CQRS（命令查询职责分离）模式中的命令操作规范。
 * 主要用于处理数据的写入操作（增删改），与查询操作分离。
 *
 * 主要功能：
 * 1. 提供数据插入操作方法
 * 2. 提供数据删除操作方法（单个、批量、条件删除）
 * 3. 提供数据更新操作方法（按 ID 更新、条件更新）
 * 4. 继承 MyBatis Plus 的 Mapper 接口，获得基础功能
 *
 * CQRS 模式优势：
 * - 读写分离，提高系统性能
 * - 写操作优化，可以使用专门的写入模型
 * - 职责清晰，便于维护和扩展
 * - 支持复杂的事务处理场景
 *
 * 使用场景：
 * - 需要明确区分读写操作的系统
 * - 高并发写入场景
 * - 复杂的业务逻辑处理
 *
 * @param <T> 实体类型参数
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 02:05
 * @since 1.0.0
 */
public interface BaseCommandMapper<T> extends Mapper<T> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     * @return the int
     * @since 1.0.0
     */
    int insert(T entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     * @return the int
     * @since 1.0.0
     */
    int deleteById(Serializable id);

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     * @return the int
     * @since 1.0.0
     */
    int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param wrapper 实体对象封装操作类（可以为 null）
     * @return the int
     * @since 1.0.0
     */
    int delete(@Param(Constants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     * @return the int
     * @since 1.0.0
     */
    int deleteBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     * @return the int
     * @since 1.0.0
     */
    int updateById(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     * @return the int
     * @since 1.0.0
     */
    int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper);

}
