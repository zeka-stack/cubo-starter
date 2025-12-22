package dev.dong4j.zeka.starter.dict.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;

import dev.dong4j.zeka.starter.dict.enums.DictionaryValueState;
import dev.dong4j.zeka.starter.mybatis.base.BaseExtendPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典值实体类
 * <p> 该类用于表示系统字典中的值, 提供了字典值的基本信息, 包括类型代码, 编码, 名称, 描述, 排序, 状态, 租户 ID 和客户端 ID 等.
 * <p> 通过继承自 BaseExtendPO, 该类具备基本的持久化操作能力, 并且支持链式调用设置属性.
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
@TableName("sys_dict_value")
public class DictionaryValue extends BaseExtendPO<Long, DictionaryValue> {

    /**
     * 序列化版本标识符
     * <p> 用于序列化和反序列化对象时保持兼容性
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /** 字典类型编码 - 表字段 */
    public static final String TYPE_CODE = "type_code";
    /** 字典值编码 */
    public static final String CODE = "code";
    /** 字典值名称对应的表字段 */
    public static final String NAME = "name";
    /** 字典值描述 */
    public static final String DESCRIPTION = "description";
    /** 排序字段, 用于表示字典值的排序顺序 */
    public static final String ORDER = "order";
    /** 字典值状态 - 表字段 */
    public static final String STATE = "state";
    /** 租户 ID- 表字段 */
    public static final String TENANT_ID = "tenant_id";
    /** 客户端 ID- 表字段 */
    public static final String CLIENT_ID = "client_id";

    /** 字典类型编码 */
    @TableField("`type_code`")
    private String typeCode;
    /**
     * 字典值编码
     * <p> 用于标识字典值的唯一编码, 对应数据库中的 `code` 字段.</p>
     */
    @TableField("`code`")
    private String code;
    /** 字典值名称 */
    @TableField("`name`")
    private String name;
    /**
     * 字典值描述
     * <p> 用于存储字典项的详细描述信息
     */
    @TableField("`description`")
    private String description;
    /**
     * 排序
     * <p> 用于表示字典值在列表中的顺序 </p>
     */
    @TableField("`order`")
    private Integer order;
    /** 字典值状态 */
    @TableField("`state`")
    private DictionaryValueState state;
    /**
     * 租户 ID
     * <p> 表示当前字典值所属的租户标识 </p>
     */
    @TableField("`tenant_id`")
    private String tenantId;
    /**
     * 客户端 ID
     * <p> 表示当前字典值所属的客户端标识
     */
    @TableField("`client_id`")
    private String clientId;
}
