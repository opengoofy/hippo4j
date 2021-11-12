package com.github.dynamic.threadpool.config.service;

import cn.hutool.core.collection.CollUtil;
import com.github.dynamic.threadpool.config.service.biz.ConfigService;
import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.toolkit.Md5Util;
import com.github.dynamic.threadpool.config.event.LocalDataChangeEvent;
import com.github.dynamic.threadpool.config.model.CacheItem;
import com.github.dynamic.threadpool.config.model.ConfigAllInfo;
import com.github.dynamic.threadpool.config.notify.NotifyCenter;
import com.github.dynamic.threadpool.config.toolkit.MapUtil;
import com.google.common.collect.Maps;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Config cache service.
 *
 * @author chen.ma
 * @date 2021/6/24 21:19
 */
public class ConfigCacheService {

    static ConfigService configService = null;

    private static final ConcurrentHashMap<String, Map<String, CacheItem>> CACHE = new ConcurrentHashMap();

    public static boolean isUpdateData(String groupKey, String md5, String ip) {
        String contentMd5 = ConfigCacheService.getContentMd5IsNullPut(groupKey, ip);
        return Objects.equals(contentMd5, md5);
    }

    /**
     * Get Md5.
     *
     * @param groupKey
     * @param ip
     * @return
     */
    private static String getContentMd5IsNullPut(String groupKey, String ip) {
        Map<String, CacheItem> cacheItemMap = Optional.ofNullable(CACHE.get(groupKey)).orElse(Maps.newHashMap());

        CacheItem cacheItem = null;
        if (CollUtil.isNotEmpty(cacheItemMap) && (cacheItem = cacheItemMap.get(ip)) != null) {
            return cacheItem.md5;
        }

        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }
        String[] split = groupKey.split("\\+");

        ConfigAllInfo config = configService.findConfigAllInfo(split[0], split[1], split[2]);
        if (config != null && !StringUtils.isEmpty(config.getTpId())) {
            cacheItem = new CacheItem(groupKey, config);
            cacheItemMap.put(ip, cacheItem);
            CACHE.put(groupKey, cacheItemMap);
        }
        return (cacheItem != null) ? cacheItem.md5 : Constants.NULL;
    }

    public static String getContentMd5(String groupKey) {
        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }

        String[] split = groupKey.split("\\+");
        ConfigAllInfo config = configService.findConfigAllInfo(split[0], split[1], split[2]);
        if (config == null || StringUtils.isEmpty(config.getTpId())) {
            String errorMessage = String.format("config is null. tpId :: %s, itemId :: %s, tenantId :: %s", split[0], split[1], split[2]);
            throw new RuntimeException(errorMessage);
        }

        return Md5Util.getTpContentMd5(config);
    }

    public static void updateMd5(String groupKey, String ip, String md5) {
        CacheItem cache = makeSure(groupKey, ip);
        if (cache.md5 == null || !cache.md5.equals(md5)) {
            cache.md5 = md5;
            String[] split = groupKey.split("\\+");
            ConfigAllInfo config = configService.findConfigAllInfo(split[0], split[1], split[2]);
            cache.configAllInfo = config;
            cache.lastModifiedTs = System.currentTimeMillis();
            NotifyCenter.publishEvent(new LocalDataChangeEvent(ip, groupKey));
        }
    }

    public static CacheItem makeSure(String groupKey, String ip) {
        Map<String, CacheItem> ipCacheItemMap = CACHE.get(groupKey);
        CacheItem item = ipCacheItemMap.get(ip);
        if (null != item) {
            return item;
        }

        CacheItem tmp = new CacheItem(groupKey);
        Map<String, CacheItem> cacheItemMap = Maps.newHashMap();
        cacheItemMap.put(ip, tmp);
        CACHE.putIfAbsent(groupKey, cacheItemMap);

        return tmp;
    }

    public static Map<String, CacheItem> getContent(String identification) {
        List<String> identificationList = MapUtil.parseMapForFilter(CACHE, identification);
        Map<String, CacheItem> returnStrCacheItemMap = Maps.newHashMap();
        identificationList.forEach(each -> returnStrCacheItemMap.putAll(CACHE.get(each)));
        return returnStrCacheItemMap;
    }

}
