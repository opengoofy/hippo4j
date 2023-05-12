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

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.mapper.OperationLogMapper;
import cn.hippo4j.config.model.LogRecordInfo;
import cn.hippo4j.config.model.biz.log.LogRecordQueryReqDTO;
import cn.hippo4j.config.model.biz.log.LogRecordRespDTO;
import cn.hippo4j.config.service.biz.OperationLogService;
import cn.hippo4j.common.toolkit.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Operation log service impl.
 */
@Service
@AllArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public IPage<LogRecordRespDTO> queryPage(LogRecordQueryReqDTO pageQuery) {
        LambdaQueryWrapper<LogRecordInfo> queryWrapper = Wrappers.lambdaQuery(LogRecordInfo.class)
                .eq(StringUtil.isNotBlank(pageQuery.getBizNo()), LogRecordInfo::getBizNo, pageQuery.getBizNo())
                .eq(StringUtil.isNotBlank(pageQuery.getCategory()), LogRecordInfo::getCategory, pageQuery.getCategory())
                .eq(StringUtil.isNotBlank(pageQuery.getOperator()), LogRecordInfo::getOperator, pageQuery.getOperator())
                .orderByDesc(LogRecordInfo::getCreateTime);
        IPage<LogRecordInfo> selectPage = operationLogMapper.selectPage(pageQuery, queryWrapper);
        return selectPage.convert(each -> BeanUtil.convert(each, LogRecordRespDTO.class));
    }

    @Override
    public void record(LogRecordInfo requestParam) {
        operationLogMapper.insert(requestParam);
    }
}
