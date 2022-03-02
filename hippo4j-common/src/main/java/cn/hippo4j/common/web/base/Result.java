package cn.hippo4j.common.web.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Result.
 *
 * @author chen.ma
 * @date 2021/3/19 16:12
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -4408341719434417427L;

    /**
     * Correct return code.
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * Return code.
     */
    private String code;

    /**
     * Message.
     */
    private String message;

    /**
     * Response data.
     */
    private T data;

    /**
     * Is success.
     *
     * @return
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

}
