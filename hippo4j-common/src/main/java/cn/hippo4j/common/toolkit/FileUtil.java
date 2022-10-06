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

import cn.hippo4j.common.web.exception.IllegalException;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * File util;
 */
public class FileUtil {

    private static final int ERROR_CODE = -1;

    @SneakyThrows
    public static String readUtf8String(String path) {
        String resultReadStr;
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (
                InputStream inputStream = classPathResource.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
            int result = bis.read();
            while (result != ERROR_CODE) {
                buf.write((byte) result);
                result = bis.read();
            }
            resultReadStr = buf.toString("UTF-8");
        }
        return resultReadStr;
    }

    public static List<String> readLines(String path, Charset charset) {
        List<String> strList = new ArrayList<>();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        ClassPathResource classPathResource = new ClassPathResource(path);
        try {
            inputStreamReader = new InputStreamReader(classPathResource.getInputStream(), charset);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalException("file read error");
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    throw new IllegalException("file read error");
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new IllegalException("file read error");
                }
            }
        }
        return strList;
    }
}
