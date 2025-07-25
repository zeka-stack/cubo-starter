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
 * <p>Description: id 转换器 </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 22:59
 * @since 1.0.0
 */
public class SerializableIdTypeHandler extends BaseTypeHandler<Serializable> {
    /** Type */
    private final Class<?> type;

    /**
     * Serializable id type handler
     *
     * @param type type
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
     * Sets non null parameter *
     *
     * @param ps        ps
     * @param i         the first parameter is 1, the second is 2, ...
     * @param parameter parameter
     * @param jdbcType  jdbc type
     * @throws SQLException sql exception
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
     * Gets nullable result *
     *
     * @param cs          cs
     * @param columnIndex column index
     * @return the nullable result
     * @throws SQLException sql exception
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
     * Value of
     *
     * @param idClass id class
     * @param value   value
     * @return the serializable
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
     * Gets value *
     *
     * @param object object
     * @return the value
     * @since 1.0.0
     */
    @Contract(value = "_ -> param1", pure = true)
    private Object getValue(Object object) {
        return object;
    }
}
