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

package cn.hippo4j.rpc.support;

import cn.hippo4j.rpc.exception.ConnectionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtil {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    /**
     * parsing hostname
     *
     * @param address address
     * @return InetAddress
     */
    public static InetSocketAddress getInetAddress(String address) {
        if (address.startsWith(HTTP)) {
            address = address.replaceFirst(HTTP, "");
        }

        if (address.startsWith(HTTPS)) {
            address = address.replaceFirst(HTTPS, "");
        }

        String[] addressStr = address.split(":");
        if (addressStr.length < 2) {
            throw new ConnectionException("Failed to connect to the server because the IP address is invalid. Procedure");
        }
        return InetSocketAddress.createUnresolved(addressStr[0], Integer.parseInt(addressStr[1]));
    }

}
