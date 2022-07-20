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

package cn.hippo4j.tools.logrecord.compare;

import java.util.List;

/**
 * 对象比对器.
 *
 * @author chen.ma
 * @date 2021/10/24 20:27
 */
public interface Equator {

    /**
     * 判断两个对象是否相等.
     *
     * @param first
     * @param second
     * @return
     */
    boolean isEquals(Object first, Object second);

    /**
     * 获取两个对象不想等的属性.
     *
     * @param first
     * @param second
     * @return
     */
    List<FieldInfo> getDiffFields(Object first, Object second);

}
