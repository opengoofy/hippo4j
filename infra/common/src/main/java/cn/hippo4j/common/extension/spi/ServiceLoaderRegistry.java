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

package cn.hippo4j.common.extension.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Dynamic thread-pool SPI service loader.
 */
public final class ServiceLoaderRegistry {

    /**
     * safe container。
     * key : SPI interface class type.
     * value : collection whose elements are object of key's class type.
     */
    private static final Map<Class<?>, Collection<?>> SERVICES = new ConcurrentHashMap<>();

    private ServiceLoaderRegistry() {
    }

    /**
     * Register.
     *
     * @param serviceInterface
     */
    public static void register(final Class<?> serviceInterface) {
        if (!SERVICES.containsKey(serviceInterface)) {
            SERVICES.put(serviceInterface, load(serviceInterface));
        }
    }

    /**
     * Load.
     *
     * @param serviceInterface
     * @param <T>
     * @return
     */
    private static <T> Collection<T> load(final Class<T> serviceInterface) {
        Collection<T> result = new LinkedList<>();
        for (T each : ServiceLoader.load(serviceInterface)) {
            result.add(each);
        }
        return result;
    }

    /**
     * Get Service instances
     *
     * @param serviceClass serviceClass
     * @param <T>
     * @return
     */
    public static <T> Collection<T> getServiceInstances(final Class<T> serviceClass) {
        return null == serviceClass.getAnnotation(SingletonSPI.class) ? newServiceInstances(serviceClass) : getSingletonServiceInstances(serviceClass);
    }

    /**
     * Get singleton service instances.
     *
     * @param service
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getSingletonServiceInstances(final Class<T> service) {
        return (Collection<T>) SERVICES.getOrDefault(service, Collections.emptyList());
    }

    /**
     * New service instances.
     *
     * @param service
     * @param <T>
     * @return
     */
    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        return SERVICES.containsKey(service) ? SERVICES.get(service).stream().map(each -> (T) newServiceInstance(each.getClass())).collect(Collectors.toList()) : Collections.emptyList();
    }

    /**
     * New service instance.
     *
     * @param clazz
     * @return
     */
    private static Object newServiceInstance(final Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            throw new ServiceLoaderInstantiationException(clazz, e);
        }
    }
}
