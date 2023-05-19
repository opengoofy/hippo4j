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

import cn.hippo4j.common.extension.enums.EnableEnum;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.mapper.NotifyInfoMapper;
import cn.hippo4j.config.model.NotifyInfo;
import cn.hippo4j.config.model.biz.notify.NotifyListRespDTO;
import cn.hippo4j.config.model.biz.notify.NotifyQueryReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyRespDTO;
import cn.hippo4j.config.service.biz.NotifyService;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.server.common.base.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Notify service impl.
 */
@Service
@AllArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotifyInfoMapper notifyInfoMapper;

    @Override
    public List<NotifyListRespDTO> listNotifyConfig(NotifyQueryReqDTO reqDTO) {
        List<NotifyListRespDTO> notifyListRespList = new ArrayList<>();
        reqDTO.getGroupKeys().forEach(each -> {
            String[] parseKey = GroupKey.parseKey(each);
            List<NotifyInfo> notifyInfos = listNotifyCommon("CONFIG", parseKey);
            if (CollectionUtil.isNotEmpty(notifyInfos)) {
                notifyListRespList.add(new NotifyListRespDTO(parseKey[0] + "+" + "CONFIG", notifyInfos));
            }
            List<NotifyInfo> alarmInfos = listNotifyCommon("ALARM", parseKey);
            if (CollectionUtil.isNotEmpty(alarmInfos)) {
                notifyListRespList.add(new NotifyListRespDTO(parseKey[0] + "+" + "ALARM", alarmInfos));
            }
        });
        return notifyListRespList;
    }

    @Override
    public IPage<NotifyRespDTO> queryPage(NotifyQueryReqDTO reqDTO) {
        LambdaQueryWrapper<NotifyInfo> queryWrapper = Wrappers.lambdaQuery(NotifyInfo.class)
                .eq(StringUtil.isNotBlank(reqDTO.getTenantId()), NotifyInfo::getTenantId, reqDTO.getTenantId())
                .eq(StringUtil.isNotBlank(reqDTO.getItemId()), NotifyInfo::getItemId, reqDTO.getItemId())
                .eq(StringUtil.isNotBlank(reqDTO.getTpId()), NotifyInfo::getTpId, reqDTO.getTpId())
                .orderByDesc(NotifyInfo::getGmtCreate);
        IPage<NotifyInfo> resultPage = notifyInfoMapper.selectPage(reqDTO, queryWrapper);
        return resultPage.convert(each -> {
            NotifyRespDTO convert = BeanUtil.convert(each, NotifyRespDTO.class);
            convert.setConfigType(Objects.equals("CONFIG", each.getType()));
            convert.setAlarmType(Objects.equals("ALARM", each.getType()));
            return convert;
        });
    }

    @Override
    public void save(NotifyReqDTO requestParam) {
        if (BooleanUtil.isTrue(requestParam.getConfigType())) {
            existNotify("CONFIG", requestParam);
        }
        if (BooleanUtil.isTrue(requestParam.getAlarmType())) {
            existNotify("ALARM", requestParam);
        }
        List<NotifyInfo> notifyInfos = new ArrayList<>();
        if (BooleanUtil.isTrue(requestParam.getAlarmType())) {
            NotifyInfo alarmNotifyInfo = BeanUtil.convert(requestParam, NotifyInfo.class);
            alarmNotifyInfo.setType("ALARM");
            notifyInfos.add(alarmNotifyInfo);
        }
        if (BooleanUtil.isTrue(requestParam.getConfigType())) {
            NotifyInfo configNotifyInfo = BeanUtil.convert(requestParam, NotifyInfo.class);
            configNotifyInfo.setType("CONFIG");
            notifyInfos.add(configNotifyInfo);
        }
        notifyInfos.forEach(each -> notifyInfoMapper.insert(each));
    }

    @Override
    public void update(NotifyReqDTO reqDTO) {
        NotifyInfo notifyInfo = BeanUtil.convert(reqDTO, NotifyInfo.class);
        LambdaUpdateWrapper<NotifyInfo> updateWrapper = Wrappers.lambdaUpdate(NotifyInfo.class)
                .eq(NotifyInfo::getId, reqDTO.getId());
        try {
            notifyInfoMapper.update(notifyInfo, updateWrapper);
        } catch (DuplicateKeyException ex) {
            throw new ServiceException("修改通知报警配置重复.");
        }
    }

    @Override
    public void saveOrUpdate(boolean notifyUpdateIfExists, NotifyReqDTO reqDTO) {
        try {
            existNotify(reqDTO.getType(), reqDTO);
            save(reqDTO);
        } catch (Exception ignored) {
            if (!notifyUpdateIfExists) {
                return;
            }
            LambdaQueryWrapper<NotifyInfo> queryWrapper = Wrappers.lambdaQuery(NotifyInfo.class)
                    .eq(NotifyInfo::getTenantId, reqDTO.getTenantId())
                    .eq(NotifyInfo::getItemId, reqDTO.getItemId())
                    .eq(NotifyInfo::getTpId, reqDTO.getTpId())
                    .eq(NotifyInfo::getPlatform, reqDTO.getPlatform())
                    .eq(NotifyInfo::getType, reqDTO.getType());
            List<NotifyInfo> notifyInfos = notifyInfoMapper.selectList(queryWrapper);
            notifyInfos.forEach(each -> update(reqDTO.setId(String.valueOf(each.getId()))));
        }
    }

    @Override
    public void delete(NotifyReqDTO reqDTO) {
        LambdaUpdateWrapper<NotifyInfo> updateWrapper = Wrappers.lambdaUpdate(NotifyInfo.class)
                .eq(NotifyInfo::getId, reqDTO.getId());
        notifyInfoMapper.delete(updateWrapper);
    }

    @Override
    public void enableNotify(String id, Integer status) {
        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setId(Long.parseLong(id));
        notifyInfo.setEnable(status);
        notifyInfoMapper.updateById(notifyInfo);
    }

    private List<NotifyInfo> listNotifyCommon(String type, String[] parseKey) {
        LambdaQueryWrapper<NotifyInfo> queryWrapper = Wrappers.lambdaQuery(NotifyInfo.class)
                .eq(NotifyInfo::getTenantId, parseKey[2])
                .eq(NotifyInfo::getItemId, parseKey[1])
                .eq(NotifyInfo::getTpId, parseKey[0])
                .eq(NotifyInfo::getEnable, EnableEnum.YES.getIntCode())
                .eq(NotifyInfo::getType, type);
        List<NotifyInfo> notifyInfos = notifyInfoMapper.selectList(queryWrapper);
        return notifyInfos;
    }

    private void existNotify(String type, NotifyReqDTO requestParam) {
        LambdaQueryWrapper<NotifyInfo> queryWrapper = Wrappers.lambdaQuery(NotifyInfo.class)
                .eq(NotifyInfo::getTenantId, requestParam.getTenantId())
                .eq(NotifyInfo::getItemId, requestParam.getItemId())
                .eq(NotifyInfo::getTpId, requestParam.getTpId())
                .eq(NotifyInfo::getPlatform, requestParam.getPlatform())
                .eq(NotifyInfo::getType, type);
        List<NotifyInfo> existNotifyInfos = notifyInfoMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(existNotifyInfos)) {
            throw new ServiceException(String.format("%s 新增通知报警配置重复", type));
        }
    }
}
