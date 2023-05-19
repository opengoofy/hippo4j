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
import cn.hippo4j.server.common.base.exception.AbstractException;
import cn.hippo4j.server.common.base.exception.ErrorCode;
import cn.hippo4j.server.common.base.exception.ErrorCodeEnum;

import java.util.Optional;

/**
 * Results.
 */
public final class Results {

    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setData(data);
    }

    public static Result<Void> failure() {
        return failure(ErrorCodeEnum.SERVICE_ERROR.getCode(), ErrorCodeEnum.SERVICE_ERROR.getMessage());
    }

    public static Result<Void> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .map(ErrorCode::getCode)
                .orElse(ErrorCodeEnum.SERVICE_ERROR.getCode());

        return new Result<Void>().setCode(errorCode)
                .setMessage(abstractException.getMessage());
    }

    public static Result<Void> failure(Throwable throwable) {
        return new Result<Void>().setCode(ErrorCodeEnum.SERVICE_ERROR.getCode())
                .setMessage(throwable.getMessage());
    }

    public static Result<Void> failure(ErrorCode errorCode) {
        return failure(errorCode.getCode(), errorCode.getMessage());
    }

    public static Result<Void> failure(String code, String message) {
        return new Result<Void>()
                .setCode(code)
                .setMessage(message);
    }
}
