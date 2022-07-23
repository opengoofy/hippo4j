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

package cn.hippo4j.message.dto;

import cn.hippo4j.message.enums.NotifyTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * Alarm control dto.
 */
@Data
@Builder
public class AlarmControlDTO {

    /**
     * 线程池 Id
     */
    private String threadPool;

    /**
     * 推送报警平台
     */
    private String platform;

    /**
     * 推送报警类型
     */
    private NotifyTypeEnum typeEnum;

    /**
     * 构建线程池报警标识
     *
     * @return
     */
    public String buildPk() {
        return threadPool + "+" + platform;
    }
}
