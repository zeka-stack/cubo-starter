package dev.dong4j.zeka.starter.mybatis.service.cqrs.impl;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseQuery;
import dev.dong4j.zeka.starter.mybatis.service.cqrs.BaseQueryMapper;
import dev.dong4j.zeka.starter.mybatis.service.cqrs.BaseQueryService;
import java.io.Serializable;

/**
 * <p>Description: </p>
 *
 * @param <DTO> parameter
 * @param <Q>   parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 02:00
 * @since 1.7.3
 */
public class BaseQueryServiceImpl<DTO extends BaseDTO<? extends Serializable>,
    Q extends BaseQuery<? extends Serializable>> implements BaseQueryService<DTO, Q> {

    /**
     * Gets base mapper *
     *
     * @return the base mapper
     * @since 1.7.3
     */
    @Override
    public BaseQueryMapper<DTO> getBaseMapper() {
        return null;
    }

}
