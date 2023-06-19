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

package cn.hippo4j.config.springboot.starter.refresher;

import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Etcd refresher handler.
 */
@Slf4j
public class EtcdRefresherHandler extends AbstractConfigThreadPoolDynamicRefresh {

    private Client client;

    private static final String ENDPOINTS = "endpoints";

    private static final String USER = "user";

    private static final String PASSWORD = "password";

    private static final String CHARSET = "charset";

    private static final String AUTHORITY = "authority";

    private static final String KEY = "key";

    @SneakyThrows(value = {InterruptedException.class, ExecutionException.class})
    @Override
    public void registerListener() {
        BootstrapConfigProperties actualBootstrapConfigProperties = (BootstrapConfigProperties) bootstrapConfigProperties;
        Map<String, String> etcd = actualBootstrapConfigProperties.getEtcd();
        String key = etcd.get(KEY);
        Charset charset = StringUtil.isBlank(etcd.get(CHARSET)) ? StandardCharsets.UTF_8 : Charset.forName(etcd.get(CHARSET));
        initClient(etcd, charset);
        // TODO Currently only supports json
        GetResponse getResponse = client.getKVClient().get(ByteSequence.from(key, charset)).get();
        KeyValue keyValue = getResponse.getKvs().get(0);
        if (Objects.isNull(keyValue)) {
            return;
        }
        client.getWatchClient().watch(ByteSequence.from(key, charset), new Watch.Listener() {

            @Override
            public void onNext(WatchResponse response) {
                WatchEvent watchEvent = response.getEvents().get(0);
                WatchEvent.EventType eventType = watchEvent.getEventType();
                // todo Currently only supports json
                if (Objects.equals(eventType, WatchEvent.EventType.PUT)) {
                    KeyValue keyValue1 = watchEvent.getKeyValue();
                    String value = keyValue1.getValue().toString(charset);
                    Map map = JSONUtil.parseObject(value, Map.class);
                    dynamicRefresh(keyValue1.getKey().toString(charset), map);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Dynamic thread pool etcd config watcher exception ", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Dynamic thread pool etcd config key refreshed, config key {}", key);
            }
        });
    }

    /**
     * if client is null, init it
     *
     * @param etcd    etcd configuration item
     * @param charset charset
     */
    private void initClient(Map<String, String> etcd, Charset charset) {
        // TODO
        if (Objects.isNull(client)) {
            String user = etcd.get(USER);
            String password = etcd.get(PASSWORD);
            String authority = etcd.get(AUTHORITY);
            String endpoints = etcd.get(ENDPOINTS);
            ClientBuilder clientBuilder = Client.builder().endpoints(endpoints.split(","));
            client = StringUtil.isAllNotEmpty(user, password) ? clientBuilder.user(ByteSequence.from(user, charset))
                    .password(ByteSequence.from(password, charset)).authority(authority)
                    .build() : clientBuilder.build();
        }
    }
}
