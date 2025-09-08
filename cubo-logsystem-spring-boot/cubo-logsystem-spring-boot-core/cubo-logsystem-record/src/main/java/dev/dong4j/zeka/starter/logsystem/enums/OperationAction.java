package dev.dong4j.zeka.starter.logsystem.enums;

/**
 * 操作动作枚举
 *
 * 该枚举定义了系统中各种操作动作的类型，用于分类和标识不同的操作行为。
 * 主要用于系统日志记录中的操作类型分类和统计。
 *
 * 主要功能包括：
 * 1. 定义系统操作动作的标准类型
 * 2. 提供操作动作的代码和名称映射
 * 3. 支持操作类型的分类和统计
 * 4. 便于操作日志的查询和分析
 *
 * 操作类型包括：
 * - 默认操作：DEFAULT
 * - 用户相关：REGISTER、LOGIN、LOGOUT
 * - 数据操作：ADD、UPDATE、DELETE
 * - 文件操作：UPLOAD_FILE、IMPORT
 * - 验证操作：SEND_VERIFY_CODE
 *
 * 使用场景：
 * - 系统操作日志的分类
 * - 操作统计和分析
 * - 操作权限控制
 * - 审计日志的查询
 *
 * 设计意图：
 * 通过枚举定义标准的操作动作类型，确保操作分类的一致性和可维护性，
 * 支持操作统计、分析和权限控制。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongj4@gmail.com"
 * @date 2020.05.26 11:39
 * @since 1.0.0
 */
public enum OperationAction {

    /** Default operation action */
    DEFAULT("DEFAULT", "默认"),
    /** Register operation action */
    REGISTER("REGISTER", "注册"),

    /** Login operation action */
    LOGIN("LOGIN", "登录"),

    /** Logout operation action */
    LOGOUT("LOGOUT", "退出"),

    /** Add operation action */
    ADD("ADD", "新增"),

    /** Update operation action */
    UPDATE("UPDATE", "修改"),

    /** Delete operation action */
    DELETE("DELETE", "删除"),

    /** Upload file operation action */
    UPLOAD_FILE("UPLOAD_FILE", "上传文件"),

    /** Import operation action */
    IMPORT("IMPORT", "导入"),

    /** Send verify code operation action */
    SEND_VERIFY_CODE("SEND_VERIFY_CODE", "发送验证码");

    /** Code */
    private final String code;
    /** Name */
    private final String name;

    /**
     * Operation action
     *
     * @param code code
     * @param name name
     * @since 1.0.0
     */
    OperationAction(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Gets code *
     *
     * @return the code
     * @since 1.0.0
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    public String getName() {
        return this.name;
    }
}
