/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.server.common.base;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.server.common.base.exception.ErrorCode;
import cn.hippo4j.server.common.base.exception.ErrorCodeEnum;
import cn.hippo4j.server.common.base.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import java.util.Objects;

/**
 * Service exception test
 */
public class ServiceExceptionTest {

    @Test
    public void ServiceExceptionTest1() {
        ServiceException serviceException = new ServiceException();
        Assert.isTrue(Objects.equals(serviceException.getErrorCode().getCode(), "3"));
        Assert.isTrue(Objects.equals(serviceException.getMessage(), "SERVICE_ERROR"));
    }

    @Test
    public void ServiceExceptionTest2() {
        ErrorCode errorCode = ErrorCodeEnum.LOGIN_TIMEOUT;
        ServiceException serviceException = new ServiceException(errorCode);
        Assert.isTrue(Objects.equals(serviceException.getErrorCode().getCode(), ErrorCodeEnum.LOGIN_TIMEOUT.getCode()));
        Assert.isTrue(Objects.equals(serviceException.getMessage(), ErrorCodeEnum.LOGIN_TIMEOUT.getMessage()));
    }

    @Test
    public void ServiceExceptionTest3() {
        String message = ErrorCodeEnum.SERVICE_ERROR.getMessage();
        ServiceException serviceException = new ServiceException(message);
        Assert.isTrue(Objects.equals(serviceException.getMessage(), message));
    }

    @Test
    public void ServiceExceptionTest4() {
        Throwable cause = new Throwable();
        ServiceException serviceException = new ServiceException(cause);
        Assert.isTrue(Objects.equals(serviceException.getCause().getMessage(), cause.getMessage()));
    }

    @Test
    public void ServiceExceptionTest5() {
        String message = ErrorCodeEnum.SERVICE_ERROR.getMessage();
        Throwable cause = new Throwable();
        ServiceException serviceException = new ServiceException(message, cause);
        Assert.isTrue(Objects.equals(serviceException.getCause().getMessage(), cause.getMessage()));
        Assert.isTrue(Objects.equals(serviceException.getMessage(), message));
    }

    @Test
    public void ServiceExceptionTest6() {
        String message = ErrorCodeEnum.SERVICE_ERROR.getMessage();
        Throwable cause = new Throwable();
        ServiceException serviceException = new ServiceException(cause, message);
        Assert.isTrue(Objects.equals(serviceException.getCause().getMessage(), cause.getMessage()));
        Assert.isTrue(Objects.equals(serviceException.getMessage(), message));
    }

    @Test
    public void ServiceExceptionTest7() {
        Throwable cause = new Throwable();
        ErrorCode errorCode = ErrorCodeEnum.LOGIN_TIMEOUT;
        ServiceException serviceException = new ServiceException(cause, errorCode);
        Assert.isTrue(Objects.equals(serviceException.getCause().getMessage(), cause.getMessage()));
        Assert.isTrue(Objects.equals(serviceException.getErrorCode().getCode(), ErrorCodeEnum.LOGIN_TIMEOUT.getCode()));
    }

    @Test
    public void ServiceExceptionTest8() {
        Throwable cause = new Throwable();
        ErrorCode errorCode = ErrorCodeEnum.LOGIN_TIMEOUT;
        String message = ErrorCodeEnum.SERVICE_ERROR.getMessage();
        ServiceException serviceException = new ServiceException(message, cause, errorCode);
        Assert.isTrue(Objects.equals(serviceException.getCause().getMessage(), cause.getMessage()));
        Assert.isTrue(Objects.equals(serviceException.getErrorCode().getCode(), ErrorCodeEnum.LOGIN_TIMEOUT.getCode()));
        Assert.isTrue(Objects.equals(serviceException.getMessage(), message));
    }
}
