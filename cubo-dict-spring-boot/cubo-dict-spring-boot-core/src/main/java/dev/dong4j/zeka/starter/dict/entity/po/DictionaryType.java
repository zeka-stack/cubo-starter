package dev.dong4j.zeka.starter.dict.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import dev.dong4j.zeka.starter.mybatis.base.BaseExtendPO;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p> 字典类型表 实体类  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_type")
public class DictionaryType extends BaseExtendPO<Long, DictionaryType> {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;
    /** 字典类型编码-表字段 */
    public static final String CODE = "code";
    /** 字典类型名称-表字段 */
    public static final String NAME = "name";
    /** 描述-表字段 */
    public static final String DESCRIPTION = "description";
    /** 字典状态-表字段 */
    public static final String STATE = "state";
    /** 排序-表字段 */
    public static final String ORDER = "order";
    /** 租户ID-表字段 */
    public static final String TENANT_ID = "tenant_id";
    /** 客户端ID-表字段 */
    public static final String CLIENT_ID = "client_id";

    /** 字典类型编码 */
    @TableField("`code`")
    private String code;
    /** 字典类型名称 */
    @TableField("`name`")
    private String name;
    /** 描述 */
    @TableField("`description`")
    private String description;
    /** 字典状态 */
    @TableField("`state`")
    private DictionaryTypeState state;
    /** 排序 */
    @TableField("`order`")
    private Integer order;
    /** 租户ID */
    @TableField("`tenant_id`")
    private String tenantId;
    /** 客户端ID */
    @TableField("`client_id`")
    private String clientId;
}
