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

import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolDelReqDTO;

import java.util.List;

/**
 * Thread pool service.
 */
public interface ThreadPoolService {

    /**
     * Query thread pool page.
     *
     * @param reqDTO
     * @return
     */
    IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO);

    /**
     * Get thread pool.
     *
     * @param reqDTO
     * @return
     */
    ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO);

    /**
     * Get thread pool by item id.
     *
     * @param itemId
     * @return
     */
    List<ThreadPoolRespDTO> getThreadPoolByItemId(String itemId);

    /**
     * Save or update thread pool config.
     *
     * @param identify
     * @param reqDTO
     */
    void saveOrUpdateThreadPoolConfig(String identify, ThreadPoolSaveOrUpdateReqDTO reqDTO);

    /**
     * Delete pool.
     *
     * @param reqDTO
     */
    void deletePool(ThreadPoolDelReqDTO reqDTO);

    /**
     * Alarm enable.
     *
     * @param id
     * @param isAlarm
     */
    void alarmEnable(String id, Integer isAlarm);
}
