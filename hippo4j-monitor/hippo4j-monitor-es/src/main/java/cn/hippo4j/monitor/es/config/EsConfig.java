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

package cn.hippo4j.monitor.es.config;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "es")
@Getter
@Setter
public class EsConfig {

    // cluster name
    private String cluster;
    // such as ip1:9200,ip2:9200
    private String host;
    // such as :http
    private String scheme;
    private String userName;
    private String password;
    private int timeout;

    private RestHighLevelClient client;
    @Getter
    private SearchSourceBuilder searchBuilder;

    public EsConfig() {
    }

    public EsConfig(String host) {
        initRestClient(host, null, null);
    }

    public EsConfig(String host, String userName, String password) {
        initRestClient(host, userName, password);
    }

    public RestHighLevelClient initRestClient(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = password;

        return initRestClient();
    }

    @Bean(destroyMethod = "close")
    protected RestHighLevelClient initRestClient() {
        try {
            List<HttpHost> hosts = parseHosts();

            if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)) {
                client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{})));
            } else {
                client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{}))
                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(getCredentialsProvider())));
            }

            log.info("[ES RestHighLevelClient] ES连接成功！host:{},scheme:{}", host, scheme);
            return client;
        } catch (Exception ex) {
            log.error("[ES RestHighLevelClient] ES连接失败! cause:{}", Throwables.getStackTraceAsString(ex));
        }
        return null;
    }

    @Bean
    protected BasicCredentialsProvider getCredentialsProvider() {
        if (!Strings.isNullOrEmpty(userName) && !Strings.isNullOrEmpty(password)) {
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(userName, password));
            return credentialsProvider;
        }
        return null;
    }

    public RestHighLevelClient getClient() {
        return null == client ? initRestClient() : client;
    }

    @Bean
    public SearchSourceBuilder getSearchSourceBuilder() {
        if (null == searchBuilder) {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.timeout(new TimeValue(timeout));

            searchBuilder = sourceBuilder;
        }

        return searchBuilder;
    }

    private List<HttpHost> parseHosts() {
        String[] hostAndPorts = host.split(",");

        List<HttpHost> hosts = Lists.newArrayList();
        for (String hostAndPort : hostAndPorts) {
            hosts.add(new HttpHost(hostAndPort.split(":")[0], Integer.parseInt(hostAndPort.split(":")[1]), scheme));
        }
        return hosts;
    }

    public void printDebugConfigs() {
        String debug = "EsSettings: \n" +
                "cluster:" + cluster + "\n" +
                "host:" + host + "\n" +
                "scheme:" + scheme + "\n" +
                "userName:" + userName + "\n" +
                "password:" + password + "\n" +
                "timeout:" + timeout;

        log.info(debug);
    }
}
