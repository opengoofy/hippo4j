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

import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.enums.VerifyEnum;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.config.mapper.HisConfigVerifyMapper;
import cn.hippo4j.config.model.HisConfigVerifyInfo;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifySaveReqDTO;
import cn.hippo4j.config.service.biz.ConfigModifyVerifyService;
import cn.hippo4j.config.toolkit.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import javax.annotation.Resource;
import java.util.Date;

public abstract class AbstractConfigModifyVerifyService implements ConfigModifyVerifyService {

    @Resource
    protected HisConfigVerifyMapper hisConfigVerifyMapper;

    @Override
    public void saveConfigModifyApplication(ConfigModifySaveReqDTO reqDTO) {
        HisConfigVerifyInfo hisConfigVerifyInfo = BeanUtil.convert(reqDTO, HisConfigVerifyInfo.class);
        hisConfigVerifyInfo.setContent(ContentUtil.getPoolContent(reqDTO));
        hisConfigVerifyInfo.setVerifyStatus(VerifyEnum.TO_VERIFY.getVerifyStatus());
        hisConfigVerifyInfo.setModifyAll(EnableEnum.NO.getIntCode());

        hisConfigVerifyMapper.insert(hisConfigVerifyInfo);
    }

    @Override
    public void rejectModification(Long id, ThreadPoolParameterInfo poolParameterInfo) {
        LambdaUpdateWrapper<HisConfigVerifyInfo> updateWrapper = new LambdaUpdateWrapper<HisConfigVerifyInfo>()
                .eq(HisConfigVerifyInfo::getId, id)
                .set(HisConfigVerifyInfo::getVerifyStatus, VerifyEnum.VERIFY_REJECT.getVerifyStatus())
                .set(HisConfigVerifyInfo::getGmtVerify, new Date())
                .set(HisConfigVerifyInfo::getVerifyUser, UserContext.getUserName());

        hisConfigVerifyMapper.update(null, updateWrapper);

    }

    public void acceptModification(Long id, ThreadPoolParameterInfo poolParameterInfo) {
        LambdaUpdateWrapper<HisConfigVerifyInfo> updateWrapper = new LambdaUpdateWrapper<HisConfigVerifyInfo>()
                .eq(HisConfigVerifyInfo::getId, id)
                .set(HisConfigVerifyInfo::getVerifyStatus, VerifyEnum.VERIFY_ACCEPT.getVerifyStatus())
                .set(HisConfigVerifyInfo::getGmtVerify, new Date())
                .set(HisConfigVerifyInfo::getVerifyUser, UserContext.getUserName());

        hisConfigVerifyMapper.update(null, updateWrapper);

        updateThreadPoolParameter(poolParameterInfo);
    }

    /**
     * update thread pool parameter
     * @param poolParameterInfo
     */
    protected abstract void updateThreadPoolParameter(ThreadPoolParameterInfo poolParameterInfo);
}
