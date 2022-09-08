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
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Objects;

public class ReflectUtilTest {

    @Test
    public void getFieldValueTest() {
        TestSubClass testSubClass = new TestSubClass();
        Object privateField = ReflectUtil.getFieldValue(testSubClass, "privateField");
        Assert.isTrue(Objects.equals("privateField", privateField));

        Object field = ReflectUtil.getFieldValue(testSubClass, "field");
        Assert.isTrue(Objects.equals("field", field));

    }

    @Test
    public void getFieldValueByFiledTest() {
        TestSubClass testSubClass = new TestSubClass();
        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");

        Object privateFieldVal = ReflectUtil.getFieldValue(testSubClass, privateField);
        Assert.isTrue(Objects.equals("privateField", privateFieldVal));
    }

    @Test
    public void getFieldTest() {
        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");
        Assert.notNull(privateField);

        Field field = ReflectUtil.getField(TestSubClass.class, "field");
        Assert.notNull(field);
    }

    @Test
    public void getFieldsTest() {
        Field[] fields = ReflectUtil.getFields(TestSubClass.class);
        Assert.isTrue(Objects.equals(4, fields.length));
    }

    @Test
    public void getFieldsDirectlyTest() {
        Field[] fields = ReflectUtil.getFieldsDirectly(TestSubClass.class, false);
        Assert.isTrue(Objects.equals(2, fields.length));

        fields = ReflectUtil.getFieldsDirectly(TestSubClass.class, true);
        Assert.isTrue(Objects.equals(4, fields.length));
    }

    @Test
    public void getFieldNameTest() {
        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");
        String fieldName = ReflectUtil.getFieldName(privateField);
        Assert.notNull(fieldName);

        Field subField = ReflectUtil.getField(TestSubClass.class, "subField");
        String subfieldName = ReflectUtil.getFieldName(subField);
        Assert.notNull(subfieldName);
    }

    @Test
    public void setFieldValueTest() {
        TestClass testClass = new TestClass();
        ReflectUtil.setFieldValue(testClass, "field", "fieldVal");
        Assert.isTrue(Objects.equals("fieldVal", testClass.getField()));

        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");
        ReflectUtil.setFieldValue(testClass, privateField, "privateFieldVal");
        Assert.isTrue(Objects.equals("privateFieldVal", testClass.getPrivateField()));

    }

    @Getter
    static class TestClass {

        private String privateField;
        protected String field;

        public TestClass() {
            this.privateField = "privateField";
            this.field = "field";
        }
    }

    class TestSubClass extends TestClass {

        private String subField;
    }

}
