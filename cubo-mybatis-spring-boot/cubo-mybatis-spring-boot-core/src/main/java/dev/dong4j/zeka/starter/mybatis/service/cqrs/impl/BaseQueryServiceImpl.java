package dev.dong4j.zeka.starter.mybatis.service.cqrs.impl;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import dev.dong4j.zeka.starter.mybatis.service.cqrs.BaseQueryMapper;
import dev.dong4j.zeka.starter.mybatis.service.cqrs.BaseQueryService;
import java.io.Serializable;

/**
 * 基础查询服务实现类
 *
 * 该类提供了 BaseQueryService 接口的默认实现，用于 CQRS 模式中的查询服务。
 * 主要功能包括：
 *
 * 1. 实现基础查询服务接口的所有方法
 * 2. 提供查询操作的默认实现
 * 3. 支持子类继承和扩展
 * 4. 与 BaseQueryMapper 配合实现数据查询
 *
 * 使用说明：
 * - 子类需要重写 getBaseMapper() 方法，返回具体的 Mapper 实例
 * - 可以根据业务需求重写其他查询方法
 * - 建议在子类中注入具体的 Mapper 实现
 *
 * @param <DTO> 数据传输对象类型，继承自 BaseDTO
 * @param <Q> 查询条件对象类型，继承自 BaseQuery
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 02:00
 * @since 1.0.0
 */
public class BaseQueryServiceImpl<DTO extends BaseDTO<? extends Serializable>,
    Q extends BaseQuery<? extends Serializable>> implements BaseQueryService<DTO, Q> {

    /**
     * 获取基础查询 Mapper
     *
     * 该方法的默认实现返回 null，子类必须重写此方法以返回具体的 Mapper 实例。
     * 通常在子类中通过依赖注入的方式获取 Mapper 实例。
     *
     * 实现示例：
     * ```java
     * @Autowired
     * private UserQueryMapper userQueryMapper;
     *
     * @Override
     * public BaseQueryMapper<UserDTO> getBaseMapper() {
     *     return userQueryMapper;
     * }
     * ```
     *
     * @return BaseQueryMapper<DTO> 基础查询 Mapper 实例，默认返回 null
     * @since 1.0.0
     */
    @Override
    public BaseQueryMapper<DTO> getBaseMapper() {
        return null;
    }

}
