package dev.dong4j.zeka.starter.mybatis.dict;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 16:03
 * @since 2024.2.0
 */
@Intercepts({@Signature(
    type = ResultSetHandler.class,
    method = "handleResultSets",
    args = {Statement.class}
)})
@SuppressWarnings("all")
public class DataBindInterceptor implements Interceptor {
    /** Dict bind */
    private final IDataBind dictBind;// 字典数据绑定

    /**
     * Demo interceptor
     *
     * @param dictBind dict bind
     * @since 2024.2.0
     */
    public DataBindInterceptor(IDataBind dictBind) {
        this.dictBind = dictBind;
    }

    /**
     * Intercept
     *
     * @param invocation invocation
     * @return the object
     * @throws Throwable throwable
     * @since 2024.2.0
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 一定是list结果
        List proceed = (List) invocation.proceed();
        if (!proceed.isEmpty()) {
            // 获取mybatis的Configuration，用于获得MetaData
            DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
            Field mappedStatement = resultSetHandler.getClass().getDeclaredField("mappedStatement");
            mappedStatement.setAccessible(true);
            MappedStatement o = (MappedStatement) mappedStatement.get(resultSetHandler);
            Configuration configuration = o.getConfiguration();
            // 迭代处理
            for (Object next : proceed) {
                // 检查是否需要翻译，是否需要翻译的标准是，检查目标对象的Class是否有自定义的注解，
                // 有的话，调用字典数据绑定，取修改对象的target属性
                if (null != next && !DataBindUtil.needTranslate(configuration, next,
                    (m, f) -> {
                        // 得到自定义注解
                        FieldBind fieldBind = f.getFieldBind();
                        // 得到具体属性的值
                        Object value = m.getValue(f.getName());
                        dictBind.setMetaObject(fieldBind, value, m);
                    }
                )) {
                    // 不需要翻译的话，跳过
                    break;
                }
            }
        }
        return proceed;
    }
}
