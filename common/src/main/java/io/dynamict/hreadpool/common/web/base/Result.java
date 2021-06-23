package io.dynamict.hreadpool.common.web.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 全局返回对象
 *
 * @author chen.ma
 * @date 2021/3/19 16:12
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -4408341719434417427L;

    public static final String SUCCESS_CODE = "0";

    private String code;

    private String message;

    private T data;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public boolean isFail() {
        return !isSuccess();
    }
}
