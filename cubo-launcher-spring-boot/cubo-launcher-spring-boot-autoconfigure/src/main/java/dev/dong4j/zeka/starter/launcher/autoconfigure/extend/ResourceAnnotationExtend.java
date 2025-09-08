package dev.dong4j.zeka.starter.launcher.autoconfigure.extend;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

/**
 * @Resource 注解扩展实现，全局允许依赖注入为 null
 *
 * 该类扩展了 CommonAnnotationBeanPostProcessor，修改了默认行为：
 * 1. 当 @Resource 注解的依赖注入失败时，不再抛出异常
 * 2. 允许 @Resource 注解的字段/方法参数为 null
 * 3. 记录警告日志，便于调试
 *
 * 与 @Autowired 不同，@Resource 注解原生不支持 required=false 属性，
 * 此扩展为 @Resource 注解提供了类似的功能。
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
 * @date 2020.01.28 01:11
 * @see CommonAnnotationBeanPostProcessor#autowireResource
 * @since 1.0.0
 */
@Slf4j
public class ResourceAnnotationExtend extends CommonAnnotationBeanPostProcessor {

    /** serialVersionUID */
    private static final long serialVersionUID = 3463920095157475670L;

    /**
     * 重写资源自动装配逻辑，允许注入为 null
     *
     * 该方法重写了父类的资源注入逻辑：
     * 1. 捕获所有注入异常
     * 2. 记录警告日志
     * 3. 返回 null 而不是抛出异常
     *
     * 注意：这是一个全局开关，无法像 @Autowired 那样细粒度控制。
     * 主要用于开发环境和单元测试场景。
     *
     * @param factory 当前 Bean 工厂
     * @param element 查找元素
     * @param requestingBeanName 请求注入的 Bean 名称
     * @return 注入的对象，如果注入失败则返回 null
     * @throws BeansException 如果处理过程中发生不可恢复的错误
     * @since 1.0.0
     */
    @Override
    protected @NotNull Object autowireResource(BeanFactory factory,
                                               LookupElement element,
                                               String requestingBeanName) throws BeansException {
        Object obj = null;
        try {
            obj = super.autowireResource(factory, element, requestingBeanName);
        } catch (Throwable e) {
            log.warn("[{}] is null", requestingBeanName);
        }
        return obj;
    }

}
