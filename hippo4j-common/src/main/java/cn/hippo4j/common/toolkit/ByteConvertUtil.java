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

/**
 * 字节转换工具类.
 *
 * @author chen.ma
 * @date 2021/11/20 12:21
 */
public class ByteConvertUtil {

    /**
     * 字节转换.
     *
     * @param size
     * @return
     */
    public static String getPrintSize(long size) {
        long covertNum = 1024;
        if (size < covertNum) {
            return size + "B";
        } else {
            size = size / covertNum;
        }
        if (size < covertNum) {
            return size + "KB";
        } else {
            size = size / covertNum;
        }
        if (size < covertNum) {
            size = size * 100;
            return (size / 100) + "." + (size % 100) + "MB";
        } else {
            size = size * 100 / covertNum;
            return (size / 100) + "." + (size % 100) + "GB";
        }
    }

}
