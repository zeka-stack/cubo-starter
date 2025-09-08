package dev.dong4j.zeka.starter.launcher.autoconfigure;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 懒加载 Bean 工厂后置处理器，用于提高应用启动速度
 *
 * 该类通过将所有 Bean 设置为懒加载模式来优化启动性能。
 * 注意：此实现可能会导致某些自动配置类失效，特别是来自第三方 jar 的自动配置类。
 *
 * 使用时应谨慎评估影响，建议仅在开发环境或特定场景下启用。
 *
 * @todo 需要修复可能导致自动装配类失效的问题 (2020年04月02日 00:07)
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.01 10:29
 * @since 1.0.0
 */
public class LazyInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    /**
     * 处理 Bean 工厂，将所有 Bean 设置为懒加载模式
     *
     * 此方法会遍历所有已注册的 Bean 定义，并将它们的懒加载属性设置为 true。
     * 这可以延迟 Bean 的实例化，从而提高应用启动速度。
     *
     * @param beanFactory 可配置的 Bean 工厂
     * @throws BeansException 如果处理过程中发生错误
     * @since 1.0.0
     */
    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            beanFactory.getBeanDefinition(beanName).setLazyInit(true);
        }
    }
}
