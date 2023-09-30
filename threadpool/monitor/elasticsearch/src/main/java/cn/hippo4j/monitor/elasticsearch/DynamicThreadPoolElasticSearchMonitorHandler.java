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
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.toolkit.FileUtil;
import cn.hippo4j.monitor.base.AbstractDynamicThreadPoolMonitor;
import cn.hippo4j.threadpool.monitor.support.MonitorTypeEnum;
import cn.hippo4j.monitor.elasticsearch.model.ElasticSearchThreadPoolRunStateInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Dynamic thread-pool elastic-search monitor handler.
 */
@Slf4j
public class DynamicThreadPoolElasticSearchMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    private AtomicBoolean isIndexExist = null;

    public DynamicThreadPoolElasticSearchMonitorHandler(ThreadPoolRunStateHandler handler) {
        super(handler);
    }

    @Override
    protected void execute(ThreadPoolRunStateInfo poolRunStateInfo) {
        ElasticSearchThreadPoolRunStateInfo esThreadPoolRunStateInfo = BeanUtil.convert(poolRunStateInfo, ElasticSearchThreadPoolRunStateInfo.class);
        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String indexName = environment.getProperty("es.thread-pool-state.index.name", "thread-pool-state");
        String applicationName = environment.getProperty("spring.application.name", "application");
        if (!this.isExists(indexName)) {
            List<String> rawMapping = FileUtil.readLines(Thread.currentThread().getContextClassLoader().getResource("mapping.json").getPath(), StandardCharsets.UTF_8);
            String mapping = String.join(" ", rawMapping);
            // if index doesn't exsit, this function may try to create one, but recommend to create index manually.
            this.createIndex(EsIndex.builder().index(indexName).type("_doc").mapping(mapping).build());
        }
        esThreadPoolRunStateInfo.setApplicationName(applicationName);
        esThreadPoolRunStateInfo.setId(indexName + "-" + System.currentTimeMillis());
        this.log2Es(esThreadPoolRunStateInfo, indexName);
    }

    public void log2Es(ElasticSearchThreadPoolRunStateInfo esThreadPoolRunStateInfo, String indexName) {
        RestHighLevelClient client = ElasticSearchClientHolder.getClient();
        try {
            IndexRequest request = new IndexRequest(indexName, "_doc");
            request.id(esThreadPoolRunStateInfo.getId());
            String stateJson = JSONUtil.toJSONString(esThreadPoolRunStateInfo);
            request.source(stateJson, XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            log.info("write thread-pool state to es, id is :{}", response.getId());
        } catch (Exception ex) {
            log.error("es index error, the exception was thrown in create index. name:{},type:{},id:{}. {} ",
                    indexName,
                    "_doc",
                    esThreadPoolRunStateInfo.getId(),
                    ex);
        }
    }

    public synchronized boolean isExists(String index) {
        // cache check result
        if (Objects.isNull(isIndexExist)) {
            boolean exists = false;
            GetIndexRequest request = new GetIndexRequest(index);
            try {
                RestHighLevelClient client = ElasticSearchClientHolder.getClient();
                exists = client.indices().exists(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("check es index fail");
            }
            isIndexExist = new AtomicBoolean(exists);
        }
        return isIndexExist.get();
    }

    public void createIndex(EsIndex esIndex) {
        RestHighLevelClient client = ElasticSearchClientHolder.getClient();
        boolean acknowledged = false;
        CreateIndexRequest request = new CreateIndexRequest(esIndex.getIndex());
        if (StringUtils.hasText(esIndex.getMapping())) {
            request.mapping(esIndex.getType(), esIndex.getMapping(), XContentType.JSON);
        }
        if (!Objects.isNull(esIndex.getShards()) && !Objects.isNull(esIndex.getReplicas())) {
            request.settings(Settings.builder()
                    .put("index.number_of_shards", esIndex.getShards())
                    .put("index.number_of_replicas", esIndex.getReplicas()));
        }
        if (StringUtils.hasText(esIndex.getAlias())) {
            request.alias(new Alias(esIndex.getAlias()));
        }
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            acknowledged = createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error("create es index exception", e);
        }
        if (acknowledged) {
            log.info("create es index success");
            isIndexExist.set(true);
        } else {
            log.error("create es index fail");
            throw new RuntimeException("cannot auto create thread-pool state es index");
        }
    }

    @Override
    public String getType() {
        return MonitorTypeEnum.ELASTICSEARCH.name().toLowerCase();
    }

    /**
     * Es Index
     */
    @Getter
    @Builder
    private static class EsIndex {

        String index;
        String type;
        String mapping;
        Integer shards;
        Integer replicas;
        String alias;
    }
}
