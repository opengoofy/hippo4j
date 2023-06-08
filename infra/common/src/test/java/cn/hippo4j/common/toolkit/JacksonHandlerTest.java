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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * test for {@link JacksonHandler}
 */
public class JacksonHandlerTest {

    private final static JacksonHandler JACKSON_HANDLER = new JacksonHandler();

    private static final Entity EXPECTED_ENTITY =
            new Entity(1, "entity1", new Entity(2, "entity2", null));

    private static final List<Entity> EXPECTED_ENTITY_ARRAY = Arrays.asList(EXPECTED_ENTITY, EXPECTED_ENTITY);

    private static final String EXPECTED_ENTITY_JSON = "{" +
            "\"id\":1," +
            "\"name\":\"entity1\"," +
            "\"entity\":{" +
            "\"id\":2," +
            "\"name\":\"entity2\"" +
            "}}";

    private static final String EXPECTED_ENTITY_ARRAY_JSON = "[" +
            EXPECTED_ENTITY_JSON + "," +
            EXPECTED_ENTITY_JSON +
            "]";

    @Test
    public void testToJSONString() {
        // boolean to json
        Assertions.assertEquals("true", JACKSON_HANDLER.toJSONString(true));
        // double to json
        Assertions.assertEquals("0.01", JACKSON_HANDLER.toJSONString(0.01));
        // integer to json
        Assertions.assertEquals("1", JACKSON_HANDLER.toJSONString(1));
        // string to json
        Assertions.assertEquals("\"hello world\"", JACKSON_HANDLER.toJSONString("hello world"));
        // array to json
        Assertions.assertEquals("[0,1,2,3,4]", JACKSON_HANDLER.toJSONString(new int[]{0, 1, 2, 3, 4}));
        // object to json
        Assertions.assertEquals(EXPECTED_ENTITY_JSON, JACKSON_HANDLER.toJSONString(EXPECTED_ENTITY));
    }

    @Test
    public void testParseObject() {
        // normal json to boolean
        Assertions.assertEquals(true, JACKSON_HANDLER.parseObject("true", Boolean.class));
        // normal json to double
        Assertions.assertEquals(0.01, JACKSON_HANDLER.parseObject("0.01", Double.class));
        // normal json to integer
        Assertions.assertEquals(1, JACKSON_HANDLER.parseObject("1", Integer.class));
        // normal json to string
        Assertions.assertEquals("hello world",
                JACKSON_HANDLER.parseObject("\"hello world\"", String.class));
        // normal json to object
        Assertions.assertEquals(EXPECTED_ENTITY, JACKSON_HANDLER.parseObject(EXPECTED_ENTITY_JSON, Entity.class));
        Assertions.assertEquals(
                EXPECTED_ENTITY,
                JACKSON_HANDLER.parseObject(EXPECTED_ENTITY_JSON, new TypeReference<Entity>() {
                }));
        // illegal json
        Assertions.assertThrows(MismatchedInputException.class,
                () -> JACKSON_HANDLER.parseObject(" ", Entity.class));
        // null json
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> JACKSON_HANDLER.parseObject(null, Entity.class));
        // illegal type
        Assertions.assertThrows(MismatchedInputException.class,
                () -> JACKSON_HANDLER.parseObject(EXPECTED_ENTITY_JSON, String.class));
    }

    @Test
    public void testParseArray() {
        // normal json to array
        Assertions.assertEquals(EXPECTED_ENTITY_ARRAY, JSONUtil.parseArray(EXPECTED_ENTITY_ARRAY_JSON, Entity.class));
        // null json
        Assertions.assertEquals(Collections.emptyList(), JSONUtil.parseArray(null, Entity.class));
        // illegal json
        Assertions.assertEquals(Collections.emptyList(), JSONUtil.parseArray(" ", Entity.class));
    }

    @Test
    public void testIsJson() {
        // normal json
        Assertions.assertTrue(JACKSON_HANDLER.isJson(EXPECTED_ENTITY_JSON));
        Assertions.assertTrue(JACKSON_HANDLER.isJson(EXPECTED_ENTITY_ARRAY_JSON));
        Assertions.assertTrue(JACKSON_HANDLER.isJson(" "));
        // illegal json
        Assertions.assertFalse(JACKSON_HANDLER.isJson("{" +
                "\"id\":1," +
                "\"name\":\"entity1\"," +
                "\"entity\":{\"id\":2,\"name\":\"entity2\"}"));
        // null json
        Assertions.assertThrows(IllegalArgumentException.class, () -> JACKSON_HANDLER.isJson(null));
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class Entity {

        private Integer id;

        private String name;

        private Entity entity;
    }
}
