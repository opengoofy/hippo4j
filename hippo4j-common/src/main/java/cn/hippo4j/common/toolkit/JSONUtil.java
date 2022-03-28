package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.api.JsonFacade;

import java.util.List;

/**
 * JSON util.
 *
 * @author chen.ma
 * @date 2021/12/13 20:27
 */
public class JSONUtil {

    private static final JsonFacade JSON_FACADE = new JacksonHandler();

    public static String toJSONString(Object object) {
        if (object == null) {
            return null;
        }

        return JSON_FACADE.toJSONString(object);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtil.isBlank(text)) {
            return null;
        }

        return JSON_FACADE.parseObject(text, clazz);
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtil.isBlank(text)) {
            return null;
        }

        return JSON_FACADE.parseArray(text, clazz);
    }

}
