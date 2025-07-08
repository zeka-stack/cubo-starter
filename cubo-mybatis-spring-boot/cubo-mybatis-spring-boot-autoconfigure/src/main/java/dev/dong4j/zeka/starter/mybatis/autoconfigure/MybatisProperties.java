package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 组件配置类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:49
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = MybatisProperties.PREFIX)
public class MybatisProperties extends ZekaProperties {
    /** 组件配置前缀 */
    public static final String PREFIX = "zeka-stack.mybatis";

    /** sql 日志 */
    private boolean enableLog = false;
    /** 输出到日志的 sql 是否格式化 */
    private boolean sqlFormat = false;
    /** 超过 1000 毫秒的 sql 记录日志 */
    private Long performmaxTime = 1000L;
    /** 输出 sql 的最大长度 */
    private Integer maxLength = 1000;
    /** 附加SQL文件 */
    private boolean appendSqlFile = false;
    /** 分页默认起始页 */
    private Long page;
    /** 分页默认大小 */
    private Long limit;
    /** 单页限制 默认不限制 */
    private Long singlePageLimit = -1L;
    /** 敏感数据加密 AES_KEY */
    private String sensitiveKey = "rFsHHirtsGuST7HtBzebLge1uVYCg2ZS";
    /** sql 检查插件 */
    private boolean enableIllegalSqlInterceptor = Boolean.FALSE;
    /** SQL执行分析插件, 拦截一些整表操作 */
    private boolean enableSqlExplainInterceptor = Boolean.FALSE;
}
