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

package cn.hippo4j.rpc.support;

/**
 * Instance interface to get an instance
 */
public interface Instance {

    /**
     * get a instance
     *
     * @param cls Class object
     * @return Information about instances created or found
     */
    Object getInstance(Class<?> cls);

    /**
     * Gets an instance of a class with a recognizable identity,
     * which can be the fully qualified name of class. It can also be a unique name in a container
     *
     * @param name Identifying name
     * @return Information about instances created or found
     */
    Object getInstance(String name);

}
