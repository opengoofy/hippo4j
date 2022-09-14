package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.threadpool.ConfigChangeSaveReqDTO;

/**
 * Config Change service
 */
public interface ConfigChangeService {

    /**
     * save config change info
     * @param reqDTO
     */
    void saveConfigChange(ConfigChangeSaveReqDTO reqDTO);
}
