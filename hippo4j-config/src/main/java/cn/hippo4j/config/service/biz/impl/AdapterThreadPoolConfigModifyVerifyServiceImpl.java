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

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.constant.ConfigModifyTypeConstants;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifyVerifyReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cn.hippo4j.adapter.base.ThreadPoolAdapterBeanContainer.THREAD_POOL_ADAPTER_BEAN_CONTAINER;

@Slf4j
@Service
public class AdapterThreadPoolConfigModifyVerifyServiceImpl extends AbstractConfigModifyVerifyService {

    @Override
    public Integer type() {
        return ConfigModifyTypeConstants.ADAPTER_THREAD_POOL;
    }

    @Override
    protected void updateThreadPoolParameter(ConfigModifyVerifyReqDTO reqDTO) {
        ThreadPoolAdapterParameter adapterParameter = reqDTO.getThreadPoolAdapterParameter();
        ThreadPoolAdapter threadPoolAdapter = THREAD_POOL_ADAPTER_BEAN_CONTAINER.get(adapterParameter.getMark());
        Optional.ofNullable(threadPoolAdapter).ifPresent(each -> each.updateThreadPool(adapterParameter));
    }
}
