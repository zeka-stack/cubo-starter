package dev.dong4j.zeka.starter.logsystem.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>Description: 实体类 </p>
 *
 * @author dong4j
 * @version 1.2.3
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
