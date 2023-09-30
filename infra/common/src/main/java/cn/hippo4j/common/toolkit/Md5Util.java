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

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.model.ThreadPoolParameter;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * MD5 util.
 * Refer to com.alibaba.nacos.common.util.Md5Utils:<br>
 */
public class Md5Util {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char XF0 = 0xF0;

    private static final char XF = 0x0F;

    private static final int DATA_ID_GROUP_ID_THREE_LEN = 3;

    private static final int DATA_ID_GROUP_ID_FOUR_LEN = 4;

    private static final int DISPLACEMENT = 4;

    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    });

    public static String md5Hex(byte[] bytes) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
            if (messageDigest != null) {
                return encodeHexString(messageDigest.digest(bytes));
            }
            throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
        } finally {
            MESSAGE_DIGEST_LOCAL.remove();
        }
    }

    public static String md5Hex(String value, String encode) {
        try {
            return md5Hex(value.getBytes(encode));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeHexString(byte[] bytes) {
        int l = bytes.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(XF0 & bytes[i]) >>> DISPLACEMENT];
            out[j++] = DIGITS_LOWER[XF & bytes[i]];
        }
        return new String(out);
    }

    public static String getTpContentMd5(ThreadPoolParameter config) {
        return Md5Util.md5Hex(ContentUtil.getPoolContent(config), "UTF-8");
    }

    public static String compareMd5ResultString(List<String> changedGroupKeys) throws IOException {
        if (null == changedGroupKeys) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String groupKey : changedGroupKeys) {
            String[] dataIdGroupId = GroupKey.parseKey(groupKey);
            sb.append(dataIdGroupId[0]);
            sb.append(Constants.WORD_SEPARATOR);
            sb.append(dataIdGroupId[1]);
            // if have tenant, then set it
            boolean b = (dataIdGroupId.length == DATA_ID_GROUP_ID_THREE_LEN
                    || dataIdGroupId.length == DATA_ID_GROUP_ID_FOUR_LEN)
                    && StringUtil.isNotBlank(dataIdGroupId[2]);
            if (b) {
                sb.append(Constants.WORD_SEPARATOR);
                sb.append(dataIdGroupId[2]);
            }
            sb.append(Constants.LINE_SEPARATOR);
        }
        // To encode WORD_SEPARATOR and LINE_SEPARATOR invisible characters, encoded value is %02 and %01
        return URLEncoder.encode(sb.toString(), "UTF-8");
    }
}
