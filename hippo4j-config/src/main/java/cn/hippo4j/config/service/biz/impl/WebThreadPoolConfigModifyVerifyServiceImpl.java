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
import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifyVerifyReqDTO;
import cn.hippo4j.config.model.biz.threadpool.WebThreadPoolReqDTO;
import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.hippo4j.common.constant.Constants.HTTP_EXECUTE_TIMEOUT;

@Slf4j
@Service
public class WebThreadPoolConfigModifyVerifyServiceImpl extends AbstractConfigModifyVerifyService {

    @Resource
    private BaseInstanceRegistry baseInstanceRegistry;

    @Override
    public Integer type() {
        return ConfigModifyTypeConstants.WEB_THREAD_POOL;
    }

    @Override
    protected void updateThreadPoolParameter(ConfigModifyVerifyReqDTO reqDTO) {
        WebThreadPoolReqDTO webThreadPoolReqDTO = reqDTO.getWebThreadPoolReqDTO();
        List<String> clientAddressList = new ArrayList<>();
        // modify all instances
        if (EnableEnum.YES.getIntCode() == webThreadPoolReqDTO.getModifyAll()) {
            List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(webThreadPoolReqDTO.getItemId());
            leases.stream()
                    .forEach(lease -> clientAddressList.add(StrBuilder.create(lease.getHolder().getHostName(), ":", lease.getHolder().getPort()).toString()));
        } else {
            clientAddressList.add(reqDTO.getInstanceId().split("_")[0]);
        }

        for (String each : clientAddressList) {
            String urlString = StrBuilder.create("http://", each, "/web/update/pool").toString();
            HttpUtil.post(urlString, JSONUtil.toJSONString(webThreadPoolReqDTO), HTTP_EXECUTE_TIMEOUT);
        }
    }
}
