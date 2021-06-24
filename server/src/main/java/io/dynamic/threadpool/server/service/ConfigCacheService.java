package io.dynamic.threadpool.server.service;

import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.common.toolkit.Md5Util;
import io.dynamic.threadpool.server.constant.Constants;
import io.dynamic.threadpool.server.model.CacheItem;
import io.dynamic.threadpool.server.model.ConfigAllInfo;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Config Cache Service.
 *
 * @author chen.ma
 * @date 2021/6/24 21:19
 */
public class ConfigCacheService {

    static ConfigService configService = null;

    private static final ConcurrentHashMap<String, CacheItem> CACHE = new ConcurrentHashMap();

    public static boolean isUpdateData(String groupKey, String md5, String ip) {
        String contentMd5 = ConfigCacheService.getContentMd5(groupKey, ip);
        return Objects.equals(contentMd5, md5);
    }

    /**
     * 获取 Md5
     * TODO：加入 IP, 不同 IP 的线程池区别改写
     * TODO：groupKey && Md5 Cache
     *
     * @param groupKey
     * @param ip
     * @return
     */
    private static String getContentMd5(String groupKey, String ip) {
        CacheItem cacheItem = CACHE.get(groupKey);
        if (cacheItem != null) {
            return cacheItem.md5;
        }

        if (configService == null) {
            configService = ApplicationContextHolder.getBean(ConfigService.class);
        }
        String[] split = groupKey.split("\\+");

        ConfigAllInfo config = configService.findConfigAllInfo(split[0], split[1], split[2]);
        if (config != null && !StringUtils.isEmpty(config.getTpId())) {
            String md5 = Md5Util.getTpContentMd5(config);
            cacheItem = new CacheItem(groupKey, md5);
            CACHE.put(groupKey, cacheItem);
        }
        return (cacheItem != null) ? cacheItem.md5 : Constants.NULL;
    }

}
