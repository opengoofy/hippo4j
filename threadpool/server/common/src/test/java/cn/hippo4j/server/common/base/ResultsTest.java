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

import cn.hippo4j.common.model.Result;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.server.common.base.exception.AbstractException;
import cn.hippo4j.server.common.base.exception.ErrorCode;
import org.junit.Test;

import static cn.hippo4j.server.common.base.exception.ErrorCodeEnum.SERVICE_ERROR;

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