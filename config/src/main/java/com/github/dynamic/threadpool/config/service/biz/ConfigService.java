package com.github.dynamic.threadpool.config.service.biz;

import com.github.dynamic.threadpool.config.model.ConfigAllInfo;

/**
 * Config service.
 *
 * @author chen.ma
 * @date 2021/6/20 15:18
 */
public interface ConfigService {

    /**
     * Find config all info.
     *
     * @param tpId     tpId
     * @param itemId   itemId
     * @param tenantId tenantId
     * @return all config
     */
    ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenantId);

    /**
     * Insert or update.
     *
     * @param configAllInfo
     */
    void insertOrUpdate(ConfigAllInfo configAllInfo);

}
