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
 * 抽象插入方法基类
 *
 * 该抽象类为自定义插入方法提供基础实现，支持选择性字段插入。
 * 继承自 MyBatis Plus 的 AbstractMethod，提供了插入方法的通用逻辑。
 *
 * 主要功能：
 * 1. 处理插入 SQL 的动态生成
 * 2. 支持主键生成策略（自增、序列等）
 * 3. 处理字段的选择性插入（只插入非空字段）
 * 4. 生成对应的 MappedStatement
 *
 * 设计特点：
 * - 使用模板方法模式，子类只需提供具体的 SQL 方法类型
 * - 自动处理主键生成逻辑
 * - 支持不同的插入策略（INSERT IGNORE、REPLACE INTO 等）
 *
 * 子类实现：
 * - InsertIgnore：实现 INSERT IGNORE 功能
 * - Replace：实现 REPLACE INTO 功能
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.26 21:38
 * @since 1.0.0
 */
public class AbstractInsertMethod extends AbstractMethod {

    @Serial
    private static final long serialVersionUID = -8858194763848035688L;
    /** SQL 方法枚举 */
    private final MybatisSqlMethod sqlMethod;

    /**
     * 构造方法
     *
     * 创建抽象插入方法实例，使用指定的 SQL 方法类型。
     *
     * @param sqlMethod SQL 方法枚举，定义了方法名和 SQL 模板
     * @since 1.0.0
     */
    protected AbstractInsertMethod(MybatisSqlMethod sqlMethod) {
        super(sqlMethod.getMethod());
        this.sqlMethod = sqlMethod;
    }

    /**
     * 注入映射语句
     *
     * 该方法负责生成并注入自定义插入方法的 MappedStatement。
     * 主要处理以下逻辑：
     * 1. 根据表信息生成字段和值的 SQL 脚本
     * 2. 处理主键生成策略（自增、序列等）
     * 3. 构建完整的 SQL 语句
     * 4. 创建并返回 MappedStatement
     *
     * @param mapperClass Mapper 接口类
     * @param modelClass 实体模型类
     * @param tableInfo 表信息对象，包含表结构和字段信息
     * @return MappedStatement 映射语句对象
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
