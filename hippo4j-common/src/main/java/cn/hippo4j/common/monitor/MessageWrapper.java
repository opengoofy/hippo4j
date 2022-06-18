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

package cn.hippo4j.common.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Message wrapper.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper implements MessageRequest<Message>, Serializable {

    /**
     * contentParams
     */
    private List<Map<String, Object>> contentParams;

    /**
     * responseClass
     */
    private Class responseClass;

    /**
     * getMessageType
     */
    private MessageTypeEnum messageType;

    @Override
    public List<Map<String, Object>> getContentParams() {
        return contentParams;
    }

    @Override
    public Class<Message> getResponseClass() {
        return responseClass;
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return messageType;
    }
}
