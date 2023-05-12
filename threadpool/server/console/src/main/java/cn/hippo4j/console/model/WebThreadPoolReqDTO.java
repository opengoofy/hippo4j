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

package cn.hippo4j.console.model;

import lombok.Data;

import java.util.List;

/**
 * Web thread-pool req dto.
 */
@Data
public class WebThreadPoolReqDTO {

    /**
     * Thread-pool id
     */
    private String tenantId;

    /**
     * Item id
     */
    private String itemId;

    /**
     * thread pool instance id
     */
    private String identify;

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Keep alive time
     */
    private Integer keepAliveTime;

    /**
     * weather modify all instances
     */
    private Boolean modifyAll;

    /**
     * Client address list
     */
    private List<String> clientAddressList;
}
