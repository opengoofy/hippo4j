package cn.hippo4j.common.web.base;

import cn.hippo4j.common.web.exception.AbstractException;
import cn.hippo4j.common.web.exception.ErrorCode;
import cn.hippo4j.common.web.exception.ErrorCodeEnum;

import java.util.Optional;

/**
 * Results.
 *
 * @author chen.ma
 * @date 2021/3/19 16:12
 */
public final class Results {

    /**
     * 成功.
     *
     * @return
     */
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    /**
     * 成功.
     *
     * @param data 并设置 DATA 参数
     * @param <T>  对应 DATA 字段的数据类型
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setData(data);
    }

    /**
     * 返回失败信息.
     *
     * @return
     */
    public static Result<Void> failure() {
        return failure(ErrorCodeEnum.SERVICE_ERROR.getCode(), ErrorCodeEnum.SERVICE_ERROR.getMessage());
    }

    /**
     * 框架定义抽象异常拦截.
     *
     * @param abstractException 框架自定义抽象异常
     * @return
     */
    public static Result<Void> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .map(ErrorCode::getCode)
                .orElse(ErrorCodeEnum.SERVICE_ERROR.getCode());

        return new Result<Void>().setCode(errorCode)
                .setMessage(abstractException.getMessage());
    }

    /**
     * 未知异常.
     *
     * @param throwable 未知异常
     * @return
     */
    public static Result<Void> failure(Throwable throwable) {
        return new Result<Void>().setCode(ErrorCodeEnum.SERVICE_ERROR.getCode())
                .setMessage(throwable.getMessage());
    }

    /**
     * 返回失败信息.
     *
     * @param errorCode 错误编码、错误信息
     * @return
     */
    public static Result<Void> failure(ErrorCode errorCode) {
        return failure(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 返回失败信息.
     *
     * @param code    错误编码.
     * @param message 错误信息.
     * @return
     */
    public static Result<Void> failure(String code, String message) {
        return new Result<Void>()
                .setCode(code)
                .setMessage(message);
    }

}
