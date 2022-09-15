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
import cn.hippo4j.config.mapper.HisConfigVerifyMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.HisConfigVerifyInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ThreadPoolInstanceConfigModifyVerifyServiceImpl extends AbstractConfigModifyVerifyService {

    @Override
    public Integer type() {
        return ConfigModifyTypeConstants.THREAD_POOL_INSTANCE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptModification(Long id, ThreadPoolParameterInfo poolParameterInfo) {
    }

}
