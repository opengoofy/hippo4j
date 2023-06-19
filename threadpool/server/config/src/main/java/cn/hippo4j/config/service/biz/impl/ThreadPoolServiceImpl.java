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
import cn.hippo4j.common.extension.enums.DelEnum;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.config.mapper.ConfigInfoMapper;
import cn.hippo4j.config.model.ConfigAllInfo;
import cn.hippo4j.config.model.LogRecordInfo;
import cn.hippo4j.config.model.biz.threadpool.ConfigModifySaveReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolDelReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import cn.hippo4j.config.service.biz.ConfigService;
import cn.hippo4j.config.service.biz.OperationLogService;
import cn.hippo4j.config.service.biz.ThreadPoolService;
import cn.hippo4j.config.verify.ConfigModificationVerifyServiceChoose;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Thread pool service impl.
 */
@Service
@RequiredArgsConstructor
public class ThreadPoolServiceImpl implements ThreadPoolService {

    private final ConfigService configService;
    private final ConfigInfoMapper configInfoMapper;
    private final OperationLogService operationLogService;
    private final ConfigModificationVerifyServiceChoose configModificationVerifyServiceChoose;

    private static final List<String> AUDITABLE_ROLES = CollectionUtil.toList("ROLE_ADMIN", "ROLE_MANAGE");

    @Override
    public IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(!StringUtils.isBlank(reqDTO.getTenantId()), ConfigAllInfo::getTenantId, reqDTO.getTenantId())
                .eq(!StringUtils.isBlank(reqDTO.getItemId()), ConfigAllInfo::getItemId, reqDTO.getItemId())
                .eq(!StringUtils.isBlank(reqDTO.getTpId()), ConfigAllInfo::getTpId, reqDTO.getTpId())
                .eq(ConfigAllInfo::getDelFlag, DelEnum.NORMAL.getIntCode())
                .orderByDesc(reqDTO.getDesc() != null, ConfigAllInfo::getGmtCreate);
        return configInfoMapper.selectPage(reqDTO, wrapper).convert(each -> BeanUtil.convert(each, ThreadPoolRespDTO.class));
    }

    @Override
    public ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO) {
        ConfigAllInfo configAllInfo = configService.findConfigAllInfo(reqDTO.getTpId(), reqDTO.getItemId(), reqDTO.getTenantId());
        return BeanUtil.convert(configAllInfo, ThreadPoolRespDTO.class);
    }

    @Override
    public List<ThreadPoolRespDTO> getThreadPoolByItemId(String itemId) {
        LambdaQueryWrapper<ConfigAllInfo> queryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getItemId, itemId);
        List<ConfigAllInfo> selectList = configInfoMapper.selectList(queryWrapper);
        return BeanUtil.convert(selectList, ThreadPoolRespDTO.class);
    }

    @Override
    public void saveOrUpdateThreadPoolConfig(String identify, ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        if (AUDITABLE_ROLES.contains(UserContext.getUserRole())) {
            ConfigAllInfo configAllInfo = BeanUtil.convert(reqDTO, ConfigAllInfo.class);
            Long executeTimeOut = Objects.equals(configAllInfo.getExecuteTimeOut(), 0L) ? null : configAllInfo.getExecuteTimeOut();
            configAllInfo.setExecuteTimeOut(executeTimeOut);
            configService.insertOrUpdate(identify, false, configAllInfo);
        } else {
            ConfigModifySaveReqDTO modifySaveReqDTO = BeanUtil.convert(reqDTO, ConfigModifySaveReqDTO.class);
            modifySaveReqDTO.setCorePoolSize(reqDTO.getCoreSize());
            modifySaveReqDTO.setMaximumPoolSize(reqDTO.getMaxSize());
            modifySaveReqDTO.setModifyUser(UserContext.getUserName());
            modifySaveReqDTO.setModifyAll(false);
            modifySaveReqDTO.setType(ConfigModifyTypeConstants.THREAD_POOL_MANAGER);
            configModificationVerifyServiceChoose.choose(modifySaveReqDTO.getType()).saveConfigModifyApplication(modifySaveReqDTO);
        }
    }

    @Override
    public void deletePool(ThreadPoolDelReqDTO requestParam) {
        configInfoMapper.delete(
                Wrappers.lambdaUpdate(ConfigAllInfo.class)
                        .eq(ConfigAllInfo::getTenantId, requestParam.getTenantId())
                        .eq(ConfigAllInfo::getItemId, requestParam.getItemId())
                        .eq(ConfigAllInfo::getTpId, requestParam.getTpId()));
        recordOperationLog(requestParam);
    }

    private void recordOperationLog(ThreadPoolDelReqDTO requestParam) {
        LogRecordInfo logRecordInfo = LogRecordInfo.builder()
                .bizKey(requestParam.getItemId() + "_" + requestParam.getTpId())
                .bizNo(requestParam.getItemId() + "_" + requestParam.getTpId())
                .operator(UserContext.getUserName())
                .action("删除线程池: " + requestParam.getTpId())
                .category("THREAD_POOL_DELETE")
                .detail(JSONUtil.toJSONString(requestParam))
                .createTime(new Date())
                .build();
        operationLogService.record(logRecordInfo);
    }

    @Override
    public void alarmEnable(String id, Integer isAlarm) {
        ConfigAllInfo configAllInfo = configInfoMapper.selectById(id);
        configAllInfo.setIsAlarm(isAlarm);
        configService.insertOrUpdate(null, false, configAllInfo);
    }
}
