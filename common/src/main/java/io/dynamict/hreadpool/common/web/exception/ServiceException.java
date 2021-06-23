package io.dynamict.hreadpool.common.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务系统业务逻辑相关异常
 *
 * @author chen.ma
 * @date 2021/3/19 16:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -8667394300356618037L;

    private String detail;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.detail = cause.getMessage();
    }

    public ServiceException(String message, String detail, Throwable cause) {
        super(message, cause);
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "message='" + getMessage() + "'," +
                "detail='" + getDetail() + "'" +
                '}';
    }

}