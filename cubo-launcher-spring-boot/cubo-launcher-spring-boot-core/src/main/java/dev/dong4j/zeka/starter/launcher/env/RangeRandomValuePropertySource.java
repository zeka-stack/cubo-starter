package dev.dong4j.zeka.starter.launcher.env;

import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.exception.PropertiesException;
import dev.dong4j.zeka.kernel.common.util.EnumUtils;
import dev.dong4j.zeka.kernel.common.util.NetUtils;
import dev.dong4j.zeka.kernel.common.util.NumberUtils;
import dev.dong4j.zeka.kernel.common.util.RandomUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 扩展的随机值属性源，提供更灵活的随机值生成功能
 *
 * 该类扩展了 Spring Boot 的 RandomValuePropertySource，增加了以下功能：
 * 1. 支持范围随机整数：${range.random.int(min,max)}
 * 2. 支持自定义长度随机字符串：${range.random.key(length)}
 * 3. 自动处理端口冲突，在指定范围内查找可用端口
 *
 * 主要应用场景：
 * 1. 随机端口分配，自动处理端口冲突
 * 2. 生成随机密钥或令牌
 * 3. 需要随机值的配置项
 *
 * 与原生 ${random.*} 的区别：
 * 1. 支持范围限制的随机整数
 * 2. 支持自定义长度的随机字符串（包含大小写字母和数字）
 * 3. 自动处理端口冲突
 *
 * @author dongj4
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.23 14:21
 * @see RandomValuePropertySource
 * @since 1.0.0
 */
@Slf4j
public final class RangeRandomValuePropertySource extends RandomValuePropertySource {

    /** RANGE_RANDOM_PROPERTY_SOURCE_NAME */
    private static final String RANGE_RANDOM_PROPERTY_SOURCE_NAME = "range.random";
    /** PREFIX */
    private static final String PREFIX = "range.random.";
    /** 生成长度小于字符串的随机数字从而生成随机字符串 */
    private static final String CONSTANT_STRING = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /** DEFAULT_START */
    private static final Integer DEFAULT_START = 8080;
    /** DEFAULT_END */
    private static final Integer DEFAULT_END = 18080;

    /**
     * 范围类型枚举，定义支持的随机值类型
     *
     * 1. INT - 整数范围随机值
     * 2. KEY - 随机字符串
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.31 17:00
     * @since 1.0.0
     */
    public enum RangeType {
        /** Int range type */
        INT,
        /** Key range type */
        KEY,
    }

    /**
     * Range random port value property source
     *
     * @since 1.0.0
     */
    public RangeRandomValuePropertySource() {
        super(RANGE_RANDOM_PROPERTY_SOURCE_NAME);
    }

    /**
     * 获取属性值
     *
     * @param name 属性名称，支持以下格式：
     *             1. range.random.int - 默认范围随机端口
     *             2. range.random.int(min,max) - 指定范围随机整数
     *             3. range.random.key - 默认长度随机字符串
     *             4. range.random.key(length) - 指定长度随机字符串
     * @return 生成的随机值，如果名称不匹配则返回 null
     * @since 1.0.0
     */
    @Override
    public Object getProperty(@NotNull String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        } else {
            return this.getRangeRandomValue(StringUtils.trimAllWhitespace(name.substring(PREFIX.length())));
        }
    }

    /**
     * 获取范围随机值
     *
     * @param type 随机值类型，支持：
     *             1. int - 整数随机值
     *             2. key - 字符串随机值
     * @return 生成的随机值
     * @throws PropertiesException 如果类型不支持或参数格式错误
     * @since 1.0.0
     */
    private Object getRangeRandomValue(@NotNull String type) {
        RangeType rangeType = EnumUtils.of(RangeType.class, e -> type.toUpperCase().contains(e.name()))
            .orElseThrow(() -> new PropertiesException("目前只支持 range.random.int, range.random.int(min, max), "
                + "range.random.key 和 range.random.key(size)"));

        String range = this.getRange(type, rangeType.name().toLowerCase());

        if (rangeType == RangeType.INT) {
            return this.getIntValueInRange(range);
        } else if (rangeType == RangeType.KEY) {
            return this.getStringInSize(range);
        } else {
            throw new PropertiesException("不支持的 type: [{}]", type);
        }
    }

    /**
     * 从类型字符串中提取范围参数
     *
     * @param type   完整的类型字符串
     * @param prefix 类型前缀（int 或 key）
     * @return 范围参数字符串，如果没有指定范围则返回 null
     * @since 1.0.0
     */
    @Nullable
    private String getRange(@NotNull String type, String prefix) {
        if (type.startsWith(prefix)) {
            int startIndex = prefix.length() + 1;
            if (type.length() > startIndex) {
                return type.substring(startIndex, type.length() - 1);
            }
        }
        return null;
    }

    /**
     * 处理整数范围随机值
     *
     * @param range 范围字符串，格式为 "min,max" 或 null（使用默认范围）
     * @return 范围内的随机整数
     * @throws PropertiesException 如果范围格式无效
     * @since 1.0.0
     */
    @Nullable
    private @Unmodifiable Object getIntValueInRange(String range) {
        if (StringUtils.isBlank(range)) {
            return RangeRandomPort.nextValue(DEFAULT_START, DEFAULT_END);
        }

        if (this.checkPattern(range)) {
            String[] tokens = StringUtils.commaDelimitedListToStringArray(range);
            int start = Integer.parseInt(tokens[0]);
            int end = Integer.parseInt(tokens[1]);
            return RangeRandomPort.nextValue(start, end);
        }

        return null;
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param range 长度字符串或 null（使用默认长度）
     * @return 随机生成的字符串
     * @throws PropertiesException 如果长度参数无效
     * @since 1.0.0
     */
    @NotNull
    private @Unmodifiable Object getStringInSize(String range) {
        // 默认生成 64 位的随机字符串
        if (StringUtils.isBlank(range)) {
            return this.randomString(64);
        }
        if (NumberUtils.isNumer(range)) {
            // 指定了字符串长度, 生成指定长度字符串
            return this.randomString(NumberUtils.toInt(range));
        }

        throw new PropertiesException("不是正确的字符串长度: [{}]", range);
    }

    /**
     * 生成随机字符串
     *
     * @param length 字符串长度
     * @return 随机生成的字符串
     * @since 1.0.0
     */
    @NotNull
    private String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = this.getSource().nextInt(CONSTANT_STRING.length());
            sb.append(CONSTANT_STRING.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 检查范围格式是否有效
     *
     * @param range 范围字符串，应为 "min,max" 格式
     * @return true 如果格式有效
     * @throws PropertiesException 如果格式无效
     * @since 1.0.0
     */
    private boolean checkPattern(String range) {
        String pattern = "^(\\d+),(\\d+)$";
        if (!Pattern.matches(pattern, range)) {
            throw new PropertiesException("{} 无法解析, 请使用正确的配置", range);
        }
        return true;
    }

    /**
     * 将随机值属性源添加到环境变量中
     *
     * @param environment 可配置的环境对象
     * @since 1.0.0
     */
    public static void addToEnvironment(@NotNull ConfigurableEnvironment environment) {
        environment.getPropertySources().addLast(new RangeRandomValuePropertySource());
    }

    /**
     * 随机端口内部类，处理端口范围内的随机端口获取
     *
     * 该类实现了以下功能：
     * 1. 在指定范围内生成随机端口
     * 2. 检查端口是否可用
     * 3. 如果端口被占用，自动查找下一个可用端口
     *
     * @author dongj4
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.23 14:21
     * @since 1.0.0
     */
    @Slf4j
    private static class RangeRandomPort {
        /** START */
        private static final int START = 1024;
        /** END */
        private static final int END = 65535;

        /**
         * Next value int
         *
         * @param start start
         * @param end   end
         * @return the int
         * @since 1.0.0
         */
        static int nextValue(Integer start, Integer end) {
            int serverPort;
            start = start == null ? START : Math.max(start, START);
            end = end == null ? END : Math.min(end, END);
            if (start > end) {
                // 说明未找到满足条件的端口, 抛出异常
                throw new LowestException("在端口范围内[" + start + "," + end + "]未找到可用端口, 请修改随机端口范围");
            }
            serverPort = RandomUtils.nextInt(start, end + 1);
            // 端口没有占用, 直接返回
            if (NetUtils.available(serverPort)) {
                log.trace("成功获取可用随机端口:[{}]", serverPort);
                return serverPort;
            }
            // 初始化循环开始数
            serverPort = start;
            // 端口被占用, 则顺延 ManagementEnvironmentCustomizer 往后查找一个可用端口并返回
            while (serverPort <= end) {
                log.trace("随机端口已被占用:[{}]", serverPort);
                if (NetUtils.available(++serverPort)) {
                    log.trace("成功获取可用随机端口:[{}]", serverPort);
                    break;
                }
            }
            if (serverPort == end + 1) {
                // 说明 while 循环结束都没找到可用端口或者最后一次找到了, 但超过了end返回, 仍然不满足条件
                throw new LowestException("在端口范围内[" + start + "," + end + "]未找到可用端口, 请修改随机端口范围");
            }
            return serverPort;
        }
    }
}
