package dev.dong4j.zeka.starter.rest.entity;

import dev.dong4j.zeka.kernel.common.annotation.SerializeValue;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 15:17
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UserType implements SerializeEnum<Integer> {
    /** N a user type */
    N_A(0, StringPool.NULL_STRING),
    /** Tenant own user type */
    TENANT(1, "租户"),
    /** Platform fleet user type */
    ADMIN(2, "系统管理员"),
    /** Platform driver user type */
    SADMIN(3, "超级管理员");

    /** Value */
    @SerializeValue
    private final Integer value;
    /** Desc */
    private final String desc;

}


