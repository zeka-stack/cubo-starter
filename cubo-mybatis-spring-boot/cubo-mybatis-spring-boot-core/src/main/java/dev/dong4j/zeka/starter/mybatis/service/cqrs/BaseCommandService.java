package dev.dong4j.zeka.starter.mybatis.service.cqrs;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

/**
 * CQRS 模式基础命令服务接口
 *
 * 该接口基于 CQRS（命令查询职责分离）模式设计，专门处理写操作（命令）。
 * 将读操作和写操作分离，提高系统的可扩展性和性能。
 *
 * 主要功能：
 * 1. 提供基础的数据写入操作（插入、更新、删除）
 * 2. 支持忽略重复数据的插入操作
 * 3. 与 BaseCommandMapper 配合使用
 * 4. 专注于命令操作，不包含查询功能
 *
 * CQRS 模式优势：
 * - 读写分离，提高系统性能
 * - 独立优化读写操作
 * - 支持不同的数据存储策略
 * - 便于系统扩展和维护
 *
 * 使用场景：
 * - 高并发的写操作场景
 * - 需要读写分离的系统架构
 * - 复杂的业务写入逻辑
 * - 事件驱动的系统设计
 *
 * @param <DTO> 数据传输对象类型
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.02.28 01:53
 * @since 1.0.0
 */
public interface BaseCommandService<DTO> {
    /**
     * 获取基础命令映射器
     *
     * 该方法返回与当前服务关联的命令映射器，用于执行数据库的写操作。
     * 命令映射器专门处理插入、更新、删除等写入操作。
     *
     * @return BaseCommandMapper<DTO> 基础命令映射器实例
     * @since 1.0.0
     */
    BaseCommandMapper<DTO> getBaseMapper();

    /**
     * 保存数据
     *
     * 该方法执行标准的数据插入操作，将 DTO 对象保存到数据库中。
     * 使用 SqlHelper.retBool 方法将影响行数转换为布尔值结果。
     *
     * 注意：在实际使用中，可能需要将 DTO 转换为 PO 对象后再保存
     *
     * @param dto 要保存的数据传输对象
     * @return boolean 保存是否成功，true 表示成功，false 表示失败
     * @since 1.0.0
     */
    default boolean save(DTO dto) {
        return SqlHelper.retBool(this.getBaseMapper().insert(dto));
    }

    /**
     * 忽略重复数据保存
     *
     * 该方法执行 INSERT IGNORE 操作，当数据已存在时会忽略插入操作，
     * 不会抛出异常。适用于批量插入或幂等操作场景。
     *
     * @param entity 要保存的实体对象
     * @return boolean 保存是否成功，true 表示成功插入，false 表示被忽略或失败
     * @since 1.0.0
     */
    boolean saveIgnore(DTO entity);
}
