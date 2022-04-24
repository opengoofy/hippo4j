package cn.hippo4j.config.service;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.design.observer.AbstractSubjectCenter;
import cn.hippo4j.common.design.observer.Observer;
import cn.hippo4j.common.design.observer.ObserverMessage;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.config.event.LocalDataChangeEvent;
import cn.hippo4j.config.model.CacheItem;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.notify.NotifyCenter;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.config.toolkit.MapUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Config cache service.
 *
 * @author chen.ma
 * @date 2021/6/24 21:19
 */
@Slf4j
public class ConfigCacheService {

    private static ConfigService CONFIG_SERVICE;

    static {
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.CLEAR_CONFIG_CACHE, new ClearConfigCache());
    }

    /**
     * TODO: 数据结构、客户端停机时 remove 操作待重构
     * <p>
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
     * Get Md5.
     *
     * @param groupKey
     * @param clientIdentify
     * @return
     */
    private synchronized static String getContentMd5IsNullPut(String groupKey, String clientIdentify) {
        Map<String, CacheItem> cacheItemMap = Optional.ofNullable(CLIENT_CONFIG_CACHE.get(groupKey)).orElse(Maps.newHashMap());

        CacheItem cacheItem = null;
        if (CollUtil.isNotEmpty(cacheItemMap) && (cacheItem = cacheItemMap.get(clientIdentify)) != null) {
            return cacheItem.md5;
        }

        if (CONFIG_SERVICE == null) {
            CONFIG_SERVICE = ApplicationContextHolder.getBean(ConfigService.class);
        }
        String[] params = groupKey.split("\\+");
        ConfigAllInfo config = CONFIG_SERVICE.findConfigRecentInfo(params);
        if (config != null && StrUtil.isNotBlank(config.getTpId())) {
            cacheItem = new CacheItem(groupKey, config);
            cacheItemMap.put(clientIdentify, cacheItem);
            CLIENT_CONFIG_CACHE.put(groupKey, cacheItemMap);
        }

        return (cacheItem != null) ? cacheItem.md5 : Constants.NULL;
    }

    public static String getContentMd5(String groupKey) {
        if (CONFIG_SERVICE == null) {
            CONFIG_SERVICE = ApplicationContextHolder.getBean(ConfigService.class);
        }

        String[] params = groupKey.split("\\+");
        ConfigAllInfo config = CONFIG_SERVICE.findConfigRecentInfo(params);
        if (config == null || StringUtils.isEmpty(config.getTpId())) {
            String errorMessage = String.format("config is null. tpId :: %s, itemId :: %s, tenantId :: %s", params[0], params[1], params[2]);
            throw new RuntimeException(errorMessage);
        }

        return Md5Util.getTpContentMd5(config);
    }

    public static void updateMd5(String groupKey, String identify, String md5) {
        CacheItem cache = makeSure(groupKey, identify);
        if (cache.md5 == null || !cache.md5.equals(md5)) {
            cache.md5 = md5;
            String[] params = groupKey.split("\\+");
            ConfigAllInfo config = CONFIG_SERVICE.findConfigRecentInfo(params);
            cache.configAllInfo = config;
            cache.lastModifiedTs = System.currentTimeMillis();
            NotifyCenter.publishEvent(new LocalDataChangeEvent(identify, groupKey));
        }
    }

    public synchronized static CacheItem makeSure(String groupKey, String ip) {
        Map<String, CacheItem> ipCacheItemMap = CLIENT_CONFIG_CACHE.get(groupKey);
        CacheItem item;
        if (ipCacheItemMap != null && (item = ipCacheItemMap.get(ip)) != null) {
            return item;
        }

        CacheItem tmp = new CacheItem(groupKey);
        Map<String, CacheItem> cacheItemMap = Maps.newHashMap();
        cacheItemMap.put(ip, tmp);
        CLIENT_CONFIG_CACHE.putIfAbsent(groupKey, cacheItemMap);

        return tmp;
    }

    public static Map<String, CacheItem> getContent(String identification) {
        List<String> identificationList = MapUtil.parseMapForFilter(CLIENT_CONFIG_CACHE, identification);
        Map<String, CacheItem> returnStrCacheItemMap = Maps.newHashMap();
        identificationList.forEach(each -> returnStrCacheItemMap.putAll(CLIENT_CONFIG_CACHE.get(each)));
        return returnStrCacheItemMap;
    }

    public static synchronized Integer getTotal() {
        AtomicInteger total = new AtomicInteger();
        CLIENT_CONFIG_CACHE.forEach((key, val) -> total.addAndGet(val.values().size()));
        return total.get();
    }

    /**
     * Remove config cache.
     *
     * @param groupKey 租户 + 项目 + IP
     */
    public static void removeConfigCache(String groupKey) {
        coarseRemove(groupKey);
    }

    /**
     * Coarse remove.
     *
     * @param coarse
     */
    private synchronized static void coarseRemove(String coarse) {
        // 模糊搜索
        List<String> identificationList = MapUtil.parseMapForFilter(CLIENT_CONFIG_CACHE, coarse);
        for (String cacheMapKey : identificationList) {
            Map<String, CacheItem> removeCacheItem = CLIENT_CONFIG_CACHE.remove(cacheMapKey);
            log.info("Remove invalidated config cache. config info :: {}", JSONUtil.toJSONString(removeCacheItem));
        }
    }

    /**
     * This is an observer, clear config cache.
     */
    static class ClearConfigCache implements Observer<String> {

        @Override
        public void accept(ObserverMessage<String> observerMessage) {
            log.info("Clean up the configuration cache. Key :: {}", observerMessage.message());
            coarseRemove(observerMessage.message());
        }

    }

}
