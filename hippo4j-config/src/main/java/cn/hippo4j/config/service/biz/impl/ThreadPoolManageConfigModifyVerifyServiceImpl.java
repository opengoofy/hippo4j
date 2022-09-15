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

package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.common.constant.ConfigModifyTypeConstants;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import cn.hippo4j.config.service.biz.ThreadPoolService;
import cn.hippo4j.config.toolkit.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ThreadPoolManageConfigModifyVerifyServiceImpl extends AbstractConfigModifyVerifyService {

    @Resource
    private ThreadPoolService threadPoolService;

    @Override
    public Integer type() {
        return ConfigModifyTypeConstants.THREAD_POOL_MANAGER;
    }

    @Override
    public void updateThreadPoolParameter(ThreadPoolParameterInfo poolParameterInfo) {
        ThreadPoolSaveOrUpdateReqDTO reqDTO = BeanUtil.convert(poolParameterInfo, ThreadPoolSaveOrUpdateReqDTO.class);
        threadPoolService.saveOrUpdateThreadPoolConfig(null, reqDTO);
    }

}
