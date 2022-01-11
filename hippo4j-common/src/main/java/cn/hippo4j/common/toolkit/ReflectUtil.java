package cn.hippo4j.common.toolkit;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflect util.
 *
 * @author chen.ma
 * @date 2022/1/9 13:16
 */
public class ReflectUtil {

    private static final Map<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap();

    public static Object getFieldValue(Object obj, String fieldName) {
        if (null == obj || StringUtil.isBlank(fieldName)) {
            return null;
        }

        Field field = getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName);
        return getFieldValue(obj, field);
    }

    public static Object getFieldValue(Object obj, Field field) {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            obj = null;
        }

        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            String exceptionMsg = String.format("IllegalAccess for %s.%s", field.getDeclaringClass(), field.getName());
            throw new RuntimeException(exceptionMsg, e);
        }

        return result;
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && false == accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        final Field[] fields = getFields(beanClass);
        return ArrayUtil.firstMatch((field) -> name.equals(getFieldName(field)), fields);
    }

    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        Field[] allFields = FIELDS_CACHE.get(beanClass);
        if (null != allFields) {
            return allFields;
        }

        allFields = getFieldsDirectly(beanClass, true);
        FIELDS_CACHE.put(beanClass, allFields);
        return allFields;
    }

    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        Assert.notNull(beanClass);

        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                int length = allFields.length;
                allFields = Arrays.copyOf(allFields, length + declaredFields.length);
                for (int i = 1; i < declaredFields.length; i++) {
                    allFields[length + i] = declaredFields[i - 1];
                }
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    public static String getFieldName(Field field) {
        if (null == field) {
            return null;
        }

        return field.getName();
    }

}
