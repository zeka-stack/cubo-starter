package dev.dong4j.zeka.starter.mybatis.logger;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;

/**
 * 无日志输出的 SLF4J 日志实现类
 * <p>该类继承自 Slf4jImpl, 用于禁用所有日志输出功能. 所有日志级别 (如 debug,trace,error 等) 的方法均被重写为不执行任何操作, 适用于不需要日志输出的场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
public class NoLogOutImpl extends Slf4jImpl {
    /**
     * 构造函数, 用于初始化日志输出类名
     * <p> 该构造函数接受一个类名参数, 并传递给父类构造函数进行初始化.
     *
     * @param clazz 用于标识日志输出类的类名
     * @since 1.0.0
     */
    public NoLogOutImpl(String clazz) {
        super(clazz);
    }

    /**
     * 禁用 debug 级别日志输出
     * <p> 该方法重写自父类, 返回 false 以禁用 MyBatis 的 debug 日志输出, 减少冗余日志信息.
     *
     * @return 始终返回 false, 表示 debug 日志未启用
     * @since 1.0.0
     */
    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    /**
     * 禁用 trace 级别日志输出
     * <p> 该方法覆盖父类实现, 始终返回 false, 以禁用 MyBatis 的 trace 级别日志输出.
     *
     * @return 始终返回 false, 表示 trace 级别日志未启用
     * @since 1.0.0
     */
    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    /**
     * 禁用错误日志输出
     * <p> 覆盖父类的 error 方法, 防止输出错误日志信息, 以减少日志冗余.
     *
     * @param s 错误日志信息
     * @param e 与错误相关的异常对象
     */
    @Override
    public void error(String s, Throwable e) {
        // Do Nothing
    }

    /**
     * 禁用错误日志输出
     * <p> 覆盖父类的 error 方法, 防止输出错误日志信息.
     *
     * @param s 错误日志信息
     */
    @Override
    public void error(String s) {
        // Do Nothing
    }

    /**
     * 禁用 debug 日志输出
     * <p> 覆盖父类方法, 防止 MyBatis 输出 debug 级别的日志信息.
     *
     * @param s 要输出的日志信息
     * @since 1.0.0
     */
    @Override
    public void debug(String s) {
        // Do Nothing
    }

    /**
     * 禁用 trace 级别的日志输出
     * <p> 该方法覆盖了父类的 trace 方法, 确保不会输出 trace 级别的日志信息.
     *
     * @param s 要记录的日志信息
     */
    @Override
    public void trace(String s) {
        // Do Nothing
    }

    /**
     * 禁用 warn 级别的日志输出
     * <p> 覆盖父类方法, 防止输出 warn 级别的日志信息.
     *
     * @param s 要输出的警告信息
     */
    @Override
    public void warn(String s) {
        // Do Nothing
    }

}
