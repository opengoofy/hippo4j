package com.github.dynamic.threadpool.config.enums;

/**
 * 状态枚举
 *
 * @author chen.ma
 * @date 2021/3/26 18:45
 */
public enum DelEnum {

    /**
     * 正常状态
     */
    NORMAL("0"),

    /**
     * 删除状态
     */
    DELETE("1");

    private final String statusCode;

    DelEnum(String statusCode) {
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

}
