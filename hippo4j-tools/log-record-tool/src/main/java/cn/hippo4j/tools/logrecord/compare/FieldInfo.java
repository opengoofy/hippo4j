package cn.hippo4j.tools.logrecord.compare;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 对象比较中不同的字段.
 *
 * @author chen.ma
 * @date 2021/10/24 20:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldInfo {

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 第一个字段的类型
     */
    private Class<?> firstFieldType;

    /**
     * 第二个字段的类型
     */
    private Class<?> secondFieldType;

    /**
     * 第一个对象的值
     */
    private Object firstVal;

    /**
     * 第二个对象的值
     */
    private Object secondVal;

    public FieldInfo(String fieldName, Class<?> firstFieldType, Class<?> secondFieldType) {
        this.fieldName = fieldName;
        this.firstFieldType = firstFieldType;
        this.secondFieldType = secondFieldType;
    }

    public FieldInfo(String fieldName, Class<?> fieldType, Object firstVal, Object secondVal) {
        this.fieldName = fieldName;
        this.firstFieldType = fieldType;
        this.secondFieldType = fieldType;
        this.firstVal = firstVal;
        this.secondVal = secondVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(fieldName, fieldInfo.fieldName) &&
                Objects.equals(firstFieldType, fieldInfo.firstFieldType) &&
                Objects.equals(secondFieldType, fieldInfo.secondFieldType) &&
                Objects.equals(firstVal, fieldInfo.firstVal) &&
                Objects.equals(secondVal, fieldInfo.secondVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, firstFieldType, secondFieldType, firstVal, secondVal);
    }


}
