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

package cn.hippo4j.config.model;

import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.config.toolkit.SimpleReadWriteLock;
import cn.hippo4j.config.toolkit.SingletonRepository;
import cn.hippo4j.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * Cache item.
 */
@Getter
@Setter
public class CacheItem {

    final String groupKey;

    private volatile String md5 = Constants.NULL;

    private volatile long lastModifiedTs;

    private volatile ConfigAllInfo configAllInfo;

    private SimpleReadWriteLock rwLock = new SimpleReadWriteLock();

    public CacheItem(String groupKey) {
        this.groupKey = SingletonRepository.DataIdGroupIdCache.getSingleton(groupKey);
    }

    public CacheItem(String groupKey, String md5) {
        this.md5 = md5;
        this.groupKey = SingletonRepository.DataIdGroupIdCache.getSingleton(groupKey);
    }

    public CacheItem(String groupKey, ConfigAllInfo configAllInfo) {
        this.configAllInfo = configAllInfo;
        this.md5 = Md5Util.getTpContentMd5(configAllInfo);
        this.groupKey = SingletonRepository.DataIdGroupIdCache.getSingleton(groupKey);
    }
}
