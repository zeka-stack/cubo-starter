package dev.dong4j.zeka.starter.mybatis.dict;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.kernel.common.util.ObjectUtils;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 16:04
 * @since 2024.2.0
 */
@SuppressWarnings("all")
public class DataBindUtil {
    /** Field pro maps */
    private static final Map<Class<?>, List<FieldProperty>> FIELD_PRO_MAPS;
    /** Invalid class */
    private static final Set<Class<?>> INVALID_CLASS;

    static {
        // 缓存class和属性，不用每次都便利查找
        FIELD_PRO_MAPS = new ConcurrentHashMap<>();
        // 不合法的class，有些class天生就不可能有我们自定义的注解，例如HashMap
        // 如果第一个便利class属性之后发现没有自定义注解，也会被标记为不合法的class
        INVALID_CLASS = new CopyOnWriteArraySet<>();
        // 不校验HashMap
        INVALID_CLASS.add(HashMap.class);
    }


    /**
     * 是否需要翻译，通过寻找class上的自定义注解
     *
     * @param configuration configuration
     * @param o             o
     * @param biConsumer    bi consumer
     * @return the boolean
     * @since 2024.2.0
     */
    public static boolean needTranslate(Configuration configuration, Object o, BiConsumer<MetaObject, FieldProperty> biConsumer) {
        // 根据对象的
        List<FieldProperty> fieldPropertyList = getFieldPropertyList(o.getClass());
        if (CollectionUtils.isNotEmpty(fieldPropertyList)) {
            // 如果不为空的话，调用BiConsumer的apply了
            // 创建元数据对象（为什么要花很大功夫得到mybatis的Configuration？自己写反射不也可以完成吗？因为mybatis可能还有很多其他配置，
            // 自己可能写会丢失那些功能，这些配置都在Configuration里了，newMetaObject也会有缓存在其中）
            MetaObject metaObject = configuration.newMetaObject(o);
            // 多线程处理，fork join
            fieldPropertyList.parallelStream().forEach(fieldProperty -> {
                biConsumer.accept(metaObject, fieldProperty);
            });
            return true;
        }
        return false;
    }

    /**
     * 找翻译注解
     *
     * @param c c
     * @return the field property list
     * @since 2024.2.0
     */
    private static List<FieldProperty> getFieldPropertyList(Class<?> c) {
        if (INVALID_CLASS.contains(c)) {
            // 检查是否合法
            return null;
        }

        // 缓存检查
        List<FieldProperty> fieldProperties = FIELD_PRO_MAPS.get(c);
        if (fieldProperties != null) {
            return fieldProperties;
        }

        // 获取到所有的属性
        List<Field> allField = getAllField(c);
        // 过滤出有FieldBind的属性，并且封装成FieldProperty
        List<FieldProperty> collect = allField.stream().filter(i -> {
            FieldBind annotation = i.getAnnotation(FieldBind.class);
            return annotation != null;
        }).map(i -> new FieldProperty(i.getName(), i.getAnnotation(FieldBind.class))).collect(Collectors.toList());
        // 空的话，不合法
        if (ObjectUtils.isEmpty(collect)) {
            INVALID_CLASS.add(c);
            // 不为空，存缓存
        } else {
            FIELD_PRO_MAPS.put(c, collect);
        }
        return collect;
    }

    /**
     * 找到所有field（这里没有处理父类的属性，可以自行修改）
     *
     * @param c c
     * @return all field
     * @since 2024.2.0
     */
    private static List<Field> getAllField(Class<?> c) {
        Field[] declaredFields = c.getDeclaredFields();
        return Arrays.stream(declaredFields).filter((var0x) -> {
            // 去除static属性
            return !Modifier.isStatic(var0x.getModifiers());
        }).filter((var0x) -> {
            // 去除transient属性
            return !Modifier.isTransient(var0x.getModifiers());
        }).collect(Collectors.toList());
    }
}

