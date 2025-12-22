package dev.dong4j.zeka.starter.rest.xss;

import com.google.common.collect.Maps;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.dong4j.zeka.kernel.common.util.StringPool;
import lombok.extern.slf4j.Slf4j;

/**
 * HTML 过滤工具, 用于防止 XSS(跨站脚本攻击).
 * <p>
 * 此代码采用 LGPLv3 许可.
 * <p>
 * 此代码是 Cal Hendersen 在 PHP 中的原始工作的 Java 端口.
 * http://code.iamcal.com/php/lib_filter/
 * <p>
 * 翻译中最棘手的部分是处理 PHP 和 Java 之间的正则表达式处理差异.
 * 这些资源在此过程中很有帮助:
 * <p>
 * http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html
 * http://us2.php.net/manual/en/reference.pcre.pattern.modifiers.php
 * http://www.regular-expressions.info/modifiers.html
 * <p>
 * 关于命名约定: 实例变量以“v”前缀; 全局常量全部大写.
 * <p>
 * 示例用法:
 * String input = ...
 * String clean = new HtmlFilter().filter(input);
 * <p>
 * 该类不是线程安全的. 如有疑问, 请创建一个新实例.
 * <p>
 * 如果您发现错误或有关改进 (特别是性能方面) 的建议, 请联系我们.
 * 最新版本的源代码和我们的联系方式可以在 http://xss-html-filter.sf.net 找到.
 *
 * @author Joseph O'Connell
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.11 17:11
 * @since 1.0.0
 */
@Slf4j
@SuppressWarnings(value = {"ALL"})
public final class HtmlFilter {

    /** regex flag union representing /si modifiers in PHP */
    private static final int REGEX_FLAGS_SI = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
    /** 匹配 HTML 注释的正则表达式模式 */
    private static final Pattern P_COMMENTS = Pattern.compile("<!--(.*?)-->", Pattern.DOTALL);
    /**
     * 匹配 HTML 注释内容的正则表达式模式
     * 用于识别形如 <!--...--> 的注释内容
     */
    private static final Pattern P_COMMENT = Pattern.compile("^!--(.*)--$", REGEX_FLAGS_SI);
    /**
     * 匹配 HTML 标签的正则表达式模式
     * 用于识别输入字符串中的所有 HTML 标签
     */
    private static final Pattern P_TAGS = Pattern.compile("<(.*?)>", Pattern.DOTALL);
    /**
     * 匹配结束标签的正则表达式模式, 用于识别 HTML 结束标签 (如 &lt;/div&gt;).
     * <p>
     * 该模式使用了 {@link Pattern.CASE_INSENSITIVE} 和 {@link Pattern.DOTALL} 标志,
     * 以实现不区分大小写和匹配跨行内容.
     *
     * @since 1.0.0
     */
    private static final Pattern P_END_TAG = Pattern.compile("^/([a-z0-9]+)", REGEX_FLAGS_SI);
    /**
     * 匹配 HTML 开始标签的正则表达式模式.
     * 该模式用于识别 HTML 开始标签, 如 "tag" 或 "tag attr="value"".
     * 正则表达式使用了忽略大小写和点号匹配所有字符的标志.
     *
     * @since 1.0.0
     */
    private static final Pattern P_START_TAG = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", REGEX_FLAGS_SI);
    /**
     * 匹配带引号的属性, 用于解析 HTML 标签中的属性值.
     * <p>
     * 正则表达式格式为:([a-z0-9]+)=([\"'])(.*?)\\2, 其中:
     * - ([a-z0-9]+) 匹配属性名
     * - ([\"']) 匹配引号 (双引号或单引号)
     * - (.*?) 匹配属性值
     * - \\2 匹配与第二个分组相同的引号, 确保引号闭合
     *
     * @since 1.0.0
     */
    private static final Pattern P_QUOTED_ATTRIBUTES = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", REGEX_FLAGS_SI);
    /**
     * 匹配未加引号的属性
     * <p>
     * 该正则表达式用于识别 HTML 标签中未加引号的属性, 例如:name=value
     * <p>
     * 正则表达式结构说明:
     * - ([a-z0-9]+): 匹配属性名, 由字母和数字组成
     * - (=): 匹配等号
     * - ([^\"\\s]+): 匹配属性值, 跳过双引号, 空格和反斜杠
     * <p>
     * 该模式使用了 REGEX_FLAGS_SI 标志, 表示不区分大小写且匹配所有行
     *
     * @since 1.0.0
     */
    private static final Pattern P_UNQUOTED_ATTRIBUTES = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", REGEX_FLAGS_SI);
    /**
     * 正则表达式模式, 用于匹配协议部分
     *
     * @since 1.0.0
     */
    private static final Pattern P_PROTOCOL = Pattern.compile("^([^:]+):", REGEX_FLAGS_SI);
    /**
     * 定义用于匹配 HTML 实体的正则表达式模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_ENTITY = Pattern.compile("&#(\\d+);?");
    /**
     * 表示十六进制实体的正则表达式模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_ENTITY_UNICODE = Pattern.compile("&#x([0-9a-f]+);?");
    /**
     * 编码模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_ENCODE = Pattern.compile("%([0-9a-f]{2});?");
    /**
     * 匹配有效的 HTML 实体.
     * <p>
     * 此正则表达式用于匹配有效的 HTML 实体, 实体以 '&' 开始, 并且后面跟随一个或多个非 '&' 和 ';' 的字符,
     * 直到遇到 ';' 或 '&' 或字符串结束.
     *
     * @since 1.0.0
     */
    private static final Pattern P_VALID_ENTITIES = Pattern.compile("&([^&;]*)(?=(;|&|$))");
    /**
     * 匹配有效的引号内容.
     * <p>
     * 此正则表达式用于匹配以 '>' 或字符串开头, 并以 '<' 或字符串结尾的内容.
     *
     * @since 1.0.0
     */
    private static final Pattern P_VALID_QUOTES = Pattern.compile("(>|^)([^<]+?)(<|$)", Pattern.DOTALL);
    /**
     * 匹配以 '>' 开头的字符串模式.
     *
     * @since 1.0.0
     */
    private static final Pattern P_END_ARROW = Pattern.compile("^>");
    /**
     * 匹配 HTML 标签的内容部分, 直到下一个标签或字符串结尾.
     * <p>
     * 此正则表达式用于提取 HTML 标签中的内容部分, 适用于在标签之间查找文本.
     *
     * @since 1.0.0
     */
    private static final Pattern P_BODY_TO_END = Pattern.compile("<([^>]*?)(?=<|$)");
    /**
     * 匹配 XML 内容的正则表达式模式
     * 用于识别 HTML 中的 XML 内容部分
     */
    private static final Pattern P_XML_CONTENT = Pattern.compile("(^|>)([^<]*?)(?=>)");
    /** 匹配孤立的左箭头 (即未正确闭合的 '<' 符号) */
    private static final Pattern P_STRAY_LEFT_ARROW = Pattern.compile("<([^>]*?)(?=<|$)");
    /**
     * 匹配并处理孤立的右箭头 (即 ">") 的正则表达式模式.
     * 该模式用于识别字符串中可能出现在 ">" 前的字符内容.
     */
    private static final Pattern P_STRAY_RIGHT_ARROW = Pattern.compile("(^|>)([^<]*?)(?=>)");
    /** 匹配 HTML 中的 & 符号的正则表达式模式. */
    private static final Pattern P_AMP = Pattern.compile("&");
    /**
     * 匹配小于号的正则表达式模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_QUOTE = Pattern.compile("<");
    /**
     * 匹配左尖括号 "<" 的正则表达式模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_LEFT_ARROW = Pattern.compile("<");
    /**
     * 匹配右箭头符号的正则表达式模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_RIGHT_ARROW = Pattern.compile(">");
    /**
     * 表示同时包含左右箭头的正则表达式模式
     *
     * @since 1.0.0
     */
    private static final Pattern P_BOTH_ARROWS = Pattern.compile("<>");


    /**
     * 存储用于移除成对空白标签的正则表达式模式的缓存.
     *
     * @since 1.0.0
     */
    private static final ConcurrentMap<String, Pattern> P_REMOVE_PAIR_BLANKS = new ConcurrentHashMap<>();
    /**
     * 用于存储需要移除的自闭合标签的正则表达式模式的映射
     * 键为标签名称, 值为对应的正则表达式模式
     */
    private static final ConcurrentMap<String, Pattern> P_REMOVE_SELF_BLANKS = new ConcurrentHashMap<>();

    /** 允许的 HTML 元素集合, 以及每个元素允许的属性 */
    private final Map<String, List<String>> allowed;
    /** 记录每个允许的 HTML 元素当前打开的标签数量 */
    private final Map<String, Integer> tagCounts = Maps.newHashMap();

    /** HTML 元素必须始终为自闭合元素 (例如 "<img/>") */
    private final String[] selfClosingTags;
    /** html elements which must always have separate opening and closing tags(e.g. "<b></b>") */
    private final String[] needClosingTags;
    /** 不允许的 HTML 元素集合 */
    private final String[] disallowed;
    /** 应该检查有效协议的属性 */
    private final String[] protocolAtts;
    /**
     * 允许的协议列表.
     *
     * @since 1.0.0
     */
    private final String[] allowedProtocols;
    /** 应该被移除的空内容的 HTML 标签 (例如 "<b></b>" 或 "<b/>") */
    private final String[] removeBlanks;
    /** 允许在 HTML 标记中的实体 */
    private final String[] allowedEntities;
    /** flag determining whether comments are allowed in input String. */
    private final boolean stripComment;
    /** 是否对引号进行编码 */
    private final boolean encodeQuotes;
    /**
     * 标志位, 用于确定在遇到“不平衡”的角括号时是否尝试生成标签. 如果设置为 false,
     * 不平衡的角括号将被转义为 HTML 实体.
     */
    private final boolean alwaysMakeTags;
    /**
     * V debug 标志, 用于控制是否启用调试模式. 当设置为 true 时, 会在日志中输出调试信息.
     * 默认值为 false.
     *
     * @since 1.0.0
     */
    private boolean debug = false;

    /**
     * 设置调试标志为 true. 否则使用默认设置. 请参见默认构造函数.
     *
     * @param debug 通过传入 true 参数启用调试模式
     * @since 1.0.0
     */
    public HtmlFilter(boolean debug) {
        this();
        this.debug = debug;

    }

    /**
     * 默认构造函数.
     * 初始化 HTML 过滤器的默认配置, 包括允许的 HTML 标签, 属性, 协议等.
     *
     * @since 1.0.0
     */
    public HtmlFilter() {
        this.allowed = Maps.newHashMapWithExpectedSize(6);

        List<String> aAtts = new ArrayList<>();
        aAtts.add("href");
        aAtts.add("target");
        this.allowed.put("a", aAtts);

        List<String> imgAtts = new ArrayList<>();
        imgAtts.add("src");
        imgAtts.add("width");
        imgAtts.add("height");
        imgAtts.add("alt");
        this.allowed.put("img", imgAtts);

        List<String> noAtts = new ArrayList<>();
        this.allowed.put("b", noAtts);
        this.allowed.put("strong", noAtts);
        this.allowed.put("i", noAtts);
        this.allowed.put("em", noAtts);

        this.selfClosingTags = new String[] {"img"};
        this.needClosingTags = new String[] {"a", "b", "strong", "i", "em"};
        this.disallowed = new String[] {};
        this.allowedProtocols = new String[] {"http", "mailto", "https"};
        this.protocolAtts = new String[] {"src", "href"};
        this.removeBlanks = new String[] {"a", "b", "strong", "i", "em"};
        this.allowedEntities = new String[] {"amp", "gt", "lt", "quot"};
        this.stripComment = true;
        this.encodeQuotes = true;
        this.alwaysMakeTags = true;
    }

    /**
     * Map 参数配置构造函数.
     *
     * @param conf 包含配置的映射. 键与字段名称匹配.
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public HtmlFilter(@NotNull Map<String, Object> conf) {

        assert conf.containsKey("vAllowed") : "configuration requires vAllowed";
        assert conf.containsKey("vSelfClosingTags") : "configuration requires vSelfClosingTags";
        assert conf.containsKey("vNeedClosingTags") : "configuration requires vNeedClosingTags";
        assert conf.containsKey("vDisallowed") : "configuration requires vDisallowed";
        assert conf.containsKey("vAllowedProtocols") : "configuration requires vAllowedProtocols";
        assert conf.containsKey("vProtocolAtts") : "configuration requires vProtocolAtts";
        assert conf.containsKey("vRemoveBlanks") : "configuration requires vRemoveBlanks";
        assert conf.containsKey("vAllowedEntities") : "configuration requires vAllowedEntities";

        this.allowed = Collections.unmodifiableMap((Map<String, List<String>>) conf.get("vAllowed"));
        this.selfClosingTags = (String[]) conf.get("vSelfClosingTags");
        this.needClosingTags = (String[]) conf.get("vNeedClosingTags");
        this.disallowed = (String[]) conf.get("vDisallowed");
        this.allowedProtocols = (String[]) conf.get("vAllowedProtocols");
        this.protocolAtts = (String[]) conf.get("vProtocolAtts");
        this.removeBlanks = (String[]) conf.get("vRemoveBlanks");
        this.allowedEntities = (String[]) conf.get("vAllowedEntities");
        this.stripComment = conf.containsKey("stripComment") ? (Boolean) conf.get("stripComment") : true;
        this.encodeQuotes = conf.containsKey("encodeQuotes") ? (Boolean) conf.get("encodeQuotes") : true;
        this.alwaysMakeTags = conf.containsKey("alwaysMakeTags") ? (Boolean) conf.get("alwaysMakeTags") : true;
    }

    /**
     * 将给定的十进制数转换为对应的字符.
     *
     * @param decimal 十进制数
     * @return 对应的字符
     * @since 1.0.0
     */
    @NotNull
    @Contract(pure = true)
    public static String chr(int decimal) {
        return String.valueOf((char) decimal);
    }

    /**
     * 对字符串中的特殊 HTML 字符进行转义处理.
     *
     * @param s 需要处理的字符串
     * @return 转义后的字符串
     * @since 1.0.0
     */
    public static String htmlSpecialChars(String s) {
        String result = s;
        result = regexReplace(P_AMP, "&amp;", result);
        result = regexReplace(P_QUOTE, "&quot;", result);
        result = regexReplace(P_LEFT_ARROW, "&lt;", result);
        result = regexReplace(P_RIGHT_ARROW, "&gt;", result);
        return result;
    }

    /**
     * Regex replace string
     *
     * @param regexPattern regex pattern
     * @param replacement  replacement
     * @param s            input string
     * @return the string after replacement
     * @since 1.0.0
     */
    private static String regexReplace(@NotNull Pattern regexPattern, String replacement, String s) {
        Matcher m = regexPattern.matcher(s);
        return m.replaceAll(replacement);
    }

    /**
     * 判断字符串是否存在于数组中
     *
     * @param s     要查找的字符串
     * @param array 字符串数组
     * @return 如果字符串存在于数组中返回 true, 否则返回 false
     * @since 1.0.0
     */
    @Contract(pure = true)
    private static boolean inArray(String s, @NotNull String[] array) {
        for (String item : array) {
            if (item.equals(s)) {
                return true;
            }
        }
        return false;
    }

    //---------------------------------------------------------------

    /**
     * 重置标签计数器
     *
     * @since 1.0.0
     */
    private void reset() {
        this.tagCounts.clear();
    }

    /**
     * 输出调试信息
     *
     * @param msg 调试信息
     * @since 1.0.0
     */
    private void debug(String msg) {
        if (this.debug) {
            log.info(msg);
        }
    }

    /**
     * 根据用户提交的输入字符串, 过滤掉任何无效或受限的 HTML.
     *
     * @param input 用户提交的可能包含 HTML 的文本
     * @return 过滤后的字符串, 仅包含有效的白名单中的 HTML 元素
     * @since 1.0.0
     */
    public String filter(String input) {
        this.reset();
        String s = input;

        this.debug("************************************************");
        this.debug("              INPUT: " + input);

        s = escapeComments(s);
        this.debug("     escapeComments: " + s);

        s = this.balanceHtml(s);
        this.debug("        balanceHtml: " + s);

        s = this.checkTags(s);
        this.debug("          checkTags: " + s);

        s = this.processRemoveBlanks(s);
        this.debug("processRemoveBlanks: " + s);

        s = this.validateEntities(s);
        this.debug("    validateEntites: " + s);

        this.debug("************************************************\n\n");
        return s;
    }

    /**
     * 判断是否始终生成标签
     *
     * @return 是否始终生成标签
     * @since 1.0.0
     */
    @Contract(pure = true)
    public boolean isAlwaysMakeTags() {
        return this.alwaysMakeTags;
    }

    /**
     * 获取是否剥离注释的标志
     *
     * @return 是否剥离注释的布尔值
     * @since 1.0.0
     */
    @Contract(pure = true)
    public boolean isStripComments() {
        return this.stripComment;
    }

    /**
     * 对字符串中的 HTML 注释进行转义
     *
     * @param s 需要处理的字符串
     * @return 转义后的字符串
     * @since 1.0.0
     */
    @NotNull
    private static String escapeComments(String s) {
        Matcher m = P_COMMENTS.matcher(s);
        StringBuffer buf = new StringBuffer();
        if (m.find()) {
            String match = m.group(1);
            m.appendReplacement(buf, Matcher.quoteReplacement("<!--" + htmlSpecialChars(match) + "-->"));
        }
        m.appendTail(buf);

        return buf.toString();
    }

    /**
     * 平衡 HTML 字符串
     *
     * @param s 输入字符串
     * @return 处理后的字符串
     * @since 1.0.0
     */
    private String balanceHtml(String s) {
        if (this.alwaysMakeTags) {
            //
            // try and form html
            //
            s = regexReplace(P_END_ARROW, "", s);
            s = regexReplace(P_BODY_TO_END, "<$1>", s);
            s = regexReplace(P_XML_CONTENT, "$1<$2", s);

        } else {
            //
            // escape stray brackets
            //
            s = regexReplace(P_STRAY_LEFT_ARROW, "&lt;$1", s);
            s = regexReplace(P_STRAY_RIGHT_ARROW, "$1$2&gt;<", s);

            //
            // the last regexp causes '<>' entities to appear
            // (we need to do a lookahead assertion so that the last bracket can
            // be used in the next pass of the regexp)
            //
            s = regexReplace(P_BOTH_ARROWS, "", s);
        }

        return s;
    }

    /**
     * 检查并处理 HTML 标签字符串
     *
     * @param s 需要处理的原始字符串
     * @return 处理后的字符串, 其中只包含允许的 HTML 标签
     * @since 1.0.0
     */
    @NotNull
    private String checkTags(String s) {
        Matcher m = P_TAGS.matcher(s);

        StringBuffer buf = new StringBuffer();
        while (m.find()) {
            String replaceStr = m.group(1);
            replaceStr = this.processTag(replaceStr);
            m.appendReplacement(buf, Matcher.quoteReplacement(replaceStr));
        }
        m.appendTail(buf);

        s = buf.toString();

        // these get tallied in processTag
        // (remember to reset before subsequent calls to filter method)
        StringBuilder sb = new StringBuilder(s);

        for (String key : this.tagCounts.keySet()) {
            for (int ii = 0; ii < this.tagCounts.get(key); ii++) {
                sb.append("</").append(key).append(">");
            }
        }

        return sb.toString();
    }

    /**
     * 处理空白标签字符串
     *
     * @param s 输入字符串
     * @return 处理后的字符串, 移除了指定标签的空白内容
     * @since 1.0.0
     */
    private String processRemoveBlanks(String s) {
        String result = s;
        for (String tag : this.removeBlanks) {
            if (!P_REMOVE_PAIR_BLANKS.containsKey(tag)) {
                P_REMOVE_PAIR_BLANKS.putIfAbsent(tag, Pattern.compile("<" + tag + "(\\s[^>]*)?></" + tag + ">"));
            }
            result = regexReplace(P_REMOVE_PAIR_BLANKS.get(tag), "", result);
            if (!P_REMOVE_SELF_BLANKS.containsKey(tag)) {
                P_REMOVE_SELF_BLANKS.putIfAbsent(tag, Pattern.compile("<" + tag + "(\\s[^>]*)?/>"));
            }
            result = regexReplace(P_REMOVE_SELF_BLANKS.get(tag), "", result);
        }

        return result;
    }

    /**
     * 处理标签字符串
     *
     * @param s 需要处理的原始字符串
     * @return 处理后的字符串
     * @since 1.0.0
     */
    @NotNull
    @SuppressWarnings("checkstyle:ReturnCount")
    private String processTag(String s) {
        Matcher m = P_END_TAG.matcher(s);
        if (m.find()) {
            String name = m.group(1).toLowerCase();
            if (this.allowed(name)) {
                if (!inArray(name, this.selfClosingTags)) {
                    if (this.tagCounts.containsKey(name)) {
                        this.tagCounts.put(name, this.tagCounts.get(name) - 1);
                        return "</" + name + ">";
                    }
                }
            }
        }
        m = P_START_TAG.matcher(s);
        if (m.find()) {
            String name = m.group(1).toLowerCase();
            String body = m.group(2);
            String ending = m.group(3);
            if (this.allowed(name)) {
                StringBuilder params = new StringBuilder();
                Matcher m2 = P_QUOTED_ATTRIBUTES.matcher(body);
                Matcher m3 = P_UNQUOTED_ATTRIBUTES.matcher(body);
                List<String> paramNames = new ArrayList<>();
                List<String> paramValues = new ArrayList<>();
                while (m2.find()) {
                    paramNames.add(m2.group(1));
                    paramValues.add(m2.group(3));
                }
                while (m3.find()) {
                    paramNames.add(m3.group(1));
                    paramValues.add(m3.group(3));
                }
                String paramName, paramValue;
                for (int ii = 0; ii < paramNames.size(); ii++) {
                    paramName = paramNames.get(ii).toLowerCase();
                    paramValue = paramValues.get(ii);
                    if (this.allowedAttribute(name, paramName)) {
                        if (inArray(paramName, this.protocolAtts)) {
                            paramValue = this.processParamProtocol(paramValue);
                        }
                        params.append(" ").append(paramName).append("=\"").append(paramValue).append("\"");
                    }
                }
                if (inArray(name, this.selfClosingTags)) {
                    ending = " /";
                }
                if (inArray(name, this.needClosingTags)) {
                    ending = "";
                }
                if (ending == null || ending.length() < 1) {
                    if (this.tagCounts.containsKey(name)) {
                        this.tagCounts.put(name, this.tagCounts.get(name) + 1);
                    } else {
                        this.tagCounts.put(name, 1);
                    }
                } else {
                    ending = " /";
                }
                return "<" + name + params + ending + ">";
            } else {
                return "";
            }
        }
        m = P_COMMENT.matcher(s);
        if (!this.stripComment && m.find()) {
            return "<" + m.group() + ">";
        }
        return "";
    }

    /**
     * Process param protocol string
     *
     * @param s the input string to process
     * @return the processed string with protocol replaced if necessary
     * @since 1.0.0
     */
    private String processParamProtocol(String s) {
        s = this.decodeEntities(s);
        Matcher m = P_PROTOCOL.matcher(s);
        if (m.find()) {
            String protocol = m.group(1);
            if (!inArray(protocol, this.allowedProtocols)) {
                // bad protocol, turn into local anchor link instead
                s = "#" + s.substring(protocol.length() + 1);
                if (s.startsWith(StringPool.DOUBLE_SLASH)) {
                    s = "#" + s.substring(3);
                }
            }
        }

        return s;
    }

    /**
     * 解码实体字符串
     *
     * @param s 要解码的字符串
     * @return 解码后的字符串
     * @since 1.0.0
     */
    private String decodeEntities(String s) {
        StringBuffer buf = new StringBuffer();

        Matcher m = P_ENTITY.matcher(s);
        while (m.find()) {
            String match = m.group(1);
            int decimal = Integer.decode(match);
            m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal)));
        }
        m.appendTail(buf);
        s = buf.toString();

        buf = new StringBuffer();
        m = P_ENTITY_UNICODE.matcher(s);

        s = this.getString(buf, m);

        buf = new StringBuffer();
        m = P_ENCODE.matcher(s);

        s = this.getString(buf, m);

        s = this.validateEntities(s);
        return s;
    }

    /**
     * 从匹配结果中获取处理后的字符串
     *
     * @param buf 用于存储处理结果的缓冲区
     * @param m   正则表达式匹配器
     * @return 处理后的字符串
     * @since 1.0.0
     */
    @NotNull
    private String getString(StringBuffer buf, @NotNull Matcher m) {
        String s;
        while (m.find()) {
            String match = m.group(1);
            int decimal = Integer.valueOf(match, 16);
            m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal)));
        }
        m.appendTail(buf);
        s = buf.toString();
        return s;
    }

    /**
     * Validate entities string
     *
     * @param s the input string to process
     * @return the string with validated entities
     * @since 1.0.0
     */
    private String validateEntities(String s) {
        StringBuffer buf = new StringBuffer();

        // validate entities throughout the string
        Matcher m = P_VALID_ENTITIES.matcher(s);
        while (m.find()) {
            String one = m.group(1);
            String two = m.group(2);
            m.appendReplacement(buf, Matcher.quoteReplacement(this.checkEntity(one, two)));
        }
        m.appendTail(buf);

        return this.encodeQuotes(buf.toString());
    }

    /**
     * 对引号进行编码处理
     *
     * @param s 需要处理的字符串
     * @return 编码后的字符串
     * @since 1.0.0
     */
    private String encodeQuotes(String s) {
        if (this.encodeQuotes) {
            StringBuffer buf = new StringBuffer();
            Matcher m = P_VALID_QUOTES.matcher(s);
            while (m.find()) {
                String one = m.group(1);
                String two = m.group(2);
                String three = m.group(3);
                m.appendReplacement(buf, Matcher.quoteReplacement(one + regexReplace(P_QUOTE, "&quot;", two) + three));
            }
            m.appendTail(buf);
            return buf.toString();
        } else {
            return s;
        }
    }

    /**
     * 检查实体字符串
     *
     * @param preamble 实体前缀
     * @param term     实体后缀
     * @return 处理后的字符串
     * @since 1.0.0
     */
    @NotNull
    private String checkEntity(String preamble, String term) {

        return ";".equals(term)
               && this.isValidEntity(preamble)
               ? '&' + preamble
               : "&amp;" + preamble;
    }

    /**
     * 判断实体是否有效
     *
     * @param entity 实体字符串
     * @return 如果实体在允许的实体列表中, 则返回 true, 否则返回 false
     * @since 1.0.0
     */
    @Contract(pure = true)
    private boolean isValidEntity(String entity) {
        return inArray(entity, this.allowedEntities);
    }

    /**
     * 判断给定的 HTML 元素名称是否允许
     *
     * @param name HTML 元素名称
     * @return 是否允许该元素
     * @since 1.0.0
     */
    private boolean allowed(String name) {
        return (this.allowed.isEmpty() || this.allowed.containsKey(name)) && !inArray(name, this.disallowed);
    }

    /**
     * 判断指定 HTML 元素是否允许指定属性
     *
     * @param name      HTML 元素名称
     * @param paramName 属性名称
     * @return 如果允许该属性则返回 true, 否则返回 false
     * @since 1.0.0
     */
    private boolean allowedAttribute(String name, String paramName) {
        return this.allowed(name) && (this.allowed.isEmpty() || this.allowed.get(name).contains(paramName));
    }
}
