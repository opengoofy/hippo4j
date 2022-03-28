package cn.hippo4j.config.toolkit;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Map util.
 *
 * @author chen.ma
 * @date 2021/6/23 19:09
 */
public class MapUtil {

    public static Object computeIfAbsent(Map target, Object key, BiFunction mappingFunction, Object param1, Object param2) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(mappingFunction, "mappingFunction");
        Objects.requireNonNull(param1, "param1");
        Objects.requireNonNull(param2, "param2");

        Object val = target.get(key);
        if (val == null) {
            Object ret = mappingFunction.apply(param1, param2);
            target.put(key, ret);
            return ret;
        }
        return val;
    }


    /**
     * 根据 Key 进行模糊匹配.
     *
     * @param sourceMap
     * @param filters
     * @return
     */
    public static List<String> parseMapForFilter(Map<String, ?> sourceMap, String filters) {
        List<String> resultList = Lists.newArrayList();
        if (CollUtil.isEmpty(sourceMap)) {
            return resultList;
        }

        sourceMap.forEach((key, val) -> {
            if (checkKey(key, filters)) {
                resultList.add(key);
            }
        });

        return resultList;
    }

    /**
     * 匹配想要查询的字符.
     *
     * @param key
     * @param filters
     * @return
     */
    private static boolean checkKey(String key, String filters) {
        if (key.indexOf(filters) > -1) {
            return true;
        } else {
            return false;
        }
    }

}
