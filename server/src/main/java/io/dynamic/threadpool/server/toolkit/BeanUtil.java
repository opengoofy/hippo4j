package io.dynamic.threadpool.server.toolkit;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;

import java.lang.reflect.Array;
import java.util.*;

import static com.github.dozermapper.core.loader.api.TypeMappingOptions.mapEmptyString;
import static com.github.dozermapper.core.loader.api.TypeMappingOptions.mapNull;

/**
 * 对象复制工具类
 *
 * @author chen.ma
 * @date 2021/3/19 15:40
 */
public class BeanUtil {

    private BeanUtil() {

    }

    protected static Mapper BEAN_MAPPER_BUILDER;

    static {
        BEAN_MAPPER_BUILDER = DozerBeanMapperBuilder.buildDefault();
    }

    /**
     * 复制单个对象
     *
     * @param source 数据对象
     * @param clazz  复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象
     */
    public static <T, S> T convert(S source, Class<T> clazz) {
        return Optional.ofNullable(source)
                .map(each -> BEAN_MAPPER_BUILDER.map(each, clazz))
                .orElse(null);
    }

    /**
     * 复制多个对象
     *
     * @param sources 数据对象
     * @param clazz   复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象集合
     */
    public static <T, S> List<T> convert(List<S> sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    List<T> targetList = new ArrayList<T>(each.size());
                    each.parallelStream()
                            .forEach(item -> targetList.add(BEAN_MAPPER_BUILDER.map(item, clazz)));
                    return targetList;
                })
                .orElse(null);
    }

    /**
     * 复制多个对象
     *
     * @param sources 数据对象
     * @param clazz   复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象集合
     */
    public static <T, S> Set<T> convert(Set<S> sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    Set<T> targetSize = new HashSet<T>(each.size());
                    each.parallelStream()
                            .forEach(item -> targetSize.add(BEAN_MAPPER_BUILDER.map(item, clazz)));
                    return targetSize;
                })
                .orElse(null);
    }

    /**
     * 复制多个对象
     *
     * @param sources 数据对象
     * @param clazz   复制目标类型
     * @param <T>
     * @param <S>
     * @return 转换后对象集合
     */
    public static <T, S> T[] convert(S[] sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    @SuppressWarnings("unchecked")
                    T[] targetArray = (T[]) Array.newInstance(clazz, sources.length);
                    for (int i = 0; i < targetArray.length; i++) {
                        targetArray[i] = BEAN_MAPPER_BUILDER.map(sources[i], clazz);
                    }
                    return targetArray;
                })
                .orElse(null);
    }

    /**
     * 拷贝非空且非空串属性
     *
     * @param source 数据源
     * @param target 指向源
     */
    public static void convertIgnoreNullAndBlank(Object source, Object target) {
        DozerBeanMapperBuilder dozerBeanMapperBuilder = DozerBeanMapperBuilder.create();
        Mapper mapper = dozerBeanMapperBuilder.withMappingBuilders(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(source.getClass(), target.getClass(), mapNull(false), mapEmptyString(false));
            }
        }).build();
        mapper.map(source, target);
    }

    /**
     * 拷贝非空属性
     *
     * @param source 数据源
     * @param target 指向源
     */
    public static void convertIgnoreNull(Object source, Object target) {
        DozerBeanMapperBuilder dozerBeanMapperBuilder = DozerBeanMapperBuilder.create();
        Mapper mapper = dozerBeanMapperBuilder.withMappingBuilders(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(source.getClass(), target.getClass(), mapNull(false));
            }
        }).build();
        mapper.map(source, target);
    }

}
