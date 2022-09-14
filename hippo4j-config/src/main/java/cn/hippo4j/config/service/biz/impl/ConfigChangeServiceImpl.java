package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.config.model.biz.threadpool.ConfigChangeSaveReqDTO;
import cn.hippo4j.config.service.biz.ConfigChangeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ConfigChangeServiceImpl implements ConfigChangeService {

    @Override
    public void saveConfigChange(ConfigChangeSaveReqDTO reqDTO) {

    }
}
