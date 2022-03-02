package cn.hippo4j.common.web.exception;

/**
 * 错误码抽象接口.
 *
 * @author chen.ma
 * @date 2021/9/16 15:39
 */
public interface ErrorCode {

    /**
     * 错误码.
     *
     * @return
     */
    String getCode();

    /**
     * 错误信息.
     *
     * @return
     */
    String getMessage();

}
