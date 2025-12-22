package dev.dong4j.zeka.starter.mybatis.service.cqrs.impl;

import java.io.Serializable;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import dev.dong4j.zeka.starter.mybatis.service.cqrs.BaseQueryMapper;
import dev.dong4j.zeka.starter.mybatis.service.cqrs.BaseQueryService;

/**
 * 基础查询服务实现类
 * <p> 提供基础查询相关的业务逻辑实现, 支持泛型 DTO 和查询对象 Q, 用于统一处理数据查询操作.
 * 该类通过继承 BaseQueryService 接口, 实现通用的查询功能, 适用于需要进行数据查询的业务场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
public class BaseQueryServiceImpl<DTO extends BaseDTO<? extends Serializable>,
    Q extends BaseQuery<? extends Serializable>> implements BaseQueryService<DTO, Q> {

    /**
     * 获取基础查询 Mapper
     * <p>
     * 该方法的默认实现返回 null, 子类必须重写此方法以返回具体的 Mapper 实例.
     * 通常在子类中通过依赖注入的方式获取 Mapper 实例.
     * <p>
     * 实现示例:
     * <pre>
     *     private UserQueryMapper userQueryMapper;
     * </pre>
     *
     * @return 基础查询 Mapper 实例, 默认返回 null
     * @since 1.0.0
     */
    @Override
    public BaseQueryMapper<DTO> getBaseMapper() {
        return null;
    }

}
