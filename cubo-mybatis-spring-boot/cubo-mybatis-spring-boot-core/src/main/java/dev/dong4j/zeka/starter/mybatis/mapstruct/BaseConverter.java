package dev.dong4j.zeka.starter.mybatis.mapstruct;

import dev.dong4j.zeka.kernel.common.base.BaseDTO;
import dev.dong4j.zeka.kernel.common.base.BaseVO;
import dev.dong4j.zeka.starter.mybatis.base.BasePO;
import java.io.Serializable;

/**
 * 基础转换器接口
 *
 * 该接口继承自 BaseWrapper，提供了对象转换的标准接口定义。
 * 主要用于 MapStruct 等映射工具的基础接口，简化转换器的定义。
 *
 * 主要功能：
 * 1. 继承 BaseWrapper 的所有转换方法
 * 2. 为 MapStruct 转换器提供统一的接口规范
 * 3. 支持 VO、DTO、PO 之间的完整转换链路
 * 4. 提供类型安全的转换接口
 *
 * 使用方式：
 * ```java
 * @Mapper(componentModel = "spring")
 * public interface UserConverter extends BaseConverter<UserVO, UserDTO, UserPO> {
 *     // MapStruct 会自动实现转换方法
 * }
 * ```
 *
 * 设计目的：
 * - 为 MapStruct 转换器提供统一的基础接口
 * - 减少重复的接口定义
 * - 确保转换方法的一致性
 * - 提供类型安全的转换操作
 *
 * 注意：该接口已标记为废弃，建议使用更专业的转换接口：
 * - 使用 kernel 模块中的 ViewConverter 和 ServiceConverter
 * - 采用更细粒度的转换接口设计
 * - 使用现代化的对象映射工具
 *
 * @param <V> 视图对象类型，继承自 BaseVO
 * @param <D> 数据传输对象类型，继承自 BaseDTO
 * @param <P> 持久化对象类型，继承自 BasePO
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.30 16:56
 * @since 1.0.0
 * @deprecated 建议使用 kernel 模块中的专业转换接口
 */
@Deprecated
public interface BaseConverter<V extends BaseVO<? extends Serializable>,
    D extends BaseDTO<? extends Serializable>,
    P extends BasePO<? extends Serializable, P>> extends BaseWrapper<V, D, P> {
}
