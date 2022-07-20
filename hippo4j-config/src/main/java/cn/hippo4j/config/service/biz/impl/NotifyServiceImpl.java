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

import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.web.exception.ServiceException;
import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.config.mapper.NotifyInfoMapper;
import cn.hippo4j.config.model.NotifyInfo;
import cn.hippo4j.config.model.biz.notify.NotifyListRespDTO;
import cn.hippo4j.config.model.biz.notify.NotifyQueryReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyRespDTO;
import cn.hippo4j.config.service.biz.NotifyService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:02
 */
@Service
@AllArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotifyInfoMapper notifyInfoMapper;

    @Override
    public List<NotifyListRespDTO> listNotifyConfig(NotifyQueryReqDTO reqDTO) {
        List<NotifyListRespDTO> notifyListRespList = Lists.newArrayList();
        reqDTO.getGroupKeys().forEach(each -> {
            String[] parseKey = GroupKey.parseKey(each);
            List<NotifyInfo> notifyInfos = listNotifyCommon("CONFIG", parseKey);
            if (CollUtil.isNotEmpty(notifyInfos)) {
                notifyListRespList.add(new NotifyListRespDTO(StrUtil.builder(parseKey[0], "+", "CONFIG").toString(), notifyInfos));
            }

            List<NotifyInfo> alarmInfos = listNotifyCommon("ALARM", parseKey);
            if (CollUtil.isNotEmpty(alarmInfos)) {
                notifyListRespList.add(new NotifyListRespDTO(StrUtil.builder(parseKey[0], "+", "ALARM").toString(), alarmInfos));
            }
        });

        return notifyListRespList;
    }

    @Override
    public IPage<NotifyRespDTO> queryPage(NotifyQueryReqDTO reqDTO) {
        LambdaQueryWrapper<NotifyInfo> queryWrapper = Wrappers.lambdaQuery(NotifyInfo.class)
                .eq(StrUtil.isNotBlank(reqDTO.getTenantId()), NotifyInfo::getTenantId, reqDTO.getTenantId())
                .eq(StrUtil.isNotBlank(reqDTO.getItemId()), NotifyInfo::getItemId, reqDTO.getItemId())
                .eq(StrUtil.isNotBlank(reqDTO.getTpId()), NotifyInfo::getTpId, reqDTO.getTpId())
                .orderByDesc(NotifyInfo::getGmtCreate);

        IPage<NotifyInfo> resultPage = notifyInfoMapper.selectPage(reqDTO, queryWrapper);
        return resultPage.convert(each -> BeanUtil.convert(each, NotifyRespDTO.class));
    }

    @Override
    public void save(NotifyReqDTO reqDTO) {
        if (existNotify(reqDTO)) {
            throw new ServiceException("新增通知报警配置重复.");
        }

        notifyInfoMapper.insert(BeanUtil.convert(reqDTO, NotifyInfo.class));
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

    private boolean existNotify(NotifyReqDTO reqDTO) {
        LambdaQueryWrapper<NotifyInfo> queryWrapper = Wrappers.lambdaQuery(NotifyInfo.class)
                .eq(NotifyInfo::getTenantId, reqDTO.getTenantId())
                .eq(NotifyInfo::getItemId, reqDTO.getItemId())
                .eq(NotifyInfo::getTpId, reqDTO.getTpId())
                .eq(NotifyInfo::getPlatform, reqDTO.getPlatform())
                .eq(NotifyInfo::getType, reqDTO.getType());

        List<NotifyInfo> existNotifyInfos = notifyInfoMapper.selectList(queryWrapper);
        return CollUtil.isNotEmpty(existNotifyInfos);
    }

}
