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

package cn.hippo4j.server.common.base.exception;

import lombok.EqualsAndHashCode;

/**
 * Service exception.
 */
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends AbstractException {

    public ServiceException() {
        this(ErrorCodeEnum.SERVICE_ERROR);
    }

    public ServiceException(ErrorCode errorCode) {
        this(errorCode.getMessage(), null, errorCode);
    }

    public ServiceException(String message) {
        this(message, null, ErrorCodeEnum.SERVICE_ERROR);
    }

    public ServiceException(Throwable cause) {
        this(cause, cause.getMessage());
    }

    public ServiceException(String message, Throwable cause) {
        this(message, cause, ErrorCodeEnum.SERVICE_ERROR);
    }

    public ServiceException(Throwable cause, String message) {
        this(message, cause, ErrorCodeEnum.SERVICE_ERROR);
    }

    public ServiceException(Throwable cause, ErrorCode errorCode) {
        this(errorCode.getMessage(), cause, errorCode);
    }

    public ServiceException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{"
                + "code='" + getErrorCode().getCode() + "',"
                + "message='" + getErrorCode().getMessage() + "'"
                + '}';
    }
}
