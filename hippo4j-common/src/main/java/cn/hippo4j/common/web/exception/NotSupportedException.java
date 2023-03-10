package cn.hippo4j.common.web.exception;

/**
 * This exception is thrown when a context implementation does not support the operation being invoked.
 */
public class NotSupportedException extends AbstractException {

    public NotSupportedException(String message, ErrorCode errorCode) {
        super(message, null, errorCode);
    }

}
