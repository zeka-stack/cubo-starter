package dev.dong4j.zeka.starter.mybatis.check;

import java.util.List;

/**
 * JDBC URL 规范化工具类
 * <p>
 * 提供对 JDBC URL 的标准化处理功能, 主要处理以 "jdbc:p6spy:" 开头的 URL, 将其转换为标准的 "jdbc:" 格式. 适用于在数据库连接配置中统一 URL 格式, 确保兼容性和一致性.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.14
 * @since 2.0.0
 */
@SuppressWarnings("PMD.UndefineMagicConstantRule")
public final class JdbcUrlNormalizer {

    /**
     * 私有构造函数, 用于防止外部实例化
     * <p>
     * 该构造函数被声明为私有, 确保此类不能被外部代码直接实例化
     */
    private JdbcUrlNormalizer() {}

    /**
     * 对原始 URL 进行标准化处理
     * <p>
     * 如果原始 URL 为 null 或空白字符串, 则返回空列表. 如果 URL 以 "jdbc:p6spy:" 开头, 则将其替换为 "jdbc:" 并返回包含新 URL 的列表. 否则返回包含原始 URL 的列表.
     *
     * @param rawUrl 需要标准化的原始 URL 字符串
     * @return 标准化后的 URL 列表, 可能包含一个或零个元素
     */
    public static List<String> normalize(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return List.of();
        }

        if (rawUrl.startsWith("jdbc:p6spy:")) {
            return List.of(rawUrl.replaceFirst("jdbc:p6spy:", "jdbc:"));
        }

        return List.of(rawUrl);
    }
}
