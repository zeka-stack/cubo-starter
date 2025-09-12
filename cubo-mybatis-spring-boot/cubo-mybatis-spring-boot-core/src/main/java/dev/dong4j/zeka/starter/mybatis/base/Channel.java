package dev.dong4j.zeka.starter.mybatis.base;

/**
 * 数据渠道标识接口
 * <p>
 * 该接口定义了多租户和多客户端场景下的数据渠道标识规范。
 * 实现该接口的实体类将自动支持租户 ID 和客户端 ID 的自动填充功能。
 * <p>
 * 主要功能：
 * 1. 定义租户 ID 字段的访问方法，用于多租户数据隔离
 * 2. 定义客户端 ID 字段的访问方法，用于多客户端数据标识
 * 3. 提供字段名称常量，便于统一管理
 * <p>
 * 应用场景：
 * - SaaS 多租户系统中的数据隔离
 * - 多客户端应用的数据来源追踪
 * - 数据权限控制和访问限制
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.03.08 14:47
 * @since 1.0.0
 */
public interface Channel {

    /** TENANT_ID */
    String TENANT_ID = "tenant_id";
    /** CLIENT_ID */
    String CLIENT_ID = "client_id";

    /**
     * 获取租户 ID
     * <p>
     * 该方法用于获取数据记录所属的租户标识，在多租户系统中用于数据隔离。
     * 租户 ID 通常在数据插入时自动设置，确保数据归属的正确性。
     *
     * @return Long 租户 ID
     * @since 1.0.0
     */
    Long getTenantId();

    /**
     * 获取客户端 ID
     * <p>
     * 该方法用于获取数据记录的客户端标识，用于标识数据的来源客户端。
     * 客户端 ID 通常在数据插入时自动设置，便于数据来源追踪和统计分析。
     *
     * @return String 客户端 ID
     * @since 1.0.0
     */
    String getClientId();

}
