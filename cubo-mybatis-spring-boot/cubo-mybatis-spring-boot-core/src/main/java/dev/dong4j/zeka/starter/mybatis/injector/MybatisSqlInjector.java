package dev.dong4j.zeka.starter.mybatis.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import dev.dong4j.zeka.starter.mybatis.injector.methods.InsertIgnore;
import dev.dong4j.zeka.starter.mybatis.injector.methods.Replace;
import dev.dong4j.zeka.starter.mybatis.service.impl.BaseServiceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.session.Configuration;

/**
 * 自定义的 sql 注入
 * 注入 insrt ignore 和 replace
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:52
 * @since 1.0.0
 */
public class MybatisSqlInjector extends DefaultSqlInjector {

    /**
     * Gets method list *
     *
     * @param mapperClass mapper class
     * @return the method list
     * @since 1.0.0
     * @see BaseServiceImpl#saveIgnoreBatch(Collection, int)
     * @see BaseServiceImpl#saveReplaceBatch(Collection, int)
     */
    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = new ArrayList<>();
        methodList.add(new InsertIgnore());
        methodList.add(new Replace());
        methodList.addAll(super.getMethodList(configuration, mapperClass, tableInfo));
        return Collections.unmodifiableList(methodList);
    }
}
