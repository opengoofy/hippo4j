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
import org.junit.Test;
import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

public class ReflectUtilTest {

    @Test
    public void getFieldValueTest() {
        TestSubClass testSubClass = new TestSubClass();
        Object privateField = ReflectUtil.getFieldValue(testSubClass, "privateField");
        Assert.assertEquals("privateField", privateField);

        Object field = ReflectUtil.getFieldValue(testSubClass, "field");
        Assert.assertEquals("field", field);

    }

    @Test
    public void getFieldValueByFiledTest() {
        TestSubClass testSubClass = new TestSubClass();
        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");

        Object privateFieldVal = ReflectUtil.getFieldValue(testSubClass, privateField);
        Assert.assertEquals("privateField", privateFieldVal);
    }

    @Test
    public void getFieldTest() {
        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");
        Assert.assertNotNull(privateField);

        Field field = ReflectUtil.getField(TestSubClass.class, "field");
        Assert.assertNotNull(field);
    }

    // @Test
    public void getFieldsTest() {
        Field[] fields = ReflectUtil.getFields(TestSubClass.class);
        Assert.assertEquals(4, fields.length);
    }

    // @Test
    public void getFieldsDirectlyTest() {
        Field[] fields = ReflectUtil.getFieldsDirectly(TestSubClass.class, false);
        Assert.assertEquals(2, fields.length);

        fields = ReflectUtil.getFieldsDirectly(TestSubClass.class, true);
        Assert.assertEquals(4, fields.length);
    }

    @Test
    public void getFieldNameTest() {
        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");
        String fieldName = ReflectUtil.getFieldName(privateField);
        Assert.assertNotNull(fieldName);

        Field subField = ReflectUtil.getField(TestSubClass.class, "subField");
        String subfieldName = ReflectUtil.getFieldName(subField);
        Assert.assertNotNull(subfieldName);
    }

    @Test
    public void setFieldValueTest() {
        TestClass testClass = new TestClass();
        ReflectUtil.setFieldValue(testClass, "field", "fieldVal");
        Assert.assertEquals("fieldVal", testClass.getField());

        Field privateField = ReflectUtil.getField(TestSubClass.class, "privateField");
        ReflectUtil.setFieldValue(testClass, privateField, "privateFieldVal");
        Assert.assertEquals("privateFieldVal", testClass.getPrivateField());

    }

    @Test
    public void castTest() {
        TestClass testClass = new TestSubClass();
        Object cast = ReflectUtil.cast(TestSubClass.class, testClass);
        Assert.assertTrue(cast instanceof TestSubClass);
    }

    @Test
    public void getDefaultValueTest() {
        Object defaultValue = ReflectUtil.getDefaultValue(Long.class);
        Assert.assertNull(defaultValue);
        Object primitiveValueLong = ReflectUtil.getDefaultValue(long.class);
        Assert.assertEquals(0L, primitiveValueLong);
        Object primitiveValueInt = ReflectUtil.getDefaultValue(int.class);
        Assert.assertEquals(0, primitiveValueInt);
        Object primitiveValueFloat = ReflectUtil.getDefaultValue(float.class);
        Assert.assertEquals(0f, primitiveValueFloat);
        Object primitiveValueShort = ReflectUtil.getDefaultValue(short.class);
        Assert.assertEquals((short) 0, primitiveValueShort);
        Object primitiveValueChar = ReflectUtil.getDefaultValue(char.class);
        Assert.assertEquals((char) 0, primitiveValueChar);
        Object primitiveValueDouble = ReflectUtil.getDefaultValue(double.class);
        Assert.assertEquals(0D, primitiveValueDouble);
        Object primitiveValueBoolean = ReflectUtil.getDefaultValue(boolean.class);
        Assert.assertEquals(false, primitiveValueBoolean);
    }

    @Test
    public void getMethodByNameTest() {
        // private method
        Method runStateLessThan = ReflectUtil.getMethodByName(ThreadPoolExecutor.class, "runStateLessThan");
        Assert.assertNotNull(runStateLessThan);
        // public method
        Method field = ReflectUtil.getMethodByName(TestClass.class, "setPrivateField");
        Assert.assertNotNull(field);
        // parameters
        Method privateField = ReflectUtil.getMethodByName(TestClass.class, "setPrivateField", String.class);
        Assert.assertNotNull(privateField);
    }

    @Test
    public void invokeTest() {
        TestClass testClass = new TestClass();
        Method method = ReflectUtil.getMethodByName(TestClass.class, "getPrivateField");
        String invoke = ReflectUtil.invoke(testClass, method);
        Assert.assertEquals(invoke, "privateField");
    }

    @Getter
    @Setter
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
