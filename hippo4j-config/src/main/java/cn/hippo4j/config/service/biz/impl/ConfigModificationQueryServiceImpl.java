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

import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.config.mapper.HisConfigVerifyMapper;
import cn.hippo4j.config.model.HisConfigVerifyInfo;
import cn.hippo4j.config.model.biz.threadpool.ConfigModificationQueryRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.service.biz.ConfigModificationQueryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Config modification query service impl
 */
@Service
public class ConfigModificationQueryServiceImpl implements ConfigModificationQueryService {

    @Resource
    private HisConfigVerifyMapper hisConfigVerifyMapper;

    @Override
    public IPage<ConfigModificationQueryRespDTO> queryApplicationPage(ThreadPoolQueryReqDTO reqDTO) {
        LambdaQueryWrapper<HisConfigVerifyInfo> wrapper = Wrappers.lambdaQuery(HisConfigVerifyInfo.class)
                .eq(!StringUtils.isBlank(reqDTO.getTenantId()), HisConfigVerifyInfo::getTenantId, reqDTO.getTenantId())
                .eq(!StringUtils.isBlank(reqDTO.getItemId()), HisConfigVerifyInfo::getItemId, reqDTO.getItemId())
                .orderByDesc(HisConfigVerifyInfo::getGmtCreate);
        return hisConfigVerifyMapper.selectPage(reqDTO, wrapper).convert(each -> BeanUtil.convert(each, ConfigModificationQueryRespDTO.class));
    }

    @Override
    public ThreadPoolParameterInfo queryApplicationDetail(Long id) {
        HisConfigVerifyInfo hisConfigVerifyInfo = hisConfigVerifyMapper.selectById(id);
        ThreadPoolParameterInfo poolParameterInfo = JSONUtil.parseObject(hisConfigVerifyInfo.getContent(), ThreadPoolParameterInfo.class);
        poolParameterInfo.setCorePoolSize(poolParameterInfo.corePoolSizeAdapt());
        poolParameterInfo.setMaximumPoolSize(poolParameterInfo.maximumPoolSizeAdapt());
        return poolParameterInfo;
    }
}
