package dev.dong4j.zeka.starter.mybatis.dict;

import dev.dong4j.zeka.kernel.common.asserts.Assertions;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import java.util.List;
import java.util.function.Supplier;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;

/**
 * 默认 spel 表达式
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 16:28
 * @since 2024.2.0
 */
public class SpelDataBind implements IDataBind {

    /** Spel expression parser */
    private final BeanExpressionResolver parseExpression = new StandardBeanExpressionResolver();

    /**
     * Set meta object
     *
     * @param field      field
     * @param fieldValue field value
     * @param metaObject meta object
     * @since 2024.2.0
     */
    @Override
    public void setMetaObject(FieldBind field, Object fieldValue, MetaObject metaObject) {
        // 最终输出的数据
        String dictIdStr = String.valueOf(fieldValue);
        List<String> dictIds = StringUtils.splitTrim(dictIdStr, StringPool.COMMA);
        String express = field.express();
        if (StringUtils.isBlank(express)) {
            return;
        }
        ApplicationContext applicationContext = SpringContext.getApplicationContext();
        Assertions.notNull(applicationContext, "spring 容器不能为空");
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        Assertions.notNull(beanFactory, "bean 工厂不能为空");
        BeanExpressionContext expressionContext = new InnerParamsExpressionContext((ConfigurableBeanFactory) beanFactory, dictIds);
        Object result = this.parseExpression.evaluate(express, expressionContext);
        if (result != null) {
            metaObject.setValue(field.target(), result);
        }
    }

    /**
     * 自定义内部参数上下文
     *
     * @author zhonghaijun
     * @version 1.0.0
     * @email "mailto:zhonghaijun@zhxx.com"
     * @date 2024.07.11 17:59
     * @since 2024.2.0
     */
    private static final class InnerParamsExpressionContext extends BeanExpressionContext {

        /** PARAMS_KEY */
        public static final String PARAMS_KEY = "params";

        /** Callback */
        private final Supplier<?> callback;

        /**
         * Inner params expression context
         *
         * @param beanFactory bean factory
         * @since 2024.2.0
         */
        InnerParamsExpressionContext(ConfigurableBeanFactory beanFactory, Object params) {
            super(beanFactory, null);
            this.callback = () -> params;
        }

        /**
         * Gets object *
         *
         * @param key key
         * @return the object
         * @since 2024.2.0
         */
        @Override
        @SuppressWarnings("all")
        public Object getObject(String key) {
            boolean paramsContains = PARAMS_KEY.contains(key);
            if (paramsContains) {
                return this.callback.get();
            }
            // 如果传递的数据不是 impl 实现类名称结尾那么可能找不到对应的 bean 对象
            boolean containsObject = super.containsObject(key);
            if (!StringUtils.endsWithIgnoreCase(key, "Impl") && !containsObject) {
                // 找 impl 实现类
                key += "Impl";
            }
            return super.getObject(key);
        }

        /**
         * Contains object
         *
         * @param key key
         * @return the boolean
         * @since 2024.2.0
         */
        @Override
        @SuppressWarnings("all")
        public boolean containsObject(String key) {
            boolean paramsContains = PARAMS_KEY.contains(key);
            if (paramsContains) {
                return true;
            }
            // 如果传递的数据不是 impl 实现类名称结尾那么可能找不到对应的 bean 对象
            boolean containsObject = super.containsObject(key);
            if (!StringUtils.endsWithIgnoreCase(key, "Impl") && !containsObject) {
                // 找 impl 实现类
                key += "Impl";
            }
            return super.containsObject(key);
        }
    }
}
