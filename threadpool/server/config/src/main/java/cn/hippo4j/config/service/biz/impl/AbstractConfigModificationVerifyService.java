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

import cn.hippo4j.common.extension.enums.VerifyEnum;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.ConditionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.config.mapper.HisConfigVerifyMapper;
import cn.hippo4j.config.model.HisConfigVerifyInfo;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifySaveReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifyVerifyReqDTO;
import cn.hippo4j.config.service.biz.ConfigModificationVerifyService;
import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import cn.hippo4j.discovery.core.Lease;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstract config modification verify service.
 */
public abstract class AbstractConfigModificationVerifyService implements ConfigModificationVerifyService {

    @Resource
    protected HisConfigVerifyMapper hisConfigVerifyMapper;

    @Resource
    private BaseInstanceRegistry baseInstanceRegistry;

    @Override
    public void saveConfigModifyApplication(ConfigModifySaveReqDTO reqDTO) {
        HisConfigVerifyInfo hisConfigVerifyInfo = BeanUtil.convert(reqDTO, HisConfigVerifyInfo.class);
        hisConfigVerifyInfo.setContent(JSONUtil.toJSONString(reqDTO));
        hisConfigVerifyInfo.setVerifyStatus(VerifyEnum.TO_VERIFY.getVerifyStatus());
        hisConfigVerifyMapper.insert(hisConfigVerifyInfo);
    }

    @Override
    public void rejectModification(ConfigModifyVerifyReqDTO reqDTO) {
        LambdaUpdateWrapper<HisConfigVerifyInfo> updateWrapper = new LambdaUpdateWrapper<HisConfigVerifyInfo>()
                .eq(HisConfigVerifyInfo::getId, Long.parseLong(reqDTO.getId()))
                .set(HisConfigVerifyInfo::getVerifyStatus, VerifyEnum.VERIFY_REJECT.getVerifyStatus())
                .set(HisConfigVerifyInfo::getGmtVerify, new Date())
                .set(HisConfigVerifyInfo::getVerifyUser, UserContext.getUserName());
        hisConfigVerifyMapper.update(null, updateWrapper);
    }

    public void acceptModification(ConfigModifyVerifyReqDTO reqDTO) {
        updateThreadPoolParameter(reqDTO);
        LambdaUpdateWrapper<HisConfigVerifyInfo> updateWrapper = new LambdaUpdateWrapper<HisConfigVerifyInfo>()
                .eq(HisConfigVerifyInfo::getId, Long.parseLong(reqDTO.getId()))
                .set(HisConfigVerifyInfo::getVerifyStatus, VerifyEnum.VERIFY_ACCEPT.getVerifyStatus())
                .set(HisConfigVerifyInfo::getGmtVerify, new Date())
                .set(HisConfigVerifyInfo::getVerifyUser, UserContext.getUserName());
        hisConfigVerifyMapper.update(null, updateWrapper);
        Date gmtCreate = hisConfigVerifyMapper.selectById(reqDTO.getId()).getGmtCreate();
        LambdaUpdateWrapper<HisConfigVerifyInfo> invalidUpdateWrapper = new LambdaUpdateWrapper<HisConfigVerifyInfo>()
                .eq(HisConfigVerifyInfo::getType, reqDTO.getType())
                .eq(reqDTO.getTenantId() != null, HisConfigVerifyInfo::getTenantId, reqDTO.getTenantId())
                .eq(reqDTO.getItemId() != null, HisConfigVerifyInfo::getItemId, reqDTO.getItemId())
                .eq(reqDTO.getTpId() != null, HisConfigVerifyInfo::getTpId, reqDTO.getTpId())
                .and(reqDTO.getIdentify() != null, wrapper -> wrapper.eq(HisConfigVerifyInfo::getIdentify, reqDTO.getIdentify()).or().eq(HisConfigVerifyInfo::getModifyAll, true))
                .lt(HisConfigVerifyInfo::getGmtCreate, gmtCreate)
                .eq(HisConfigVerifyInfo::getVerifyStatus, VerifyEnum.TO_VERIFY.getVerifyStatus())
                .set(HisConfigVerifyInfo::getVerifyStatus, VerifyEnum.VERIFY_INVALID.getVerifyStatus())
                .set(HisConfigVerifyInfo::getVerifyUser, UserContext.getUserName())
                .set(HisConfigVerifyInfo::getGmtVerify, new Date());
        hisConfigVerifyMapper.update(null, invalidUpdateWrapper);
    }

    /**
     * Get client address.
     *
     * @param reqDTO
     * @return
     */
    protected List<String> getClientAddress(ConfigModifyVerifyReqDTO reqDTO) {
        List<String> clientAddressList = new ArrayList<>();
        List<Lease<InstanceInfo>> leases = baseInstanceRegistry.listInstance(reqDTO.getItemId());
        ConditionUtil
                .condition(reqDTO.getModifyAll(),
                        () -> leases.forEach(lease -> clientAddressList.add(lease.getHolder().getCallBackUrl())),
                        () -> clientAddressList.add(
                                leases.stream()
                                        .filter(lease -> lease.getHolder().getIdentify().equals(reqDTO.getIdentify())).findAny().orElseThrow(() -> new RuntimeException("该线程池实例不存在")).getHolder()
                                        .getCallBackUrl()));
        return clientAddressList;
    }

    /**
     * Update thread pool parameter.
     *
     * @param reqDTO
     */
    protected abstract void updateThreadPoolParameter(ConfigModifyVerifyReqDTO reqDTO);
}
