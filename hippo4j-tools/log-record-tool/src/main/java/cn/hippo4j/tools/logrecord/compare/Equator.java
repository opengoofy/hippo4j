package cn.hippo4j.tools.logrecord.compare;

import java.util.List;

/**
 * 对象比对器.
 *
 * @author chen.ma
 * @date 2021/10/24 20:27
 */
public interface Equator {

    /**
     * 判断两个对象是否相等.
     *
     * @param first
     * @param second
     * @return
     */
    boolean isEquals(Object first, Object second);

    /**
     * 获取两个对象不想等的属性.
     *
     * @param first
     * @param second
     * @return
     */
    List<FieldInfo> getDiffFields(Object first, Object second);

}
