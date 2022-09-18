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
import com.github.dozermapper.core.converters.ConversionException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * BeanUtil Test
 */
public class BeanUtilTest {

    @Test
    public void beanToMapConvertTest() {
        // 测试BeanToMap
        final Person person = new Person();
        person.setName("Hippo4j");
        person.setAge(1);
        person.setAddress("hippo4j.cn");
        person.setSize(999);
        final Map<?, ?> convert = BeanUtil.convert(person, Map.class);
        Assert.isTrue(Objects.equals("Hippo4j", convert.get("name")));
        Assert.isTrue(Objects.equals(1, convert.get("age")));
        Assert.isTrue(Objects.equals("hippo4j.cn", convert.get("address")));
        Assert.isTrue(Objects.equals(999, convert.get("size")));
    }

    @Test
    public void mapToBeanConvertTest() {
        // 测试MapToBean
        final HashMap<String, Object> map = Maps.newHashMap();
        map.put("name", "Hippo4j");
        map.put("age", 1);
        map.put("address", "hippo4j.cn");
        map.put("size", 999);
        final Person person = BeanUtil.convert(map, Person.class);
        Assert.isTrue(Objects.equals("Hippo4j", person.getName()));
        Assert.isTrue(Objects.equals(1, person.getAge()));
        Assert.isTrue(Objects.equals("hippo4j.cn", person.getAddress()));
        Assert.isTrue(Objects.equals(999, person.getSize()));
    }

    @Test
    public void ListToListConvertTest() {
        final List<Person> list = Lists.newArrayList();
        list.add(Person.builder().name("one").age(1).build());
        list.add(Person.builder().name("two").age(2).build());
        list.add(Person.builder().name("three").age(3).build());

        final List<PersonVo> persons = BeanUtil.convert(list, PersonVo.class);
        Assert.isTrue(Objects.equals(list.size(), persons.size()));
    }

    @Test
    public void copyPropertiesBeanToMapTest() {
        // 测试BeanToMap
        final Person person = new Person();
        person.setName("Hippo4j");

        final Map<?, ?> convert = BeanUtil.convert(person, Map.class);
        Assert.isTrue(Objects.equals("Hippo4j", convert.get("name")));

        // static属性应被忽略
        Assert.isTrue(!convert.containsKey("STATIC_NAME"));
    }

    /**
     * 测试在不忽略错误情况下，转换失败需要报错。
     */
    @Test(expected = ConversionException.class)
    public void mapToBeanWinErrorTest() {
        final Map<String, String> map = new HashMap<>();
        map.put("age", "Hippo4j");
        BeanUtil.convert(map, Person.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person {

        public static final String STATIC_NAME = "STATIC_NAME";

        private String name;
        private int age;
        private String address;
        private Integer size;
    }

    @Setter
    public static class PersonVo {

        private String name;
        private int age;
    }
}
