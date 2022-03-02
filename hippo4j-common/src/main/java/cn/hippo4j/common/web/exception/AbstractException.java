package cn.hippo4j.common.web.exception;

import lombok.Getter;

/**
 * Abstract exception.
 *
 * @author chen.ma
 * @date 2022/3/2 20:01
 */
public class AbstractException extends RuntimeException {

    @Getter
    public final ErrorCode errorCode;

    public AbstractException(String message, Throwable throwable, ErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode;
    }

}
