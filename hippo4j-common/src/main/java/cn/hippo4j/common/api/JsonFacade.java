package cn.hippo4j.common.api;

import java.util.List;

/**
 * Json facade.
 *
 * @author chen.ma
 * @date 2021/12/13 20:01
 */
public interface JsonFacade {

    /**
     * To JSON string.
     *
     * @param object
     * @return
     */
    String toJSONString(Object object);

    /**
     * Parse object.
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T parseObject(String text, Class<T> clazz);

    /**
     * Parse array.
     *
     * @param text
     * @param clazz
     * @param <T>
     * @return
     */
    <T> List<T> parseArray(String text, Class<T> clazz);

}
