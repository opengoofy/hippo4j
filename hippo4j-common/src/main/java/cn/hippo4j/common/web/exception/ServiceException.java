package cn.hippo4j.common.web.exception;

import lombok.EqualsAndHashCode;

/**
 * Service exception.
 *
 * @author chen.ma
 * @date 2021/3/19 16:14
 */
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends AbstractException {

    /**
     * 无指定错误码.
     */
    public ServiceException() {
        this(ErrorCodeEnum.SERVICE_ERROR);
    }

    /**
     * 指定错误码.
     *
     * @param errorCode
     */
    public ServiceException(ErrorCode errorCode) {
        this(errorCode.getMessage(), null, errorCode);
    }

    /**
     * 指定报错信息.
     *
     * @param message
     */
    public ServiceException(String message) {
        this(message, null, ErrorCodeEnum.SERVICE_ERROR);
    }

    /**
     * 异常抛出.
     *
     * @param cause
     */
    public ServiceException(Throwable cause) {
        this(cause, cause.getMessage());
    }

    /**
     * 异常抛出并指定错误信息.
     *
     * @param message
     * @param cause
     */
    public ServiceException(String message, Throwable cause) {
        this(message, cause, ErrorCodeEnum.SERVICE_ERROR);
    }

    /**
     * 异常抛出并指定错误信息.
     *
     * @param cause
     * @param message
     */
    public ServiceException(Throwable cause, String message) {
        this(message, cause, ErrorCodeEnum.SERVICE_ERROR);
    }

    /**
     * 异常抛出并指定错误码.
     *
     * @param cause
     * @param errorCode
     */
    public ServiceException(Throwable cause, ErrorCode errorCode) {
        this(errorCode.getMessage(), cause, errorCode);
    }

    /**
     * 报错信息与错误码同时存在, 并且不一致.
     * <p> 以报错信息为主, 进行打印.
     *
     * @param message
     * @param cause
     * @param errorCode
     */
    public ServiceException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode.getCode() + "'," +
                "message='" + errorCode.getMessage() + "'" +
                '}';
    }

}
