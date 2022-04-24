package cn.hippo4j.auth.toolkit;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ReturnT.
 *
 * @author chen.ma
 * @date 2021/11/10 00:00
 */
@Data
@NoArgsConstructor
public class ReturnT<T> implements Serializable {

    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;

    public static final ReturnT<String> SUCCESS = new ReturnT<>(null);
    public static final ReturnT<String> FAIL = new ReturnT<>(FAIL_CODE, null);

    private int code;
    private String msg;
    private T content;

    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnT(T content) {
        this.code = SUCCESS_CODE;
        this.content = content;
    }

}
