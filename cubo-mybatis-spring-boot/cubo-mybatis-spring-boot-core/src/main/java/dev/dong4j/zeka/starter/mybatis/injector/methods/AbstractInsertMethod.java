package dev.dong4j.zeka.starter.mybatis.injector.methods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import dev.dong4j.zeka.starter.mybatis.injector.MybatisSqlMethod;
import java.io.Serial;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Description: 抽象的 插入一条数据 (选择字段插入) </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:38
 * @since 1.0.0
 */
public class AbstractInsertMethod extends AbstractMethod {

    @Serial
    private static final long serialVersionUID = -8858194763848035688L;
    /** Sql method */
    private final MybatisSqlMethod sqlMethod;

    /**
     * @since 3.5.0
     */
    protected AbstractInsertMethod(MybatisSqlMethod sqlMethod) {
        super(sqlMethod.getMethod());
        this.sqlMethod = sqlMethod;
    }

    /**
     * Inject mapped statement mapped statement
     *
     * @param mapperClass mapper class
     * @param modelClass  model class
     * @param tableInfo   table info
     * @return the mapped statement
     * @since 1.0.0
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, @NotNull TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        String columnScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlColumnMaybeIf(null),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String valuesScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlPropertyMaybeIf(null),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                // 自增主键
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(this.sqlMethod.getMethod(),
                        tableInfo,
                        this.builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }
        String sql = String.format(this.sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass,
            modelClass,
            this.sqlMethod.getMethod(),
            sqlSource,
            keyGenerator,
            keyProperty,
            keyColumn);
    }
}
