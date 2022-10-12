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

package cn.hippo4j.config.toolkit;

import cn.hippo4j.common.toolkit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * ClassUtil Test
 */
public class ClassUtilTest {

    @Test
    public void isAssignableFromTest() {
        final boolean assignableFrom = ClassUtil.isAssignableFrom(TestClass.class, TestSubClass.class);
        Assert.isTrue(assignableFrom);
    }

    @Test
    public void isNotAssignableFromTest() {
        final boolean assignableFrom = ClassUtil.isAssignableFrom(TestSubClass.class, TestClass.class);
        Assert.isTrue(!assignableFrom);
    }

    @Test
    public void getCanonicalNameTest() {
        final String canonicalName = ClassUtil.getCanonicalName(TestClass.class);
        Assert.isTrue(Objects.equals("cn.hippo4j.config.toolkit.ClassUtilTest.TestClass", canonicalName));
    }

    @SuppressWarnings("unused")
    static class TestClass {

        private String privateField;

        protected String field;

        private void privateMethod() {
        }

        public void publicMethod() {
        }
    }

    @SuppressWarnings({"unused", "InnerClassMayBeStatic"})
    class TestSubClass extends TestClass {

        private String subField;

        private void privateSubMethod() {
        }

        public void publicSubMethod() {
        }

    }
}
