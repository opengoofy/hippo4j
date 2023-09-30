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

package cn.hippo4j.config.toolkit;

/**
 * Simple read write lock.
 */
public class SimpleReadWriteLock {

    private int status = 0;

    private static final int FREE_STATUS = -1;

    public synchronized boolean tryReadLock() {
        if (isWriteLocked()) {
            return false;
        } else {
            status++;
            return true;
        }
    }

    public synchronized void releaseReadLock() {
        status--;
    }

    public synchronized boolean tryWriteLock() {
        if (!isFree()) {
            return false;
        } else {
            status = FREE_STATUS;
            return true;
        }
    }

    public synchronized void releaseWriteLock() {
        status = 0;
    }

    private boolean isWriteLocked() {
        return status < 0;
    }

    private boolean isFree() {
        return status == 0;
    }
}
