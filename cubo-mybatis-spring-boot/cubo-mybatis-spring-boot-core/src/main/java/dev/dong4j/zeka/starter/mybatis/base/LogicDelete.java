package dev.dong4j.zeka.starter.mybatis.base;

import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;

/**
 * 逻辑删除字段接口
 * <p>
 * 该接口定义了逻辑删除功能的标准规范，用于实现软删除机制。
 * 实现该接口的实体类将支持逻辑删除功能，而不是物理删除数据。
 * <p>
 * 主要功能：
 * 1. 定义删除状态字段的访问方法
 * 2. 提供删除状态字段名称常量
 * 3. 支持 MyBatis Plus 的逻辑删除功能
 * <p>
 * 逻辑删除的优势：
 * - 数据安全：避免误删除导致的数据丢失
 * - 数据恢复：可以轻松恢复被删除的数据
 * - 审计追踪：保留完整的数据变更历史
 * - 关联完整性：避免删除数据导致的关联关系破坏
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.22 13:53
 * @since 1.0.0
 */
public interface LogicDelete {
    /** DELETED */
    String DELETED = "deleted";

    /**
     * 获取删除状态
     * <p>
     * 该方法用于获取数据记录的删除状态，用于实现逻辑删除功能。
     * 返回的枚举值表示数据是否被逻辑删除：
     * - NORMAL：正常状态，数据未被删除
     * - DELETED：已删除状态，数据被逻辑删除
     *
     * @return DeletedEnum 删除状态枚举
     * @since 1.0.0
     */
    DeletedEnum getDeleted();
}
