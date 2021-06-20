package io.dtp.server.service;

import io.dtp.server.model.ConfigAllInfo;

/**
 * 服务端配置接口
 *
 * @author chen.ma
 * @date 2021/6/20 15:18
 */
public interface ConfigService {

    /**
     * 查询配置全部信息
     *
     * @param tpId    tpId
     * @param itemId itemId
     * @param tenant  tenant
     * @return 全部配置信息
     */
    ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenant);
}
