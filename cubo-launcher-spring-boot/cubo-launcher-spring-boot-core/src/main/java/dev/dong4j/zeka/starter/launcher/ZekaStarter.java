package dev.dong4j.zeka.starter.launcher;

import cn.hutool.core.util.ClassLoaderUtil;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.exception.StarterException;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.starter.launcher.annotation.RunningType;
import dev.dong4j.zeka.starter.launcher.enums.ApplicationType;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Spring Boot 应用启动器基类，负责应用的初始化和生命周期管理
 *
 * 该类在 Spring Boot 启动之前初始化，提供了应用启动的标准流程和扩展点。
 * 设计为抽象类，必须通过子类继承并运行，不能直接实例化。
 *
 * 主要功能：
 * 1. 自动检测和验证启动类
 * 2. 处理应用类型和运行模式
 * 3. 提供应用生命周期钩子（before、run、after）
 * 4. 支持服务型应用的守护线程管理
 *
 * 使用方式：
 * ```java
 * @SpringBootApplication
 * public class MyApplication extends ZekaStarter {
 *     // 不需要编写 main 方法，继承即可
 * }
 * ```
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.27 01:18
 * @since 1.0.0
 */
@Slf4j
public abstract class ZekaStarter implements CommandLineRunner {
    /** applicationClassName */
    private static String applicationClassName;
    /** applicationClass */
    private static Class<?> applicationClass;
    /** started */
    private static boolean started = false;
    /** SPRING_BOOT_APPLICATION */
    private static final String SPRING_BOOT_APPLICATION = "org.springframework.boot.autoconfigure.SpringBootApplication";
    /** ENABLE_AUTOCONFIGURATION */
    private static final String ENABLE_AUTOCONFIGURATION = "org.springframework.boot.autoconfigure.EnableAutoConfiguration";
    /** LOCK */
    private static final ReentrantLock LOCK = new ReentrantLock();
    /** STOP */
    private static final Condition STOP = LOCK.newCondition();
    /** applicationType */
    private static ApplicationType applicationType = ApplicationType.deduceFromClasspath();
    /** START_CLASS_ARGS */
    public static final String START_CLASS_ARGS = "--start.class=";

    /**
     * 从命令行参数中解析启动类名
     *
     * 此方法主要用于处理使用 shell 脚本启动时的增强处理，
     * 通过 --start.class=xxx 参数指定启动类。
     *
     * @param args 命令行参数数组
     * @since 1.0.0
     */
    private static void processorArgs(String @NotNull [] args) {
        for (String arg : args) {
            if (arg.startsWith(START_CLASS_ARGS)) {
                applicationClassName = arg.substring(START_CLASS_ARGS.length());
                break;
            }
        }
    }

    /**
     * 检查并验证启动类
     *
     * 确保应用中只存在一个被 @SpringBootApplication 标识的主类。
     * 如果未通过参数指定启动类，则会自动扫描项目中的 ZekaStarter 子类作为启动类。
     *
     * @since 1.0.0
     */
    private static void check() {
        if (StringUtils.isBlank(applicationClassName)) {
            ConfigurationBuilder build = ConfigurationBuilder.build(ConfigDefaultValue.BASE_PACKAGES,
                new SubTypesScanner(false));
            build.setExpandSuperTypes(false);
            Reflections reflections = new Reflections(build);

            Set<Class<? extends ZekaStarter>> subTypesOf = reflections.getSubTypesOf(ZekaStarter.class);
            if (CollectionUtils.isEmpty(subTypesOf)) {
                throw new IllegalStateException("""
                    错误原因: 没有找到 ZekaStarter 的子类: 不能直接通过 ZekaStarter.main() 启动, 必须通过子类启动, 写法如下:

                    @SpringBootApplication
                    public class DemoApplication extends ZekaStarter {
                        // 不需要写 main()
                    }""");
            }

            if (subTypesOf.size() > 1) {
                throw new IllegalStateException("一个应用只允许存在一个启动类!");
            }
            applicationClassName = subTypesOf.stream().findFirst().orElseThrow(RuntimeException::new).getName();
        }

        verificationStartClass(applicationClassName);
    }

    /**
     * 验证启动类是否符合要求
     *
     * 检查指定的启动类是否使用了 @SpringBootApplication 或 @EnableAutoConfiguration 注解，
     * 确保它是一个有效的 Spring Boot 应用启动类。
     *
     * @param startClassName 启动类的全限定名
     * @since 1.0.0
     */
    private static void verificationStartClass(String startClassName) {
        try {
            Class<?> mainClass = ClassLoaderUtil.getClassLoader().loadClass(startClassName);
            boolean matched = Arrays.stream(mainClass.getAnnotations())
                .anyMatch(m -> m.annotationType().getName()
                    .matches(SPRING_BOOT_APPLICATION) ||
                    m.annotationType().getName()
                        .matches(ENABLE_AUTOCONFIGURATION));
            if (!matched) {
                throw new StarterException("启动类必须使用 @SpringBootApplication 或者 @EnableAutoConfiguration 注解");
            }
            applicationClass = mainClass;
        } catch (Exception e) {
            throw new StarterException(e.getMessage());
        }
    }

    /**
     * 确定应用的运行类型
     *
     * 通过检查启动类上的 @RunningType 注解来确定应用类型，
     * 如果未指定，则使用自动推断的类型。
     *
     * @since 1.0.0
     */
    private static void processApplicationType() {
        RunningType runningType = AnnotationUtils.findAnnotation(applicationClass, RunningType.class);
        if (runningType != null) {
            applicationType = runningType.value();
        }
        processRunningType();
    }

    /**
     * 处理应用运行类型的兼容性检查
     *
     * 验证指定的应用类型与实际依赖是否匹配，防止类型配置错误。
     * 例如：设置为 WEB 应用但缺少 WEB 依赖时会抛出异常。
     *
     * @since 1.0.0
     */
    private static void processRunningType() {
        ApplicationType automaticDeterminationType = ApplicationType.deduceFromClasspath();
        switch (applicationType) {
            case NONE:
                if (automaticDeterminationType == ApplicationType.SERVLET
                    || automaticDeterminationType == ApplicationType.REACTIVE) {
                    log.warn("当前应用已设置为非 WEB 应用, 但是存在 WEB 相关依赖, 请删除以减少部署包体积.");
                }
                break;
            case SERVICE:
                if (automaticDeterminationType == ApplicationType.SERVLET
                    || automaticDeterminationType == ApplicationType.REACTIVE) {
                    log.warn("当前应用已设置非 WEB 应用, 但是存在 WEB 相关依赖, 请删除以减少部署包体积.");
                }
                break;
            case SERVLET:
                if (automaticDeterminationType == ApplicationType.NONE) {
                    throw new StarterException("当前应用已设置 WEB 应用, 但是不存在 WEB(Servlet) 相关依赖.");
                }
                break;
            case REACTIVE:
                if (automaticDeterminationType == ApplicationType.NONE) {
                    throw new StarterException("当前应用已设置 WEB 应用, 但是不存在 WEB(Web.flux) 相关依赖.");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 添加应用关闭钩子，处理优雅停机
     *
     * 如果应用类型是 {@link ApplicationType#SERVICE}，则启动一个守护线程防止主线程退出。
     * 当收到关闭信号时，会停止 Spring 上下文并释放锁，允许应用正常退出。
     *
     * @param applicationContext Spring 应用上下文
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:Regexp")
    private static void addHook(ConfigurableApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                applicationContext.stop();
            } catch (Exception e) {
                log.error("ZekaStarter stop exception ", e);
            }

            LOCK.lock();
            try {
                STOP.signal();
            } finally {
                LOCK.unlock();
            }
        }, "Shutdown-Hook"));

        // 主线程阻塞等待, 守护线程释放锁后退出
        LOCK.lock();
        try {
            STOP.await();
        } catch (InterruptedException e) {
            log.warn("service stopped, interrupted by other thread!", e);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 启动应用并执行生命周期回调
     *
     * 完成应用启动流程，并按顺序执行：
     * 1. 启动 Spring 上下文
     * 2. 发布自定义事件
     * 3. 调用所有 ZekaStarter 实例的 after 方法
     *
     * @param args 命令行参数
     * @return 配置好的 Spring 应用上下文
     * @throws Exception 启动过程中可能抛出的异常
     * @since 1.0.0
     */
    private ConfigurableApplicationContext start(String[] args) throws Exception {
        ConfigurableApplicationContext context = runing(args);
        this.publishEvent(context);

        this.callRunners(context);
        return context;
    }

    /**
     * 在应用启动完成后调用所有 ZekaStarter 实例的 after 方法
     *
     * 查找上下文中所有 ZekaStarter 类型的 Bean，并执行它们的 after 方法，
     * 用于在应用完全启动后执行自定义逻辑。
     *
     * @param context Spring 应用上下文
     * @since 1.0.0
     */
    private void callRunners(@NotNull ApplicationContext context) {
        Collection<ZekaStarter> values = context.getBeansOfType(ZekaStarter.class).values();
        values.forEach(ZekaStarter::after);
    }

    /**
     * 内部使用的运行方法
     *
     * 调用 ZekaApplication.run 方法启动应用，
     * 使用已确定的应用类和参数。
     *
     * @param args 命令行参数
     * @return 配置好的 Spring 应用上下文
     * @throws Exception 启动过程中可能抛出的异常
     * @since 1.0.0
     */
    private static ConfigurableApplicationContext runing(String[] args) throws Exception {
        return run(applicationClass, args);
    }

    /**
     * 在 Spring 容器启动之前执行自定义逻辑
     *
     * 子类可以重写此方法，在 Spring 容器初始化前执行准备工作，
     * 如环境检查、资源预加载等。
     *
     * @since 1.0.0
     */
    protected void before() {

    }

    /**
     * 在容器刷新完成后执行逻辑
     *
     * 实现 {@link CommandLineRunner} 接口的 run 方法，
     * 在 Spring 容器完全初始化后但在 after() 方法之前执行。
     * 子类可重写此方法添加自定义启动逻辑。
     *
     * @param args 命令行参数
     * @since 1.0.0
     */
    @Override
    public void run(String... args) {

    }

    /**
     * 在启动完成后且在 {@link ZekaStarter#after()} 之前发送事件
     *
     * 子类可重写此方法，在应用启动完成后发布自定义事件，
     * 用于通知其他组件应用已启动。
     *
     * @param context Spring 应用上下文
     * @since 1.0.0
     */
    protected void publishEvent(ConfigurableApplicationContext context) {

    }

    /**
     * 在 Spring 容器启动完成后执行自定义逻辑
     *
     * 应用完全启动后的最终回调方法，子类可重写此方法执行
     * 需要在应用完全就绪后才能执行的操作。
     *
     * @since 1.0.0
     */
    protected void after() {

    }

    /**
     * 运行应用并返回上下文，主要用于单元测试
     *
     * 提供一个静态方法用于在单元测试中启动应用作为内嵌服务，
     * 直接调用 ZekaApplication.run 方法。
     *
     * @param source 包含 @SpringBootApplication 注解的主类
     * @param args   命令行参数
     * @return 配置好的 Spring 应用上下文
     * @since 1.0.0
     */
    public static ConfigurableApplicationContext run(Class<?> source, String... args) {
        return ZekaApplication.run(source, applicationType, args);
    }

    /**
     * 应用启动入口方法
     *
     * 主要启动流程：
     * 1. 设置日志系统属性
     * 2. 处理启动参数
     * 3. 检查启动类
     * 4. 处理应用类型
     * 5. 实例化启动类并调用生命周期方法
     * 6. 对于服务型应用，添加守护线程防止退出
     *
     * @param args 命令行参数
     * @see AbstractApplicationContext#registerShutdownHook()
     * @since 1.0.0
     */
    @SuppressWarnings("checkstyle:UncommentedMain")
    public static void main(String[] args) {
        System.setProperty("log4j2.isThreadContextMapInheritable", Boolean.TRUE.toString());
        try {
            if (!started) {
                // 处理启动参数
                processorArgs(args);
                // 启动类检查
                check();
                // 处理应用类型
                processApplicationType();
                // 反射实例化子类来启动 Spring Boot
                Constructor<?> constructor = applicationClass.getConstructor();
                ZekaStarter starter = (ZekaStarter) constructor.newInstance();

                starter.before();
                ConfigurableApplicationContext configurableApplicationContext;
                configurableApplicationContext = starter.start(args);

                started = true;

                if (applicationType == ApplicationType.SERVICE) {
                    addHook(configurableApplicationContext);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("启动失败: ", e);
        }
    }
}
