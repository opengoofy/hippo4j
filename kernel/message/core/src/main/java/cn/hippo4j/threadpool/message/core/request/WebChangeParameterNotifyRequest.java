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

package cn.hippo4j.threadpool.message.core.request;

import cn.hippo4j.threadpool.message.core.request.base.BaseNotifyRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Change parameter notify request for web thread pool.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebChangeParameterNotifyRequest extends BaseNotifyRequest {

    private String active;

    private String appName;

    private String identify;

    private Integer beforeCorePoolSize;

    private Integer nowCorePoolSize;

    private Integer beforeMaximumPoolSize;

    private Integer nowMaximumPoolSize;

    private Long beforeKeepAliveTime;

    private Long nowKeepAliveTime;
}
