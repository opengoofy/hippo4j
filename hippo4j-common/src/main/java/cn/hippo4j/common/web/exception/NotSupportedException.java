package cn.hippo4j.common.web.exception;

/**
 * @author hongdan.qin
 * @date 2022/12/5 19:42
 */
public class NotSupportedException extends AbstractException {

    public NotSupportedException(String message, ErrorCode errorCode) {
        super(message, null, errorCode);
    }

}
