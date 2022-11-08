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

package cn.hippo4j.common.spi;

import java.util.Collection;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

/**
 * test {@link DynamicThreadPoolServiceLoader}
 */
public final class DynamicThreadPoolServiceLoaderTest {

    @Test
    public void assertRegister() {
        DynamicThreadPoolServiceLoader.register(Collection.class);
        Collection<?> collections = DynamicThreadPoolServiceLoader.getSingletonServiceInstances(Collection.class);
        assertTrue(collections.isEmpty());
    }

    @Test
    public void assertGetSingletonServiceInstances() {
        DynamicThreadPoolServiceLoader.register(TestSingletonInterfaceSPI.class);
        Collection<TestSingletonInterfaceSPI> instances = DynamicThreadPoolServiceLoader.getSingletonServiceInstances(TestSingletonInterfaceSPI.class);
        assertThat(instances.size(), equalTo(1));
        assertThat(instances.iterator().next(), is(DynamicThreadPoolServiceLoader.getSingletonServiceInstances(TestSingletonInterfaceSPI.class).iterator().next()));
    }

    @Test
    public void assertNewServiceInstances() {
        DynamicThreadPoolServiceLoader.register(TestSingletonInterfaceSPI.class);
        Collection<TestSingletonInterfaceSPI> instances = DynamicThreadPoolServiceLoader.newServiceInstances(TestSingletonInterfaceSPI.class);
        assertThat(instances.size(), equalTo(1));
        assertThat(instances.iterator().next(), not(DynamicThreadPoolServiceLoader.getSingletonServiceInstances(TestSingletonInterfaceSPI.class).iterator().next()));
    }

    @Test
    public void assertGetServiceInstancesWhenIsSingleton() {
        DynamicThreadPoolServiceLoader.register(TestSingletonInterfaceSPI.class);
        Collection<TestSingletonInterfaceSPI> instances = DynamicThreadPoolServiceLoader.getServiceInstances(TestSingletonInterfaceSPI.class);
        assertThat(instances.iterator().next(), is(DynamicThreadPoolServiceLoader.getSingletonServiceInstances(TestSingletonInterfaceSPI.class).iterator().next()));

    }

    @Test
    public void assertGetServiceInstancesWhenNotSingleton() {
        DynamicThreadPoolServiceLoader.register(TestInterfaceSPI.class);
        Collection<TestInterfaceSPI> instances = DynamicThreadPoolServiceLoader.getServiceInstances(TestInterfaceSPI.class);
        assertThat(instances.iterator().next(), not(DynamicThreadPoolServiceLoader.getSingletonServiceInstances(TestInterfaceSPI.class).iterator().next()));

    }
}
