package cn.hippo4j.common.enums;

import java.util.Objects;

/**
 * Enable enum.
 *
 * @author chen.ma
 * @date 2021/12/21 20:34
 */
public enum EnableEnum {

    /**
     * True.
     */
    YES("1"),

    /**
     * False.
     */
    NO("0");

    private final String statusCode;

    EnableEnum(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCode() {
        return this.statusCode;
    }

    public Integer getIntCode() {
        return Integer.parseInt(this.statusCode);
    }

    @Override
    public String toString() {
        return statusCode;
    }

    public static boolean getBool(Integer intStatusCode) {
        return Objects.equals(intStatusCode, EnableEnum.YES.getIntCode()) ? true : false;
    }

}
