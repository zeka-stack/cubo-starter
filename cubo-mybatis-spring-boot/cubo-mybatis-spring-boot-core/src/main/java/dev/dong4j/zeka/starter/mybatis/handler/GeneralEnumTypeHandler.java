package dev.dong4j.zeka.starter.mybatis.handler;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import dev.dong4j.zeka.kernel.common.annotation.SerializeValue;
import dev.dong4j.zeka.kernel.common.enums.DeletedEnum;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.enums.serialize.EntityEnumDeserializer;
import dev.dong4j.zeka.kernel.common.enums.serialize.EntityEnumSerializer;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 为了解决 mybatis-plus 通用枚举的局限性, 减少 SDK 模块的依赖,
 * 由于 {@link MybatisEnumTypeHandler} 未考虑到扩展性, 这里拷贝 {@link MybatisEnumTypeHandler} 代码,
 * 使用系统自定义枚举 {@link SerializeEnum} 代替 {@link IEnum} 和 {@link EnumValue},
 * 修改后 web 层不需要 mybatis-plus 依赖也可对枚举进行序列化/反序列化, 且 web 层和 service 层可共用相同的枚举.
 * 注意: 不要使用 typeEnumsPackage 来扫描通用枚举, 扫描后将使用 MybatisEnumTypeHandler 处理,
 * 可查看 ({@link MybatisSqlSessionFactoryBean#buildSqlSessionFactory()})
 * </p>
 *
 * @param <E> parameter
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 18:35
 * @see MybatisEnumTypeHandler
 * @see SerializeEnum
 * @see EntityEnumSerializer
 * @see EntityEnumDeserializer
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class GeneralEnumTypeHandler<E extends Enum<?>> extends BaseTypeHandler<Enum<?>> {

    /** REFLECTOR_FACTORY */
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    /** TABLE_METHOD_OF_ENUM_TYPES */
    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap<>();
    /** Type */
    private final Class<E> type;
    /** Invoker */
    private final Invoker invoker;

    /**
     * General enum type handler
     *
     * @param type type
     * @since 1.0.0
     */
    @Contract("null -> fail")
    public GeneralEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }

        this.type = type;
        MetaClass metaClass = MetaClass.forClass(type, REFLECTOR_FACTORY);
        String name = SerializeEnum.VALUE_FILED_NAME;

        // 不是 SerializeEnum 枚举时
        if (!SerializeEnum.class.isAssignableFrom(type)) {
            name = findEnumValueFieldName(this.type)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Could not find @SerializeValue in Class: %s.",
                    this.type.getName())));
        }
        // 是 SerializeEnum 子类, 则使用 SerializeEnum.getValue
        this.invoker = metaClass.getGetInvoker(name);
    }

    /**
     * 写入 DB 转换
     *
     * @param ps        ps
     * @param i         the first parameter is 1, the second is 2, ...
     * @param parameter parameter
     * @param jdbcType  jdbc type
     * @throws SQLException sql exception
     * @since 1.0.0
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Enum<?> parameter, JdbcType jdbcType)
        throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    /**
     * 读取 DB 时转换
     *
     * @param rs         rs
     * @param columnName column name
     * @return the nullable result
     * @throws SQLException sql exception
     * @since 1.0.0
     */
    @Override
    public E getNullableResult(@NotNull ResultSet rs, String columnName) throws SQLException {
        if (null == rs.getObject(columnName) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnName));
    }

    /**
     * 读取 DB 时转换
     *
     * @param rs          rs
     * @param columnIndex column index
     * @return the nullable result
     * @throws SQLException sql exception
     * @since 1.0.0
     */
    @Override
    public E getNullableResult(@NotNull ResultSet rs, int columnIndex) throws SQLException {
        if (null == rs.getObject(columnIndex) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnIndex));
    }

    /**
     * 读取 DB 时转换
     *
     * @param cs          cs
     * @param columnIndex column index
     * @return the nullable result
     * @throws SQLException sql exception
     * @since 1.0.0
     */
    @Override
    public E getNullableResult(@NotNull CallableStatement cs, int columnIndex) throws SQLException {
        if (null == cs.getObject(columnIndex) && cs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, cs.getObject(columnIndex));
    }

    /**
     * 查找标记EnumValue字段
     *
     * @param clazz class
     * @return EnumValue字段 optional
     * @since 1.0.0
     * @deprecated 3.3.1 {@link #findEnumValueFieldName(Class)}
     */
    @Deprecated
    public static Optional<Field> dealEnumType(@NotNull Class<?> clazz) {
        return clazz.isEnum()
            ? Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(SerializeValue.class)).findFirst()
            : Optional.empty();
    }

    /**
     * 查找标记标记 EnumValue 字段
     *
     * @param clazz class
     * @return EnumValue字段 optional
     * @since 3.3.1
     */
    private static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            return Optional.ofNullable(TABLE_METHOD_OF_ENUM_TYPES.computeIfAbsent(className, key -> {
                Optional<Field> optional = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(SerializeValue.class))
                    .findFirst();
                return optional.map(Field::getName).orElse(null);
            }));
        }
        return Optional.empty();
    }

    /**
     * 判断是否为MP枚举处理
     *
     * @param clazz class
     * @return 是否为MP枚举处理 boolean
     * @since 3.3.1
     */
    @Contract("null -> false")
    public static boolean isMpEnums(Class<?> clazz) {
        return clazz != null
            && clazz.isEnum()
            && (SerializeEnum.class.isAssignableFrom(clazz) || findEnumValueFieldName(clazz).isPresent());
    }

    /**
     * Value of e
     *
     * @param enumClass enum class
     * @param value     value
     * @return the e
     * @since 1.0.0
     */
    private E valueOf(@NotNull Class<E> enumClass, Object value) {
        E[] es = enumClass.getEnumConstants();
        if (DeletedEnum.class.getName().equals(enumClass.getName()) && value instanceof Number) {
            // 如果是数值类型, 则应该是使用了大于0表示已删除的方式来处理 deleted 字段, 因此需要特殊处理
            if (new BigDecimal(String.valueOf(value)).compareTo(BigDecimal.ZERO) == 0) {
                // 如果等于 0 表示未删除, 否则为已删除(不考虑小于0的情况))
                value = DeletedEnum.N.getValue();
            } else {
                value = DeletedEnum.Y.getValue();
            }
        }
        // 其他枚举走原有逻辑
        Object finalValue = value;
        return Arrays.stream(es).filter((e) -> this.equalsValue(finalValue, this.getValue(e))).findAny().orElse(null);
    }

    /**
     * 值比较
     *
     * @param sourceValue 数据库字段值
     * @param targetValue 当前枚举属性值
     * @return 是否匹配 boolean
     * @since 3.3.0
     */
    private boolean equalsValue(Object sourceValue, Object targetValue) {
        if (sourceValue instanceof Number && targetValue instanceof Number
            && new BigDecimal(String.valueOf(sourceValue)).compareTo(new BigDecimal(String.valueOf(targetValue))) == 0) {
            return true;
        }
        return Objects.equals(sourceValue, targetValue);
    }

    /**
     * Gets value *
     *
     * @param object object
     * @return the value
     * @since 1.0.0
     */
    private Object getValue(Object object) {
        try {
            return this.invoker.invoke(object, new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}
