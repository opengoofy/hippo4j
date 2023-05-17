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

import java.text.DecimalFormat;

/**
 * Byte conversion tool class
 */
public class ByteConvertUtil {

    public static final int KB_SIZE = 2 << 9;

    public static final int MB_SIZE = 2 << 19;

    public static final int GB_SIZE = 2 << 29;

    public static String getPrintSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (size < KB_SIZE) {
            return size + "B";
        } else if (size < MB_SIZE) {
            return df.format((double) size / KB_SIZE) + "KB";
        } else if (size < GB_SIZE) {
            return df.format((double) size / MB_SIZE) + "MB";
        } else {
            return df.format((double) size / GB_SIZE) + "GB";
        }
    }
}
