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

package cn.hippo4j.rpc.handler;

import cn.hippo4j.rpc.exception.OperationException;
import io.netty.channel.ChannelHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Manage the Handler used in the processing.<br>
 * The Handler must be able to exist multiple times and be invoked once in a single execution
 *
 * @since 2.0.0
 */
public interface HandlerManager<T> {

    /**
     * Add handler to the end of the Handler chain
     *
     * @param name    name
     * @param handler handler
     */
    HandlerManager<T> addLast(String name, T handler);

    /**
     * Add handler to the head of the Handler chain
     *
     * @param name    name
     * @param handler handler
     */
    HandlerManager<T> addFirst(String name, T handler);

    /**
     * Whether handler exists
     *
     * @return Whether handler exists
     */
    boolean isEmpty();

    /**
     * Create a handler
     *
     * @param order   order
     * @param handler Handler
     * @param name    Handler name
     * @return HandlerEntity
     */
    default HandlerEntity<T> getHandlerEntity(long order, T handler, String name) {
        Class<?> cls = handler.getClass();
        boolean b = cls.isAnnotationPresent(ChannelHandler.Sharable.class)
                || HandlerManager.class.isAssignableFrom(cls);
        if (!b) {
            throw new OperationException("Join the execution of the handler must add io.netty.channel.ChannelHandler."
                    + "Sharable annotations, Please for the handler class " + cls.getName() + " add io.netty.channel."
                    + "ChannelHandler.Sharable annotation");
        }
        return new HandlerEntity<>(order, handler, name);
    }

    /**
     * handler entity
     * @param <T>
     */
    @Getter
    @AllArgsConstructor
    class HandlerEntity<T> implements Comparable<HandlerEntity<T>> {

        /**
         * order, The Handler with a larger value is executed after the Handler with a smaller value
         */
        long order;

        /**
         * handler
         */
        T handler;

        /**
         * A high level summary of handler functionality
         */
        String name;

        @Override
        public int compareTo(HandlerEntity<T> o) {
            return (int) (this.getOrder() - o.getOrder());
        }
    }
}
