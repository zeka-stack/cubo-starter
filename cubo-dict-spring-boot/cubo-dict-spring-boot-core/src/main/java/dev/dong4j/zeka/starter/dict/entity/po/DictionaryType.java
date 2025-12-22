package dev.dong4j.zeka.starter.dict.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;

import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import dev.dong4j.zeka.starter.mybatis.base.BaseExtendPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典类型实体类
 * <p> 用于表示系统中字典类型的元数据信息, 包括代码, 名称, 描述, 状态, 排序等属性, 与数据库表 sys_dict_type 对应.
 * 该类继承自 BaseExtendPO, 用于支持通用的持久化操作.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dict_type")
public class DictionaryType extends BaseExtendPO<Long, DictionaryType> {

    /**
     * 序列化版本 UID
     * <p> 用于支持序列化的版本控制, 确保在反序列化时版本匹配 </p>
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /** 字典类型编码 - 表字段 */
    public static final String CODE = "code";
    /** 字典类型名称 - 表字段 */
    public static final String NAME = "name";
    /**
     * 描述
     * <p> 对应数据库字段为 "description"</p>
     */
    public static final String DESCRIPTION = "description";
    /** 字典状态字段名 */
    public static final String STATE = "state";
    /**
     * 排序字段的名称常量
     *
     * @see #order
     */
    public static final String ORDER = "order";
    /** 租户 ID- 表字段 */
    public static final String TENANT_ID = "tenant_id";
    /** 客户端 ID */
    public static final String CLIENT_ID = "client_id";

    /** 字典类型编码 */
    @TableField("`code`")
    private String code;
    /** 字典类型名称 */
    @TableField("`name`")
    private String name;
    /** 描述信息 */
    @TableField("`description`")
    private String description;
    /**
     * 字典状态
     * <p> 表示字典类型的当前状态 </p>
     *
     * @see DictionaryTypeState
     */
    @TableField("`state`")
    private DictionaryTypeState state;
    /** 排序字段, 用于指定字典类型的排序顺序 */
    @TableField("`order`")
    private Integer order;
    /**
     * 租户 ID
     * <p> 表示当前字典类型的所属租户标识
     */
    @TableField("`tenant_id`")
    private String tenantId;
    /**
     * 客户端 ID
     * <p> 用于标识不同的客户端
     */
    @TableField("`client_id`")
    private String clientId;
}
