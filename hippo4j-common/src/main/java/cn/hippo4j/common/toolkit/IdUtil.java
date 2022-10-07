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

package cn.hippo4j.common.toolkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * id and uuid util，{@link UUID}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdUtil {

    /**
     * get a random UUID
     *
     * @return UUID
     */
    public static String randomUUID() {
        return toString(UUID.randomUUID(), false);
    }

    /**
     * get a simple random UUID
     *
     * @return a simple UUID
     */
    public static String simpleUUID() {
        return toString(UUID.randomUUID(), true);
    }

    /**
     * toString
     *
     * @param uuid UUID
     * @return UUID String
     */
    public static String toString(UUID uuid, boolean isSimple) {
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        if (isSimple) {
            return (digits(mostSigBits >> 32, 8) +
                    digits(mostSigBits >> 16, 4) +
                    digits(mostSigBits, 4) +
                    digits(leastSigBits >> 48, 4) +
                    digits(leastSigBits, 12));
        } else {
            return (digits(mostSigBits >> 32, 8) + "-" +
                    digits(mostSigBits >> 16, 4) + "-" +
                    digits(mostSigBits, 4) + "-" +
                    digits(leastSigBits >> 48, 4) + "-" +
                    digits(leastSigBits, 12));
        }
    }

    /**
     * Returns val represented by the specified number of hex digits. <br>
     * {@link UUID#digits(long, int)}
     *
     * @param val    value
     * @param digits position
     * @return hex value
     */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
}
