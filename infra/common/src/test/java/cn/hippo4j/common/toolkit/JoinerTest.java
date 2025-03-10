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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class JoinerTest {

    private Joiner joiner;

    @Before
    public void init() {
        joiner = Joiner.on(",");
    }

    @Test
    public void testJoinWithSeparator() {
        Assert.assertEquals("a,b,c", joiner.join(Arrays.asList("a", "b", "c")));
        Assert.assertEquals("1,2,3", joiner.join(Arrays.asList(1, 2, 3)));
        Assert.assertEquals("true,false,true", joiner.join(Arrays.asList(true, false, true)));
    }

    @Test
    public void testJoinWithEmptyIterable() {
        Assert.assertEquals("", joiner.join(Collections.emptyList()));
    }

    @Test
    public void testJoinWithSingleElement() {
        Assert.assertEquals("a", joiner.join(Collections.singletonList("a")));
        Assert.assertEquals("1", joiner.join(Collections.singletonList(1)));
        Assert.assertEquals("true", joiner.join(Collections.singletonList(true)));
    }

    @Test
    public void testJoinWithCustomObject() {
        Person person1 = new Person("Alice", 18);
        Person person2 = new Person("Bob", 20);
        Assert.assertEquals("Alice(18),Bob(20)", Joiner.on(",").join(Arrays.asList(person1, person2)));
    }

    private static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    @Test(expected = NullPointerException.class)
    public void testJoinWithNullIterator() {
        Joiner.on(", ").join((Iterator<?>) null);
    }

    @Test(expected = NullPointerException.class)
    public void testAppendToWithNullStringBuilder() throws IOException {
        Joiner.on(", ").appendTo(null, Arrays.asList("a", "b").iterator());
    }

    @Test(expected = NullPointerException.class)
    public void testAppendToWithNullAppendable() throws IOException {
        Joiner.on(", ").appendTo(null, Arrays.asList("a", "b").iterator());
    }
}