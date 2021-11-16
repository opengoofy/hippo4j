package cn.hippo4j.tools.logrecord.compare;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 比对器抽象类.
 *
 * @author chen.ma
 * @date 2021/10/24 20:25
 */
public class AbstractEquator implements Equator {

    private static final List<Class<?>> WRAPPER =
            Arrays
                    .asList(
                            Byte.class,
                            Short.class,
                            Integer.class,
                            Long.class,
                            Float.class,
                            Double.class,
                            Character.class,
                            Boolean.class,
                            String.class
                    );

    private List<String> includeFields;

    private List<String> excludeFields;

    private boolean bothExistFieldOnly = true;

    public AbstractEquator() {
        includeFields = Collections.emptyList();
        excludeFields = Collections.emptyList();
    }

    /**
     * @param bothExistFieldOnly 是否仅比对两个类都包含的字段
     */
    public AbstractEquator(boolean bothExistFieldOnly) {
        includeFields = Collections.emptyList();
        excludeFields = Collections.emptyList();
        this.bothExistFieldOnly = bothExistFieldOnly;
    }

    /**
     * 指定包含或排除某些字段.
     *
     * @param includeFields 包含字段, 若为 null 或空集则不指定
     * @param excludeFields 排除字段, 若为 null 或空集则不指定
     */
    public AbstractEquator(List<String> includeFields, List<String> excludeFields) {
        this.includeFields = includeFields;
        this.excludeFields = excludeFields;
    }

    /**
     * 指定包含或排除某些字段.
     *
     * @param includeFields      包含字段, 若为 null 或空集则不指定
     * @param excludeFields      排除字段, 若为 null 或空集则不指定
     * @param bothExistFieldOnly 是否只对比两个类都包含的字段, 默认为 true
     */
    public AbstractEquator(List<String> includeFields, List<String> excludeFields, boolean bothExistFieldOnly) {
        this.includeFields = includeFields;
        this.excludeFields = excludeFields;
        this.bothExistFieldOnly = bothExistFieldOnly;
    }

    @Override
    public boolean isEquals(Object first, Object second) {
        List<FieldInfo> diff = getDiffFields(first, second);
        return diff == null || diff.isEmpty();
    }

    @Override
    public List<FieldInfo> getDiffFields(Object first, Object second) {
        return null;
    }

    /**
     * 对比两个对象的指定属性是否相等, 默认为两个对象是否 equals.
     * <p>
     * 子类可以通过覆盖此方法对某些特殊属性进行比对.
     *
     * @param fieldInfo
     * @return
     */
    protected boolean isFieldEquals(FieldInfo fieldInfo) {
        // 先判断排除, 如果需要排除, 则无论在不在包含范围, 都一律不比对
        if (isExclude(fieldInfo)) {
            return true;
        }
        // 如果有指定需要包含的字段而且当前字段不在需要包含的字段中则不比对
        if (!isInclude(fieldInfo)) {
            return true;
        }
        return nullableEquals(fieldInfo.getFirstVal(), fieldInfo.getSecondVal());
    }

    /**
     * 确定是否需要需要排除这个字段, 子类可以扩展这个方法, 自定义判断方式.
     *
     * @param fieldInfo
     * @return
     */
    protected boolean isExclude(FieldInfo fieldInfo) {
        // 如果有指定需要排除的字段，而且当前字段是需要排除字段，则直接返回 true
        return excludeFields != null && !excludeFields.isEmpty() && excludeFields.contains(fieldInfo.getFieldName());
    }

    /**
     * 确定是否需要比较这个字段, 子类可以扩展这个方法, 自定义判断方式.
     *
     * @param fieldInfo
     * @return
     */
    protected boolean isInclude(FieldInfo fieldInfo) {
        // 没有指定需要包含的字段，则全部都包含
        if (includeFields == null || includeFields.isEmpty()) {
            return true;
        }
        return includeFields.contains(fieldInfo.getFieldName());
    }

    /**
     * 如果简单数据类型的对象则直接进行比对.
     *
     * @param first
     * @param second
     * @return
     */
    protected List<FieldInfo> compareSimpleField(Object first, Object second) {
        boolean eq = Objects.equals(first, second);
        if (eq) {
            return Collections.emptyList();
        } else {
            Object obj = first == null ? second : first;
            Class<?> clazz = obj.getClass();
            // 不等的字段名称使用类的名称
            return Collections.singletonList(new FieldInfo(clazz.getSimpleName(), clazz, first, second));
        }
    }

    /**
     * 判断是否为原始数据类型.
     *
     * @param first
     * @param second
     * @return
     */
    protected boolean isSimpleField(Object first, Object second) {
        Object obj = first == null ? second : first;
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() || WRAPPER.contains(clazz);
    }

    protected boolean nullableEquals(Object first, Object second) {
        if (first instanceof Collection && second instanceof Collection) {
            // 如果两个都是集合类型，尝试转换为数组再进行深度比较
            return Objects.deepEquals(((Collection) first).toArray(), ((Collection) second).toArray());
        }
        return Objects.deepEquals(first, second);
    }

    protected Set<String> getAllFieldNames(Set<String> firstFields, Set<String> secondFields) {
        Set<String> allFields;
        // 只取交集
        if (isBothExistFieldOnly()) {
            allFields = firstFields.stream().filter(secondFields::contains).collect(Collectors.toSet());
        } else {
            // 否则取并集
            allFields = new HashSet<>(firstFields);
            allFields.addAll(secondFields);
        }

        return allFields;
    }

    public boolean isBothExistFieldOnly() {
        return bothExistFieldOnly;
    }

}
