package dev.dong4j.zeka.starter.launcher.autoconfigure.extend;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

/**
 * @Autowired 注解扩展实现，全局允许依赖注入为 null
 *
 * 该类扩展了 AutowiredAnnotationBeanPostProcessor，修改了默认行为：
 * 1. 当依赖注入失败时，不再抛出异常
 * 2. 允许 @Autowired 和 @Inject 注解的字段/方法参数为 null
 * 3. 记录警告日志，便于调试
 *
 * 使用场景：
 * 1. 在开发环境中简化依赖管理
 * 2. 在可选依赖场景下避免强制注入
 *
 * 注意：生产环境应谨慎使用，可能导致 NPE 风险。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.28 01:21
 * @since 1.0.0
 */
@Slf4j
public class AutowiredAnnotationExtend extends AutowiredAnnotationBeanPostProcessor {

    /**
     * 处理属性注入，捕获异常允许注入为 null
     *
     * 重写父类方法，在依赖注入失败时：
     * 1. 捕获所有异常
     * 2. 记录警告日志
     * 3. 返回原始属性值，允许注入为 null
     *
     * @param pvs 当前属性值
     * @param bean 正在处理的 Bean 实例
     * @param beanName Bean 名称
     * @return 处理后的属性值
     * @throws BeanCreationException 如果处理过程中发生不可恢复的错误
     * @since 1.0.0
     */
    @Override
    public @NotNull PropertyValues postProcessProperties(@NotNull PropertyValues pvs,
                                                         @NotNull Object bean,
                                                         @NotNull String beanName) throws BeanCreationException {
        try {
            pvs = super.postProcessProperties(pvs, bean, beanName);
        } catch (Throwable e) {
            log.warn("[{}] is null", beanName);
        }
        return pvs;
    }
}
