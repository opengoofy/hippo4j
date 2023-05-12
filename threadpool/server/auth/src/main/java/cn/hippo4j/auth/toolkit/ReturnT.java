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

package cn.hippo4j.auth.toolkit;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ReturnT.
 */
@Data
@NoArgsConstructor
public class ReturnT<T> implements Serializable {

    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;

    public static final int FAIL_CODE = 500;

    public static final int JWT_FAIL_CODE = -1;

    public static final ReturnT<String> SUCCESS = new ReturnT<>(null);

    public static final ReturnT<String> FAIL = new ReturnT<>(FAIL_CODE, null);

    private int code;

    private String message;

    private T content;

    public ReturnT(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ReturnT(T content) {
        this.code = SUCCESS_CODE;
        this.content = content;
    }
}
