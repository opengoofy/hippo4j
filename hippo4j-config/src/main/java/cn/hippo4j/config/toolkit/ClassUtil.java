package cn.hippo4j.config.toolkit;

import java.util.Objects;

/**
 * Class util.
 *
 * @author chen.ma
 * @date 2021/6/23 19:03
 */
public class ClassUtil {

    public static boolean isAssignableFrom(Class clazz, Class cls) {
        Objects.requireNonNull(cls, "cls");
        return clazz.isAssignableFrom(cls);
    }

    public static String getCanonicalName(Class cls) {
        Objects.requireNonNull(cls, "cls");
        return cls.getCanonicalName();
    }

}
