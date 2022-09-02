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

import cn.hippo4j.config.model.biz.notify.NotifyListRespDTO;
import cn.hippo4j.config.model.biz.notify.NotifyQueryReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyRespDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * Notify service.
 */
public interface NotifyService {

    /**
     * List notify config.
     *
     * @param reqDTO
     * @return
     */
    List<NotifyListRespDTO> listNotifyConfig(NotifyQueryReqDTO reqDTO);

    /**
     * Query page.
     *
     * @param reqDTO
     * @return
     */
    IPage<NotifyRespDTO> queryPage(NotifyQueryReqDTO reqDTO);

    /**
     * Save.
     *
     * @param reqDTO
     */
    void save(NotifyReqDTO reqDTO);

    /**
     * Update.
     *
     * @param reqDTO
     */
    void update(NotifyReqDTO reqDTO);

    /**
     * Save or update.
     *
     * @param notifyUpdateIfExists
     * @param reqDTO
     */
    void saveOrUpdate(boolean notifyUpdateIfExists, NotifyReqDTO reqDTO);

    /**
     * Delete.
     *
     * @param reqDTO
     */
    void delete(NotifyReqDTO reqDTO);

    /**
     * Enable notify.
     *
     * @param id
     * @param status
     */
    void enableNotify(String id, Integer status);
}
