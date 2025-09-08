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
 * 自定义 SQL 注入器
 *
 * 该类继承自 MyBatis Plus 的 DefaultSqlInjector，用于向 Mapper 接口中
 * 注入自定义的 SQL 方法。主要功能包括：
 *
 * 1. 注入 INSERT IGNORE 方法，支持插入时忽略重复数据
 * 2. 注入 REPLACE INTO 方法，支持替换插入操作
 * 3. 保留 MyBatis Plus 默认的所有方法
 *
 * 注入的方法：
 * - insertIgnore：MySQL 的 INSERT IGNORE 语法
 * - replace：MySQL 的 REPLACE INTO 语法
 *
 * 这些方法可以在 BaseDao 接口中直接使用，也可以通过 BaseService
 * 的批量操作方法间接使用。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:52
 * @since 1.0.0
 */
public class MybatisSqlInjector extends DefaultSqlInjector {

    /**
     * 获取方法列表
     *
     * 该方法重写了父类的 getMethodList 方法，用于向 Mapper 接口注入自定义的 SQL 方法。
     *
     * 注入的方法包括：
     * 1. InsertIgnore：实现 INSERT IGNORE 功能
     * 2. Replace：实现 REPLACE INTO 功能
     * 3. 所有 MyBatis Plus 默认方法
     *
     * 返回的方法列表是不可修改的，确保注入方法的稳定性。
     *
     * @param configuration MyBatis 配置对象
     * @param mapperClass Mapper 接口类
     * @param tableInfo 表信息对象
     * @return List<AbstractMethod> 不可修改的方法列表
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
