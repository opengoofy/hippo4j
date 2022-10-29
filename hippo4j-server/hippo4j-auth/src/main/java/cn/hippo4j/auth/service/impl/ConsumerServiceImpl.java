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

package cn.hippo4j.auth.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cn.hippo4j.auth.mapper.ConsumerMapper;
import cn.hippo4j.auth.mapper.ConsumerTokenMapper;
import cn.hippo4j.auth.model.ConsumerDO;
import cn.hippo4j.auth.model.ConsumerInfo;
import cn.hippo4j.auth.model.ConsumerTokenInfo;
import cn.hippo4j.auth.model.biz.conmuser.ConsumerDTO;
import cn.hippo4j.auth.security.JwtTokenManager;
import cn.hippo4j.auth.service.ConsumerService;
import cn.hippo4j.common.toolkit.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 *@author : wh
 *@date : 2022/10/29 13:10
 *@description:
 */
@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private static final Date DEFAULT_EXPIRES = new GregorianCalendar(2099, Calendar.JANUARY, 1).getTime();

    private final ConsumerMapper consumerMapper;

    private final ConsumerTokenMapper consumerTokenMapper;

    private final JwtTokenManager jwtTokenManager;

    @Override
    public ConsumerInfo createConsumer(ConsumerDTO consumerDTO) {
        ConsumerInfo consumerDO = new ConsumerInfo();
        consumerDO.setAppId(consumerDTO.getAppId());
        consumerDO.setName(consumerDTO.getName());
        consumerMapper.insert(consumerDO);
        return consumerDO;

    }

    @Override
    public ConsumerTokenInfo generateAndSaveConsumerToken(ConsumerInfo consumer) {
        String token = jwtTokenManager.createToken(consumer.getName(), DEFAULT_EXPIRES);
        ConsumerTokenInfo consumerTokenDO = new ConsumerTokenInfo();
        consumerTokenDO.setConsumerId(consumer.getId());
        consumerTokenDO.setToken(token);
        consumerTokenMapper.insert(consumerTokenDO);
        return consumerTokenDO;
    }

    @Override
    public Integer getConsumerId(String tokenHeader) {
        if (StringUtil.isEmpty(tokenHeader)) {
            return null;
        }
        LambdaUpdateWrapper<ConsumerTokenInfo> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ConsumerTokenInfo::getToken, tokenHeader);
        ConsumerTokenInfo consumerTokenDO = consumerTokenMapper.selectOne(wrapper);

        return ObjectUtils.isEmpty(consumerTokenDO) ? null : consumerTokenDO.getConsumerId();
    }
}
