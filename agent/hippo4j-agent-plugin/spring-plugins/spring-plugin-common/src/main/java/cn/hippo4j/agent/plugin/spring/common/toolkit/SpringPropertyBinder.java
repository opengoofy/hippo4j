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

package cn.hippo4j.agent.plugin.spring.common.toolkit;

import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigFileTypeEnum;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.beans.PropertyEditorSupport;
import java.util.*;

/**
 * CustomPropertyBinder is a utility class for binding properties from Spring's Environment
 * to Java objects based on a given prefix. This is useful for dynamically binding configurations
 * in Spring applications where configuration properties are organized hierarchically and need to be
 * mapped to corresponding Java objects.
 *
 * <p>This class handles complex property structures, including nested properties and collections,
 * ensuring that all properties prefixed with the specified string are correctly bound to the target
 * Java object.</p>
 */
public class SpringPropertyBinder {

    /**
     * Binds properties from the Spring Environment to an instance of the specified configuration class.
     *
     * @param environment the Spring Environment containing property sources.
     * @param prefix      the prefix to filter properties for binding (e.g., "spring.dynamic.thread-pool").
     * @param clazz       the class type of the configuration object to bind properties to.
     * @param <T>         the type of the configuration class.
     * @return an instance of the configuration class with properties bound from the environment.
     * @throws RuntimeException if there is an error instantiating the configuration class or binding properties.
     */
    public static <T> T bindProperties(Environment environment, String prefix, Class<T> clazz) {
        try {
            // Create an instance of the target class
            T instance = clazz.getDeclaredConstructor().newInstance();
            BeanWrapper beanWrapper = new BeanWrapperImpl(instance);

            // Register custom editor for ConfigFileTypeEnum to handle specific type conversions
            beanWrapper.registerCustomEditor(ConfigFileTypeEnum.class, new ConfigFileTypeEnumEditor());

            // Iterate over all property keys that match the given prefix
            for (String key : getAllPropertyKeys(environment, prefix)) {
                String propertyName = key.substring(prefix.length() + 1); // Remove prefix from the property key
                String[] tokens = propertyName.split("\\."); // Split the property name by dot for nested properties
                setPropertyValue(tokens, beanWrapper, environment.getProperty(key)); // Set the property value recursively
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to bind properties to " + clazz.getName(), e);
        }
    }

    /**
     * Binds properties from a map to an instance of the specified configuration class.
     *
     * @param configInfo a map containing property paths and their values.
     * @param prefix     the prefix to filter properties for binding (e.g., "spring.dynamic.thread-pool").
     * @param clazz      the class type of the configuration object to bind properties to.
     * @param <T>        the type of the configuration class.
     * @return an instance of the configuration class with properties bound from the configInfo map.
     */
    public static <T> T bindProperties(Map<Object, Object> configInfo, String prefix, Class<T> clazz) {
        try {
            // Create an instance of the target class
            T instance = clazz.getDeclaredConstructor().newInstance();
            BeanWrapper beanWrapper = new BeanWrapperImpl(instance);

            // Register custom editor for specific type conversions (if needed)
            beanWrapper.registerCustomEditor(ConfigFileTypeEnum.class, new ConfigFileTypeEnumEditor());

            // Iterate over all property keys that match the given prefix in the configInfo map
            for (Map.Entry<Object, Object> entry : configInfo.entrySet()) {
                String key = entry.getKey().toString();
                if (key.startsWith(prefix)) {
                    String propertyName = key.substring(prefix.length() + 1); // Remove prefix from the property key
                    String[] tokens = propertyName.split("\\."); // Split the property name by dot for nested properties
                    setPropertyValue(tokens, beanWrapper, entry.getValue().toString()); // Set the property value recursively
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to bind properties to " + clazz.getName(), e);
        }
    }

    /**
     * Recursively sets property values on the target object, handling nested properties and collections.
     *
     * @param tokens      an array of property path tokens (e.g., ["nested", "property", "name"]).
     * @param beanWrapper the BeanWrapper instance used to manipulate the target object.
     * @param value       the value to set on the target property.
     */
    private static void setPropertyValue(String[] tokens, BeanWrapper beanWrapper, String value) {
        for (int i = 0; i < tokens.length - 1; i++) {
            String token = tokens[i];

            if (token.matches(".*\\[\\d+\\]$")) { // Handle array/list property
                token = token.substring(0, token.indexOf('['));
                int index = Integer.parseInt(tokens[i].substring(token.length() + 1, tokens[i].length() - 1));

                token = convertToCamelCase(token); // Convert token to camelCase if necessary
                List<Object> list = (List<Object>) beanWrapper.getPropertyValue(token);
                if (list == null) {
                    list = new ArrayList<>();
                    beanWrapper.setPropertyValue(convertToCamelCase(token), list); // Initialize the list if it's null
                }

                // Ensure the list has enough size to accommodate the index
                if (list.size() <= index) {
                    try {
                        // Instantiate the list element if it does not exist
                        list.add(index, beanWrapper.getPropertyTypeDescriptor(token)
                                .getElementTypeDescriptor().getType().newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // Move the beanWrapper context to the current list element
                beanWrapper = new BeanWrapperImpl(list.get(index));
            } else { // Handle simple or nested property
                Object nestedObject = beanWrapper.getPropertyValue(token);
                if (nestedObject == null) {
                    Class<?> nestedClass = beanWrapper.getPropertyType(token);
                    if (Map.class.isAssignableFrom(nestedClass)) {
                        nestedObject = new HashMap<>(); // Initialize nested Map if necessary
                    } else {
                        try {
                            nestedObject = nestedClass.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    beanWrapper.setPropertyValue(convertToCamelCase(token), nestedObject);
                }
                // Move the beanWrapper context to the nested object
                beanWrapper = new BeanWrapperImpl(nestedObject);
            }
        }

        // Finally, set the actual property value on the resolved object
        String finalPropertyName = tokens[tokens.length - 1];
        Object currentObject = beanWrapper.getWrappedInstance();
        if (currentObject instanceof Map) {
            // If the current object is a Map, set the value as a key-value pair
            ((Map<String, Object>) currentObject).put(finalPropertyName, value);
        } else {
            // Otherwise, set it as a simple property
            beanWrapper.setPropertyValue(convertToCamelCase(finalPropertyName), value);
        }
    }

    /**
     * Retrieves all property keys from the environment that start with the given prefix.
     *
     * @param environment the Spring Environment containing property sources.
     * @param prefix      the prefix to filter property keys.
     * @return a set of property keys that match the prefix.
     */
    private static Set<String> getAllPropertyKeys(Environment environment, String prefix) {
        Set<String> keys = new HashSet<>();
        // Iterate through all property sources in the environment
        for (PropertySource<?> propertySource : ((ConfigurableEnvironment) environment).getPropertySources()) {
            if (propertySource instanceof MapPropertySource) {
                Map<String, Object> source = ((MapPropertySource) propertySource).getSource();
                // Collect keys that start with the specified prefix
                for (String key : source.keySet()) {
                    if (key.startsWith(prefix)) {
                        keys.add(key);
                    }
                }
            }
        }
        return keys;
    }

    /**
     * Converts a dashed-separated string to camelCase.
     * <p>
     * For example, "my-property-name" -> "myPropertyName".
     *
     * @param dashed the dashed-separated string to be converted.
     * @return the camelCase representation of the input string.
     */
    private static String convertToCamelCase(String dashed) {
        String[] parts = dashed.split("-");
        return Arrays.stream(parts)
                .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1)) // Capitalize each part
                .reduce((first, second) -> first + second) // Concatenate all parts together
                .map(result -> result.substring(0, 1).toLowerCase() + result.substring(1)) // Lowercase the first letter
                .orElse(dashed);
    }

    /**
     * ConfigFileTypeEnumEditor is a custom property editor for converting string representations
     * of {@link ConfigFileTypeEnum} into the corresponding enum instances.
     * <p>
     * This editor is useful in scenarios where properties are read as strings but need to be
     * converted to enum types for further processing.
     */
    public static class ConfigFileTypeEnumEditor extends PropertyEditorSupport {

        /**
         * Converts the given text value to the corresponding {@link ConfigFileTypeEnum} instance.
         * <p>
         * This method overrides the default implementation to parse the input string and convert
         * it into a {@link ConfigFileTypeEnum}. If the input string does not match any known enum
         * value, an {@link IllegalArgumentException} will be thrown.
         *
         * @param text the string representation of the enum to be converted.
         * @throws IllegalArgumentException if the text does not match any known enum value.
         */
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            setValue(ConfigFileTypeEnum.of(text));
        }
    }

}
