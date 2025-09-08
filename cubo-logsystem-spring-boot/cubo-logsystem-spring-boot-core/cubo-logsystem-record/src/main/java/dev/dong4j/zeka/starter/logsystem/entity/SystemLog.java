package dev.dong4j.zeka.starter.logsystem.entity;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统日志实体类
 *
 * 该类用于记录系统操作的日志信息，继承自AbstractLog基类。
 * 主要用于记录系统敏感操作的审计日志，包括操作名称、操作动作等。
 *
 * 主要功能包括：
 * 1. 记录系统敏感操作的审计日志
 * 2. 包含操作名称和操作动作信息
 * 3. 支持操作类型的分类和统计
 * 4. 提供完整的操作追踪能力
 *
 * 特有属性：
 * - 操作名称：系统操作的具体名称
 * - 操作动作：操作的类型（新增、修改、删除等）
 *
 * 使用场景：
 * - 系统敏感操作的审计日志记录
 * - 用户操作的追踪和监控
 * - 系统安全审计
 * - 操作统计和分析
 *
 * 设计意图：
 * 通过专门的系统日志实体，提供完整的操作审计能力，
 * 支持系统安全监控和操作分析。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 05:28
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class SystemLog extends AbstractLog implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 系统操作 */
    private String operationName;
    /** 系统操作动作:enums.dev.dong4j.zeka.starter.logsystem.OperationAction */
    private String operationAction;

}
