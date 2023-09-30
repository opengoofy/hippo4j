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

package cn.hippo4j.monitor.elasticsearch;

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Elastic-search client holder.
 */
@Slf4j
public class ElasticSearchClientHolder {

    private static String host;

    private static String scheme;

    private static String userName;

    private static String password;

    private static RestHighLevelClient client;

    private static RestHighLevelClient initRestClient() {
        try {
            Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
            host = environment.getProperty("es.thread-pool-state.host");
            scheme = environment.getProperty("es.thread-pool-state.schema");
            userName = environment.getProperty("es.thread-pool-state.userName");
            password = environment.getProperty("es.thread-pool-state.password");
            List<HttpHost> hosts = parseHosts();
            if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(password)) {
                client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{})));
            } else {
                client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{}))
                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(getCredentialsProvider())));
            }
            log.info("[ES RestHighLevelClient] success to connect es！host:{},scheme:{}", host, scheme);
            return client;
        } catch (Exception ex) {
            log.error("[ES RestHighLevelClient] fail to connect es! cause:", ex);
        }
        return null;
    }

    private static BasicCredentialsProvider getCredentialsProvider() {
        if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(userName, password));
            return credentialsProvider;
        }
        return null;
    }

    public static RestHighLevelClient getClient() {
        return null == client ? initRestClient() : client;
    }

    private static List<HttpHost> parseHosts() {
        String[] hostAndPorts = host.split(",");
        List<HttpHost> hosts = new ArrayList<>();
        for (String hostAndPort : hostAndPorts) {
            hostAndPort = hostAndPort.trim();
            hosts.add(new HttpHost(hostAndPort.split(":")[0], Integer.parseInt(hostAndPort.split(":")[1]), scheme));
        }
        return hosts;
    }
}
