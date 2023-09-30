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

package cn.hippo4j.core.extension;

import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.core.extension.anymatch.AnyMatchExtImplA;
import cn.hippo4j.core.extension.anymatch.AnyMatchExtImplB;
import cn.hippo4j.core.extension.anymatch.IAnyMatchExtension;
import cn.hippo4j.core.extension.firstof.FirstOfExtImplA;
import cn.hippo4j.core.extension.firstof.FirstOfExtImplB;
import cn.hippo4j.core.extension.firstof.IFirstOfExtension;
import cn.hippo4j.core.extension.reducer.Reducers;
import cn.hippo4j.core.extension.spi.IOldSpi;
import cn.hippo4j.core.extension.support.ExtensionInvoker;
import cn.hippo4j.core.extension.support.ExtensionRegistry;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExtensionInvokerTest {

    @Before
    public void before() {
        ExtensionRegistry.getInstance().register(new FirstOfExtImplA());
        ExtensionRegistry.getInstance().register(new FirstOfExtImplB());
        ExtensionRegistry.getInstance().register(new AnyMatchExtImplA());
        ExtensionRegistry.getInstance().register(new AnyMatchExtImplB());

        ServiceLoaderRegistry.register(IOldSpi.class);
    }

    @Test
    public void test() {

        Integer arg = 20;
        // first-of
        Integer res1 = ExtensionInvoker.reduceExecute(IFirstOfExtension.class, (ext) -> ext.foo(arg),
                Reducers.firstOfNotNull());
        assertEquals(arg, res1);

        // any-match
        Boolean res2 = ExtensionInvoker.reduceExecute(IAnyMatchExtension.class, (ext) -> ext.foo(arg),
                Reducers.anyMatch(Objects::nonNull));
        assertTrue(res2);

        // none
        List<Integer> res3 = ExtensionInvoker.reduceExecute(IFirstOfExtension.class, (ext) -> ext.foo(arg));
        assertArrayEquals(res3.toArray(new Integer[0]), Lists.newArrayList(null, arg).toArray());

        // all-match
        Boolean res4 = ExtensionInvoker.reduceExecute(IAnyMatchExtension.class, (ext) -> ext.foo(arg),
                Reducers.allMatch(Objects::nonNull));
        assertTrue(res4);

    }

    @Test
    public void test_spi_old() {
        Boolean res1 = ExtensionInvoker.reduceExecute(IOldSpi.class, IOldSpi::foo, Reducers.firstOfNotNull());
        assertTrue(res1);
    }
}
