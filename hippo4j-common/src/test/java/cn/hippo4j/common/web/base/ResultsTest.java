package cn.hippo4j.common.web.base;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.web.exception.AbstractException;
import cn.hippo4j.common.web.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static cn.hippo4j.common.web.exception.ErrorCodeEnum.SERVICE_ERROR;

public class ResultsTest {

    @Test
    public void success() {
        Assert.isTrue(Result.SUCCESS_CODE.equals(Results.success().getCode()));
    }

    @Test
    public void testSuccess() {
        Object o = new Object();
        Assert.isTrue(o.equals(Results.success(o).getData()));
        Assert.isTrue(Result.SUCCESS_CODE.equals(Results.success().getCode()));
    }

    @Test
    public void failure() {
        Assert.isTrue(SERVICE_ERROR.getCode().equals(Results.failure().getCode()));
        Assert.isTrue(SERVICE_ERROR.getMessage().equals(Results.failure().getMessage()));
    }

    @Test
    public void testFailure() {
        String code = "500";
        String msg = "message";
        AbstractException abstractException = new AbstractException(msg, new Throwable(), new ErrorCode() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return "errorMsg";
            }
        });
        Assert.isTrue(code.equals(Results.failure(abstractException).getCode()));
        Assert.isTrue(msg.equals(Results.failure(abstractException).getMessage()));
    }

    @Test
    public void testFailure1() {
        String msg = "throwableMsg";
        Throwable throwable = new Throwable(msg);
        Assert.isTrue(SERVICE_ERROR.getCode().equals(Results.failure(throwable).getCode()));
        Assert.isTrue(msg.equals(Results.failure(throwable).getMessage()));
    }

    @Test
    public void testFailure2() {
        String code = "500";
        String msg = "message";

        ErrorCode errorCode = new ErrorCode() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return msg;
            }
        };
        Assert.isTrue(code.equals(Results.failure(errorCode).getCode()));
        Assert.isTrue(msg.equals(Results.failure(errorCode).getMessage()));
    }

    @Test
    public void testFailure3() {
        String code = "500";
        String msg = "message";
        Assert.isTrue(code.equals(Results.failure(code, msg).getCode()));
        Assert.isTrue(msg.equals(Results.failure(code, msg).getMessage()));
    }
}