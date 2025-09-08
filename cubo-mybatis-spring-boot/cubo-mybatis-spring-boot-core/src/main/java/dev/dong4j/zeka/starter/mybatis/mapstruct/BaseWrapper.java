package dev.dong4j.zeka.starter.mybatis.mapstruct;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseVO;
import dev.dong4j.zeka.kernel.common.mapstruct.ServiceConverter;
import dev.dong4j.zeka.kernel.common.mapstruct.ViewConverter;
import dev.dong4j.zeka.starter.mybatis.base.BasePO;
import java.io.Serializable;

/**
 * 基础对象转换器接口
 *
 * 该接口定义了 VO（视图对象）、DTO（数据传输对象）、PO（持久化对象）
 * 之间的相互转换方法，适用于单模块内对象能相互访问的场景。
 *
 * 主要功能：
 * 1. VO 和 DTO 之间的双向转换
 * 2. DTO 和 PO 之间的双向转换
 * 3. VO 和 PO 之间的直接转换
 * 4. 提供完整的对象转换链路
 *
 * 转换关系：
 * - VO ↔ DTO：前端展示层和业务传输层之间的转换
 * - DTO ↔ PO：业务传输层和数据持久层之间的转换
 * - VO ↔ PO：前端展示层和数据持久层之间的直接转换
 *
 * 使用场景：
 * - 单模块应用，各层对象可以相互访问
 * - 需要灵活的对象转换机制
 * - 简单的分层架构应用
 *
 * 注意：该接口已标记为废弃，建议使用更专业的转换接口：
 * - ViewConverter：专门处理视图层转换
 * - ServiceConverter：专门处理服务层转换
 * - 使用 MapStruct 等现代映射工具
 *
 * @param <V> 视图对象类型，继承自 BaseVO
 * @param <D> 数据传输对象类型，继承自 BaseDTO
 * @param <P> 持久化对象类型，继承自 BasePO
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:08
 * @see ServiceConverter
 * @see ViewConverter
 * @since 1.0.0
 * @deprecated 请使用 {@link ViewConverter} 或 {@link ServiceConverter}
 */
@Deprecated
public interface BaseWrapper<V extends BaseVO<? extends Serializable>,
    D extends BaseDTO<? extends Serializable>,
    P extends BasePO<? extends Serializable, P>> {

    /**
     * vo 转 dto
     *
     * @param vo the vo
     * @return the d
     * @since 1.0.0
     */
    D dto(V vo);

    /**
     * dto 转 vo
     *
     * @param dto the dto
     * @return the v
     * @since 1.0.0
     */
    V vo(D dto);

    /**
     * dto 转 po
     *
     * @param dto the dto
     * @return the p
     * @since 1.0.0
     */
    P po(D dto);

    /**
     * po 转 dto
     *
     * @param po the po
     * @return the d
     * @since 1.0.0
     */
    D dto(P po);

    /**
     * To vo user vo.
     *
     * @param po the po
     * @return the user vo
     * @since 1.0.0
     */
    V vo(P po);

    /**
     * To po user.
     *
     * @param vo the vo
     * @return the user
     * @since 1.0.0
     */
    P po(V vo);
}
