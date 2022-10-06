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

package cn.hippo4j.common.toolkit;

import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BeanUtilTest {

    @Test
    public void testMapToBean() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Test");
        map.put("status", 12);
        Customer customer = BeanUtil.mapToBean(map, Customer.class, true);
        Assert.assertEquals("Test", customer.getName());
        Assert.assertEquals(Integer.valueOf(12), customer.getStatus());
    }

    @Test
    public void testGetter() {
        Method name = BeanUtil.getter(Customer.class, "name");
        Assert.assertEquals("getName", name.getName());
    }

    @Test
    public void testSetter() {
        Method name = BeanUtil.setter(Customer.class, "name");
        Assert.assertEquals("setName", name.getName());
    }

    @Getter
    @Setter
    static class Customer {

        String name;

        Integer status;

    }

    @Getter
    @Setter
    static class PreCustomer {

        String name;

        Integer status;

    }
}
