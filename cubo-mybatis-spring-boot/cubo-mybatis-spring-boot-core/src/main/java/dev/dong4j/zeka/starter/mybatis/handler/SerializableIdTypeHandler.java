package dev.dong4j.zeka.starter.mybatis.handler;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Serializable ID 类型处理器
 *
 * 该类型处理器用于处理 Serializable 类型的 ID 字段在数据库和 Java 对象之间的转换。
 * 主要功能包括：
 *
 * 1. 支持常见的 ID 类型转换（String、Integer、Long）
 * 2. 处理数据库查询结果到 Java 对象的类型转换
 * 3. 处理 Java 对象到数据库参数的类型转换
 * 4. 提供空值安全的转换机制
 *
 * 支持的 ID 类型：
 * - String：字符串类型的 ID
 * - Integer：整型 ID
 * - Long：长整型 ID
 *
 * 使用场景：
 * - 统一处理不同类型的主键字段
 * - 避免类型转换异常
 * - 提供类型安全的 ID 处理机制
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:59
 * @since 1.0.0
 */
public class SerializableIdTypeHandler extends BaseTypeHandler<Serializable> {
    /** Type */
    private final Class<?> type;

    /**
     * 构造方法
     *
     * 创建 SerializableIdTypeHandler 实例，用于处理指定类型的 ID 转换。
     *
     * @param type ID 的具体类型（如 String.class、Long.class 等）
     * @throws IllegalArgumentException 当 type 参数为 null 时抛出
     * @since 1.0.0
     */
    @Contract("null -> fail")
    public SerializableIdTypeHandler(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }

        this.type = type;
    }

    /**
     * 设置非空参数
     *
     * 该方法在执行 SQL 语句时被调用，用于将 Java 对象的参数值设置到 PreparedStatement 中。
     * 支持指定 JDBC 类型或使用默认类型。
     *
     * @param ps PreparedStatement 对象
     * @param i 参数索引（从 1 开始）
     * @param parameter 要设置的参数值
     * @param jdbcType JDBC 类型，可以为 null
     * @throws SQLException 当设置参数失败时抛出
     * @since 1.0.0
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps,
                                    int i,
                                    Serializable parameter,
                                    JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    /**
     * 通过列表转换结果
     *
     * @param rs         rs
     * @param columnName column name
     * @return the nullable result
     * @throws SQLException sql exception
     * @since 1.0.0
     */
    @Override
    public Serializable getNullableResult(@NotNull ResultSet rs, String columnName) throws SQLException {
        if (null == rs.getObject(columnName) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnName));
    }

    /**
     * 通过字段下标获取转换结果
     *
     * @param rs          rs
     * @param columnIndex column index
     * @return the nullable result
     * @throws SQLException sql exception
     * @since 1.0.0
     */
    @Override
    public Serializable getNullableResult(@NotNull ResultSet rs, int columnIndex) throws SQLException {
        if (null == rs.getObject(columnIndex) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnIndex));
    }

    /**
     * 从 CallableStatement 获取可空结果
     *
     * 该方法用于从存储过程调用结果中获取指定列的值，并转换为 Serializable 类型。
     * 如果数据库中的值为 null，则返回 null。
     *
     * @param cs CallableStatement 对象
     * @param columnIndex 列索引（从 1 开始）
     * @return Serializable 转换后的结果，可能为 null
     * @throws SQLException 当获取结果失败时抛出
     * @since 1.0.0
     */
    @Override
    public Serializable getNullableResult(@NotNull CallableStatement cs, int columnIndex) throws SQLException {
        if (null == cs.getObject(columnIndex) && cs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, cs.getObject(columnIndex));
    }

    /**
     * 值类型转换
     *
     * 该方法根据指定的 ID 类型将数据库中的值转换为对应的 Java 类型。
     * 目前支持 String、Integer、Long 三种类型的转换。
     *
     * 支持的类型转换：
     * - String：直接转换为字符串类型
     * - Integer：转换为整型
     * - Long：转换为长整型
     *
     * @param idClass ID 的目标类型
     * @param value 数据库中的原始值
     * @return Serializable 转换后的值
     * @throws MybatisPlusException 当遇到不支持的 ID 类型时抛出
     * @since 1.0.0
     */
    private Serializable valueOf(@NotNull Class<?> idClass, Object value) {
        if (String.class.isAssignableFrom(idClass)) {
            return (String) value;
        } else if (Integer.class.isAssignableFrom(idClass)) {
            return (Integer) value;
        } else if (Long.class.isAssignableFrom(idClass)) {
            return (Long) value;
        }
        throw new MybatisPlusException("暂不支持的 id 类型");
    }

    /**
     * 获取参数值
     *
     * 该方法用于获取对象的值，当前实现直接返回原对象。
     * 这是一个简单的传递方法，用于保持接口的一致性。
     *
     * @param object 输入对象
     * @return Object 返回原对象
     * @since 1.0.0
     */
    @Contract(value = "_ -> param1", pure = true)
    private Object getValue(Object object) {
        return object;
    }
}
