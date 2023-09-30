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

package cn.hippo4j.config.service;

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.common.extension.design.Observer;
import cn.hippo4j.common.extension.design.ObserverMessage;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.Joiner;
import cn.hippo4j.common.toolkit.MapUtil;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.event.LocalDataChangeEvent;
import cn.hippo4j.config.model.CacheItem;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.notify.NotifyCenter;
import cn.hippo4j.config.service.biz.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER;
import static cn.hippo4j.common.constant.Constants.GROUP_KEY_DELIMITER_TRANSLATION;
import static cn.hippo4j.common.constant.MagicNumberConstants.INDEX_3;

/**
 * Config cache service.
 */
@Slf4j
public class ConfigCacheService {

    private static ConfigService configService;

    static {
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.CLEAR_CONFIG_CACHE, new ClearConfigCache());
    }

    /**
     * key: message-produce+dynamic-threadpool-example+prescription+192.168.20.227:8088_xxx
     * val:
     * key: 192.168.20.227:8088_xxx
     * val:  {@link CacheItem}
     */
    private static final ConcurrentHashMap<String, Map<String, CacheItem>> CLIENT_CONFIG_CACHE = new ConcurrentHashMap();

    public static boolean isUpdateData(String groupKey, String md5, String clientIdentify) {
        String contentMd5 = ConfigCacheService.getContentMd5IsNullPut(groupKey, clientIdentify);
        return Objects.equals(contentMd5, md5);
    }

    /**
     * check TpId.
     *
     * @param groupKey
     * @param tpId
     * @param clientIdentify
     * @return
     */
    public static boolean checkTpId(String groupKey, String tpId, String clientIdentify) {
        Map<String, CacheItem> cacheItemMap = Optional.ofNullable(CLIENT_CONFIG_CACHE.get(groupKey)).orElse(new HashMap<>());
        if (CollectionUtil.isNotEmpty(cacheItemMap)) {
            CacheItem cacheItem = cacheItemMap.get(clientIdentify);
            if (cacheItem != null) {
                return Objects.equals(tpId, cacheItem.getConfigAllInfo().getTpId());
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Get Md5.
     *
     * @param groupKey
     * @param clientIdentify
     * @return
     */
    private static synchronized String getContentMd5IsNullPut(String groupKey, String clientIdentify) {
        Map<String, CacheItem> cacheItemMap = Optional.ofNullable(CLIENT_CONFIG_CACHE.get(groupKey)).orElse(new HashMap<>());
        CacheItem cacheItem = null;
        if (CollectionUtil.isNotEmpty(cacheItemMap)) {
            cacheItem = cacheItemMap.get(clientIdentify);
            if (cacheItem != null) {
                return cacheItem.getMd5();
            }
        }
        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }
        String[] params = groupKey.split(GROUP_KEY_DELIMITER_TRANSLATION);
        ConfigAllInfo config = configService.findConfigRecentInfo(params);
        if (config != null && StringUtil.isNotBlank(config.getTpId())) {
            cacheItem = new CacheItem(groupKey, config);
            cacheItemMap.put(clientIdentify, cacheItem);
            CLIENT_CONFIG_CACHE.put(groupKey, cacheItemMap);
        }
        return (cacheItem != null) ? cacheItem.getMd5() : Constants.NULL;
    }

    public static String getContentMd5(String groupKey) {
        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }
        String[] params = groupKey.split(GROUP_KEY_DELIMITER_TRANSLATION);
        ConfigAllInfo config = configService.findConfigRecentInfo(params);
        if (config == null || StringUtils.isEmpty(config.getTpId())) {
            String errorMessage = String.format("config is null. tpId: %s, itemId: %s, tenantId: %s", params[0], params[1], params[2]);
            throw new RuntimeException(errorMessage);
        }
        return Md5Util.getTpContentMd5(config);
    }

    public static void updateMd5(String groupKey, String identify, String md5) {
        CacheItem cache = makeSure(groupKey, identify);
        if (cache.getMd5() == null || !cache.getMd5().equals(md5)) {
            cache.setMd5(md5);
            String[] params = groupKey.split(GROUP_KEY_DELIMITER_TRANSLATION);
            ConfigAllInfo config = configService.findConfigRecentInfo(params);
            cache.setConfigAllInfo(config);
            cache.setLastModifiedTs(System.currentTimeMillis());
            NotifyCenter.publishEvent(new LocalDataChangeEvent(identify, groupKey));
        }
    }

    public static synchronized CacheItem makeSure(String groupKey, String ip) {
        Map<String, CacheItem> ipCacheItemMap = CLIENT_CONFIG_CACHE.get(groupKey);
        if (ipCacheItemMap != null) {
            CacheItem item = ipCacheItemMap.get(ip);
            if (item != null) {
                return item;
            }
        }
        CacheItem tmp = new CacheItem(groupKey);
        Map<String, CacheItem> cacheItemMap = new HashMap<>();
        cacheItemMap.put(ip, tmp);
        CLIENT_CONFIG_CACHE.putIfAbsent(groupKey, cacheItemMap);
        return tmp;
    }

    public static Map<String, CacheItem> getContent(String identification) {
        List<String> identificationList = MapUtil.parseMapForFilter(CLIENT_CONFIG_CACHE, identification);
        Map<String, CacheItem> returnStrCacheItemMap = new HashMap<>();
        identificationList.forEach(each -> returnStrCacheItemMap.putAll(CLIENT_CONFIG_CACHE.get(each)));
        return returnStrCacheItemMap;
    }

    public static synchronized Integer getTotal() {
        AtomicInteger total = new AtomicInteger();
        CLIENT_CONFIG_CACHE.forEach((key, val) -> total.addAndGet(val.values().size()));
        return total.get();
    }

    public static List<String> getIdentifyList(String tenantId, String itemId, String threadPoolId) {
        List<String> identifyList = null;
        String buildKey = Joiner.on(GROUP_KEY_DELIMITER).join(CollectionUtil.newArrayList(threadPoolId, itemId, tenantId));
        List<String> keys = MapUtil.parseMapForFilter(CLIENT_CONFIG_CACHE, buildKey);
        if (CollectionUtil.isNotEmpty(keys)) {
            identifyList = new ArrayList<>(keys.size());
            for (String each : keys) {
                String[] keyArray = each.split(GROUP_KEY_DELIMITER_TRANSLATION);
                if (keyArray.length > 2) {
                    identifyList.add(keyArray[INDEX_3]);
                }
            }
        }
        return identifyList;
    }

    /**
     * Remove config cache.
     *
     * @param groupKey tenant + item + IP
     */
    public static void removeConfigCache(String groupKey) {
        coarseRemove(groupKey);
    }

    private static synchronized void coarseRemove(String coarse) {
        // fuzzy search
        List<String> identificationList = MapUtil.parseMapForFilter(CLIENT_CONFIG_CACHE, coarse);
        for (String cacheMapKey : identificationList) {
            Map<String, CacheItem> removeCacheItem = CLIENT_CONFIG_CACHE.remove(cacheMapKey);
            log.info("Remove invalidated config cache. config info: {}", JSONUtil.toJSONString(removeCacheItem));
        }
    }

    /**
     * This is an observer, clear config cache.
     */
    static class ClearConfigCache implements Observer<String> {

        @Override
        public void accept(ObserverMessage<String> observerMessage) {
            log.info("Clean up the configuration cache. Key: {}", observerMessage.message());
            coarseRemove(observerMessage.message());
        }
    }
}
