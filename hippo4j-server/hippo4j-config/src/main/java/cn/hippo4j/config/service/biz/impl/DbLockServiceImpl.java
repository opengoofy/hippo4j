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

import cn.hippo4j.config.mapper.LockMapper;
import cn.hippo4j.config.service.biz.LockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * db lock
 */
@Service
@AllArgsConstructor
public class DbLockServiceImpl implements LockService {

    private TransactionTemplate transactionTemplate;

    private final LockMapper lockMapper;

    @Override
    public <T> void tryLocK(String lockName, T t, Consumer<T> consumer) {
        transactionTemplate.execute(status -> {
            try {
                lockMapper.tryLock(lockName);
                consumer.accept(t);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public <T, R> R tryLocK(String lockName, T t, Function<T, R> function) {
        return transactionTemplate.execute(status -> {
            R r = null;
            try {
                lockMapper.tryLock(lockName);
                r = function.apply(t);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
            return r;
        });
    }

    @Override
    public <T, R> R tryLocK(String lockName, T t, Function<T, R> function, Supplier<R> errorResult) {
        return transactionTemplate.execute(status -> {
            R r;
            try {
                lockMapper.tryLock(lockName);
                r = function.apply(t);
            } catch (Exception e) {
                status.setRollbackOnly();
                return errorResult.get();
            }
            return r;
        });
    }
}
