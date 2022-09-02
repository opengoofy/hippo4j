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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *@author : wh
 *@date : 2022/8/30 17:59
 *@description:
 */
@Slf4j
public class EtcdRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private Client client;

	private static final String ENDPOINTS = "endpoints";

	private static final String USER = "user";

	private static final String PASSWORD = "password";

	private static final String CHARSET = "charset";

	private static final String AUTHORITY = "authority";

	private static final String KEY = "key";

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, String> etcd = bootstrapConfigProperties.getEtcd();
		String user = etcd.get(USER);
		String password = etcd.get(PASSWORD);
		String endpoints = etcd.get(ENDPOINTS);
		String authority = etcd.get(AUTHORITY);
		String key = etcd.get(KEY);
		Charset charset = StringUtil.isBlank(etcd.get(CHARSET)) ? StandardCharsets.UTF_8 : Charset.forName(etcd.get(CHARSET));

		ClientBuilder clientBuilder = Client.builder().endpoints(endpoints.split(","));

		client = applicationContext.getBean(Client.class);
		if (Objects.isNull(client)) {
			client = StringUtil.isAllNotEmpty(user, password) ? clientBuilder.user(ByteSequence.from(user, charset))
					.password(ByteSequence.from(password, charset)).authority(authority)
					.build() : clientBuilder.build();
		}

		// todo Currently only supports json
		KeyValue keyValue = client.getKVClient().get(ByteSequence.from(key, charset)).get().getKvs().get(0);
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
				log.error("dynamic thread pool etcd config watcher exception ", throwable);
			}

			@Override
			public void onCompleted() {
				log.info("dynamic thread pool etcd config key refreshed, config key {}", key);
			}
		});

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}