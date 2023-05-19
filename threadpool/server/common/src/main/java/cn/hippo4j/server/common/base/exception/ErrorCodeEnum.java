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

/**
 * Error code enum.
 */
public enum ErrorCodeEnum implements ErrorCode {

    /**
     * UNKNOWN_ERROR
     */
    UNKNOWN_ERROR {

        @Override
        public String getCode() {
            return "1";
        }

        @Override
        public String getMessage() {
            return "UNKNOWN_ERROR";
        }
    },

    /**
     * VALIDATION_ERROR
     */
    VALIDATION_ERROR {

        @Override
        public String getCode() {
            return "2";
        }

        @Override
        public String getMessage() {
            return "VALIDATION_ERROR";
        }
    },

    /**
     * SERVICE_ERROR
     */
    SERVICE_ERROR {

        @Override
        public String getCode() {
            return "3";
        }

        @Override
        public String getMessage() {
            return "SERVICE_ERROR";
        }
    },

    /**
     * NOT_FOUND
     */
    NOT_FOUND {

        @Override
        public String getCode() {
            return "404";
        }

        @Override
        public String getMessage() {
            return "NOT_FOUND";
        }
    },

    /**
     * LOGIN_TIMEOUT
     */
    LOGIN_TIMEOUT {

        @Override
        public String getCode() {
            return "A000004";
        }

        @Override
        public String getMessage() {
            return "登录时间过长, 请退出重新登录";
        }
    }
}
