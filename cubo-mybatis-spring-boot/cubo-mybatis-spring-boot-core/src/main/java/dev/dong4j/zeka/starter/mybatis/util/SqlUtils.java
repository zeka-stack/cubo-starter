package dev.dong4j.zeka.starter.mybatis.util;

import dev.dong4j.zeka.kernel.common.util.AesUtils;
import dev.dong4j.zeka.kernel.common.util.Base64Utils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

/**
 * MyBatis SQL工具类，提供敏感数据加密和SQL格式化功能
 * <p>
 * 该工具类专门为MyBatis框架提供了数据安全和调试支持的实用功能
 * 集成了敏感字段的加密处理能力和SQL语句的格式化美化功能，提升开发效率和数据安全性
 * <p>
 * 核心功能特性：
 * <ul>
 *     <li>敏感数据加密 - 使用AES算法对敏感字段进行安全加密</li>
 *     <li>查询条件加密 - 支持在查询条件中对敏感数据进行透明加密</li>
 *     <li>SQL格式化 - 提供SQL语句的美化和格式化输出功能</li>
 *     <li>动态密钥管理 - 支持运行时设置和更新加密密钥</li>
 * </ul>
 * <p>
 * 加密功能实现：
 * <ul>
 *     <li>基于AES对称加密算法确保数据安全性</li>
 *     <li>使用Base64编码确保加密结果的可存储性</li>
 *     <li>支持空值和无效密钥的安全处理</li>
 *     <li>提供向后兼容的废弃方法支持</li>
 * </ul>
 * <p>
 * SQL格式化功能：
 * <ul>
 *     <li>集成专业的SQL格式化器提供美化输出</li>
 *     <li>支持条件控制的格式化开关</li>
 *     <li>异常安全的格式化处理机制</li>
 *     <li>保持原始SQL的语义完整性</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 *     <li>MyBatis映射文件中的敏感字段查询条件处理</li>
 *     <li>开发和调试阶段的SQL语句格式化输出</li>
 *     <li>敏感数据的透明加密和解密流程</li>
 *     <li>数据库查询优化和性能分析</li>
 * </ul>
 * <p>
 * 技术实现特点：
 * <ul>
 *     <li>使用Lombok @UtilityClass确保工具类的不可实例化</li>
 *     <li>集成zeka-kernel核心模块的加密和编码工具</li>
 *     <li>提供JetBrains Contract注解增强方法契约描述</li>
 *     <li>线程安全的静态方法设计模式</li>
 * </ul>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.17 18:09
 * @since 1.0.0
 */
@UtilityClass
public class SqlUtils {

    /** SQL_FORMATTER */
    private static final SqlFormatter SQL_FORMATTER = new SqlFormatter();

    /** Sensitive key */
    private static String sensitiveKey;

    /**
     * Set sensitive key
     *
     * @param sensitiveKey sensitive key
     * @since 1.0.0
     */
    public void setSensitiveKey(String sensitiveKey) {
        SqlUtils.sensitiveKey = sensitiveKey;
    }

    /**
     * Get encrypt filed
     * 敏感字段做查询条件需加密后查询
     *
     * @param value value
     * @return the string
     * @since 1.0.0
     * @deprecated 请使用 {@link SqlUtils#encryptFiled(String)}
     */
    @Deprecated
    public String getEncryptFiled(String value) {
        return encryptFiled(value);
    }

    /**
     * 加密敏感数据
     *
     * @param value value
     * @return the string
     * @since 1.0.0
     */
    public String encryptFiled(String value) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(SqlUtils.sensitiveKey)) {
            return value;
        }
        byte[] encrypt = AesUtils.encrypt(value, SqlUtils.sensitiveKey);
        return Base64Utils.encodeToString(encrypt);
    }

    /**
     * 格式sql
     *
     * @param boundSql bound sql
     * @param format   format
     * @return the string
     * @since 1.0.0
     */
    @Contract("_, false -> param1")
    public static String sqlFormat(String boundSql, boolean format) {
        if (format) {
            try {
                return SQL_FORMATTER.format(boundSql);
            } catch (Exception ignored) {
            }
        }
        return boundSql;
    }

}
