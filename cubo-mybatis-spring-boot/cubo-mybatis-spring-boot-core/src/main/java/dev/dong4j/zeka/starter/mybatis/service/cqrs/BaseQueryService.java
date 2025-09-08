package dev.dong4j.zeka.starter.mybatis.service.cqrs;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import java.io.Serializable;

/**
 * 基础查询服务接口
 *
 * 该接口定义了 CQRS（命令查询职责分离）模式中的查询服务规范。
 * 主要用于处理数据查询操作，与命令服务（BaseCommandService）分离，
 * 实现读写操作的职责分离。
 *
 * 主要功能：
 * 1. 提供基础的数据查询方法
 * 2. 支持 DTO 和 Query 对象的泛型操作
 * 3. 与 BaseQueryMapper 配合实现查询功能
 * 4. 支持按 ID 查询等常用操作
 *
 * CQRS 模式优势：
 * - 读写分离，提高系统性能
 * - 查询优化，可以使用专门的查询模型
 * - 职责清晰，便于维护和扩展
 * - 支持复杂的查询场景
 *
 * @param <DTO> 数据传输对象类型，继承自 BaseDTO
 * @param <Q> 查询条件对象类型，继承自 BaseQuery
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 01:53
 * @since 1.0.0
 */
public interface BaseQueryService<DTO extends BaseDTO<? extends Serializable>,
    Q extends BaseQuery<? extends Serializable>> {

    /**
     * 获取基础查询 Mapper
     *
     * 该方法用于获取与当前查询服务关联的 BaseQueryMapper 实例。
     * 通过该 Mapper 可以执行具体的数据库查询操作。
     *
     * @return BaseQueryMapper<DTO> 基础查询 Mapper 实例
     * @since 1.0.0
     */
    BaseQueryMapper<DTO> getBaseMapper();

    /**
     * 根据 ID 查询数据
     *
     * 该方法提供了根据主键 ID 查询单条数据的功能。
     * 返回的是 DTO 对象，适用于查询场景。
     *
     * 注意：该方法是默认实现，子类可以根据需要重写以添加额外的业务逻辑。
     *
     * @param id 主键 ID
     * @return DTO 查询结果的 DTO 对象，如果不存在则返回 null
     * @since 1.0.0
     */
    default DTO selectById(Serializable id) {
        return this.getBaseMapper().selectById(id);
    }
}
