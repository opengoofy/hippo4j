package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.constant.ConfigChangeTypeConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadPoolInstanceConfigChangeVerifyServiceImpl extends AbstractConfigChangeVerifyService{

    @Override
    public Integer type() {
        return ConfigChangeTypeConstants.THREAD_POOL_INSTANCE;
    }

    @Override
    public void accept(String id) {

    }
}
