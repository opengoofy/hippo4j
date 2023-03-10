package cn.hippo4j.common.executor.support;

import cn.hippo4j.common.web.exception.AbstractException;

/**
 * This exception is thrown when a context implementation does not support the operation being invoked.
 */
public class NotSupportedException extends AbstractException {

    /**
     * Constructs a new not supported exception with the specified detail message and
     * cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public NotSupportedException(String message) {
        super(message, null, null);
    }
}
