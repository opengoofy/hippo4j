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

package cn.hippo4j.common.extension.design;

import lombok.Getter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractSubjectCenterTest {

    private Map<String, List<Observer>> OBSERVERS_MAP;

    private SubjectNotifyListener subjectNotifyListener;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        subjectNotifyListener = new SubjectNotifyListener();

        Field field = AbstractSubjectCenter.class.getDeclaredField("OBSERVERS_MAP");
        field.setAccessible(true);
        OBSERVERS_MAP = (Map<String, List<Observer>>) field.get(AbstractSubjectCenter.class);
    }

    /**
     * test register listener
     */
    @Test
    public void testDefaultRegister() {
        AbstractSubjectCenter.register(subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.SPRING_CONTENT_REFRESHED.name());
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertSame(subjectNotifyListener, list.get(0));
        OBSERVERS_MAP.clear();
    }

    /**
     * test register listener
     */
    @Test
    public void testSubjectTypeEnumRegister() {
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH, subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH.name());
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertSame(subjectNotifyListener, list.get(0));
        OBSERVERS_MAP.clear();
    }

    /**
     * test register listener
     */
    @Test
    public void testSubjectTypeNameRegister() {
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH.name(), subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH.name());
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertSame(subjectNotifyListener, list.get(0));
        OBSERVERS_MAP.clear();
    }

    /**
     * test remove listener
     */
    @Test
    public void testDefaultRemoveListener() {
        AbstractSubjectCenter.register(subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.SPRING_CONTENT_REFRESHED.name());
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertSame(subjectNotifyListener, list.get(0));

        AbstractSubjectCenter.remove(subjectNotifyListener);
        Assert.assertEquals(0, list.size());
    }

    /**
     * test remove listener
     */
    @Test
    public void testRemoveSubjectTypeNameListener() {
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH, subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH.name());
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertSame(subjectNotifyListener, list.get(0));

        AbstractSubjectCenter.remove(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH.name(), subjectNotifyListener);
        Assert.assertEquals(0, list.size());
    }

    /**
     * test notify
     */
    @Test
    public void testNotifyBySubjectType() {
        AbstractSubjectCenter.register(subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.SPRING_CONTENT_REFRESHED.name());
        Assert.assertNotNull(list);

        NotifyMessage notifyMessage = new NotifyMessage();
        Assert.assertEquals(0, notifyMessage.getCount().get());
        AbstractSubjectCenter.notify(AbstractSubjectCenter.SubjectType.SPRING_CONTENT_REFRESHED, () -> notifyMessage);
        Assert.assertEquals(1, notifyMessage.getCount().get());
        OBSERVERS_MAP.clear();
    }

    /**
     * test notify
     */
    @Test
    public void testNotifyBySubjectTypeName() {
        AbstractSubjectCenter.register(subjectNotifyListener);
        List<Observer> list = OBSERVERS_MAP.get(AbstractSubjectCenter.SubjectType.SPRING_CONTENT_REFRESHED.name());
        Assert.assertNotNull(list);

        NotifyMessage notifyMessage = new NotifyMessage();
        Assert.assertEquals(0, notifyMessage.getCount().get());
        AbstractSubjectCenter.notify(AbstractSubjectCenter.SubjectType.SPRING_CONTENT_REFRESHED.name(), () -> notifyMessage);
        Assert.assertEquals(1, notifyMessage.getCount().get());
        OBSERVERS_MAP.clear();
    }

    @Getter
    private static final class NotifyMessage {

        private final AtomicInteger count = new AtomicInteger(0);
    }

    /**
     * Subject Response Listener
     */
    private static final class SubjectNotifyListener implements Observer<NotifyMessage> {

        @Override
        public void accept(ObserverMessage<NotifyMessage> observerMessage) {
            observerMessage.message().getCount().incrementAndGet();
        }
    }
}
