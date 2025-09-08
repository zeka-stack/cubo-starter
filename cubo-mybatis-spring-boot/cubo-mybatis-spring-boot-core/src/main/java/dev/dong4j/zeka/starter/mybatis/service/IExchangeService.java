package dev.dong4j.zeka.starter.mybatis.service;

import dev.dong4j.zeka.kernel.common.base.AbstractBaseEntity;
import dev.dong4j.zeka.kernel.common.base.ICrudDelegate;
import dev.dong4j.zeka.starter.mybatis.base.BasePO;

/**
 * 实体转换服务接口
 *
 * 该接口提供了 PO（持久化对象）和 DTO（数据传输对象）之间的转换服务，
 * 结合了 CRUD 操作和 MyBatis Plus 的基础服务功能。
 *
 * 主要功能：
 * 1. 继承 ICrudDelegate 接口，提供标准的 CRUD 操作
 * 2. 继承 BaseService 接口，提供 MyBatis Plus 的扩展功能
 * 3. 支持 PO 和 DTO 之间的自动转换
 * 4. 提供统一的数据访问接口
 *
 * 设计理念：
 * - 分离持久化对象和业务传输对象
 * - 提供统一的服务层接口规范
 * - 支持复杂的业务逻辑处理
 * - 便于单元测试和接口模拟
 *
 * 适用场景：
 * - 需要 PO/DTO 转换的业务服务
 * - 复杂的业务逻辑处理
 * - 分层架构的服务层实现
 * - 需要统一数据访问接口的场景
 *
 * @param <PO> 持久化对象类型，继承自 BasePO
 * @param <DTO> 数据传输对象类型，继承自 AbstractBaseEntity
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.15 19:07
 * @since 1.0.0
 */
public interface IExchangeService<PO extends BasePO<?, PO>, DTO extends AbstractBaseEntity<?>>
    extends ICrudDelegate<DTO>, BaseService<PO> {

}
