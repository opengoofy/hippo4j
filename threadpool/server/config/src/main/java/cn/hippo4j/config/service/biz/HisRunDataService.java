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

package cn.hippo4j.config.service.biz;

import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.config.model.HisRunDataInfo;
import cn.hippo4j.config.model.biz.monitor.MonitorActiveRespDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorQueryReqDTO;
import cn.hippo4j.config.model.biz.monitor.MonitorRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * His run data service.
 */
public interface HisRunDataService extends IService<HisRunDataInfo> {

    /**
     * Query.
     *
     * @param reqDTO
     * @return
     */
    List<MonitorRespDTO> query(MonitorQueryReqDTO reqDTO);

    /**
     * Query active thread pool monitor.
     *
     * @param reqDTO
     * @return
     */
    MonitorActiveRespDTO queryInfoThreadPoolMonitor(MonitorQueryReqDTO reqDTO);

    /**
     * Query thread pool last task count.
     *
     * @param reqDTO
     * @return
     */
    MonitorRespDTO queryThreadPoolLastTaskCount(MonitorQueryReqDTO reqDTO);

    /**
     * Save.
     *
     * @param message
     */
    void save(Message message);

    /**
     * dataCollect.
     *
     * @param messageWrapper
     */
    Result<Void> dataCollect(MessageWrapper messageWrapper);
}
