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

package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;

/**
 * Event publisher.
 */
public interface EventPublisher {

    /**
     * Init.
     *
     * @param type       type
     * @param bufferSize buffer size
     */
    void init(Class<? extends AbstractEvent> type, int bufferSize);

    /**
     * Add subscriber.
     *
     * @param subscriber subscriber
     */
    void addSubscriber(AbstractSubscriber subscriber);

    /**
     * Publish.
     *
     * @param event event
     * @return publish result
     */
    boolean publish(AbstractEvent event);

    /**
     * Notify subscriber.
     *
     * @param subscriber subscriber
     * @param event      event
     */
    void notifySubscriber(AbstractSubscriber subscriber, AbstractEvent event);
}
