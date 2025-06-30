package dev.dong4j.zeka.starter.mybatis.service;

import dev.dong4j.zeka.kernel.common.base.AbstractBaseEntity;
import dev.dong4j.zeka.kernel.common.base.ICrudDelegate;
import dev.dong4j.zeka.starter.mybatis.base.BasePO;

/**
 * <p>Description: 实体转换 </p>
 *
 * @param <PO>  parameter
 * @param <DTO> parameter
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:07
 * @since 1.8.0
 */
public interface IExchangeService<PO extends BasePO<?, PO>, DTO extends AbstractBaseEntity<?>>
    extends ICrudDelegate<DTO>, BaseService<PO> {

}
