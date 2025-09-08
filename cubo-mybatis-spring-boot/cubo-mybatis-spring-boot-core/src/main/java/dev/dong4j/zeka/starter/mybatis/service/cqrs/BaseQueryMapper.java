package dev.dong4j.zeka.starter.mybatis.service.cqrs;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 基础查询 Mapper 接口
 *
 * 该接口定义了 CQRS（命令查询职责分离）模式中的查询操作规范。
 * 主要用于处理数据的读取操作，与写入操作分离。
 *
 * 主要功能：
 * 1. 提供单条数据查询方法（按 ID、条件查询）
 * 2. 提供批量数据查询方法（批量 ID、条件查询）
 * 3. 提供分页查询方法
 * 4. 提供计数查询方法
 * 5. 提供多种数据格式的查询结果（实体、Map、Object）
 *
 * CQRS 模式优势：
 * - 读写分离，提高查询性能
 * - 查询优化，可以使用专门的查询模型
 * - 支持复杂的查询场景和数据格式
 * - 职责清晰，便于维护和扩展
 *
 * 使用场景：
 * - 需要明确区分读写操作的系统
 * - 高并发查询场景
 * - 复杂的查询需求
 * - 多种数据格式的查询结果
 *
 * @param <T> 实体类型参数
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 02:07
 * @since 1.0.0
 */
public interface BaseQueryMapper<T> extends Mapper<T> {

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     * @return the t
     * @since 1.0.0
     */
    T selectById(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     * @return the list
     * @since 1.0.0
     */
    List<T> selectBatchIds(@Param(Constants.COLLECTION) Collection<? extends Serializable> idList);

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     * @return the list
     * @since 1.0.0
     */
    List<T> selectByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap);

    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return the t
     * @since 1.0.0
     */
    T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return the integer
     * @since 1.0.0
     */
    Integer selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return the list
     * @since 1.0.0
     */
    List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return the list
     * @since 1.0.0
     */
    List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return the list
     * @since 1.0.0
     */
    List<Object> selectObjs(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param <E>          parameter
     * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @return the e
     * @since 1.0.0
     */
    <E extends IPage<T>> E selectPage(E page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param <E>          parameter
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @return the e
     * @since 1.0.0
     */
    <E extends IPage<Map<String, Object>>> E selectMapsPage(E page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
}
