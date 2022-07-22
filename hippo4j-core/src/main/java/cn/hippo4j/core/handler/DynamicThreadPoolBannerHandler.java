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

package cn.hippo4j.core.handler;

import cn.hippo4j.core.config.BootstrapPropertiesInterface;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * Dynamic thread-pool print banner.
 *
 * @author chen.ma
 * @date 2021/6/20 16:34
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicThreadPoolBannerHandler implements InitializingBean {

    private final BootstrapPropertiesInterface properties;

    private final String DYNAMIC_THREAD_POOL = " :: Dynamic ThreadPool :: ";

    private final String HIPPO4J_GITHUB = "GitHub:  https://github.com/opengoofy/hippo4j";

    private final String HIPPO4J_SITE = "Site:    https://hippo4j.cn";

    private final int STRAP_LINE_SIZE = 50;

    @Override
    public void afterPropertiesSet() {
        printBanner();
    }

    private void printBanner() {
        String banner = "___                       _      _____ ___ \n" +
                "|   \\ _  _ _ _  __ _ _ __ (_)__  |_   _| _ \\\n" +
                "| |) | || | ' \\/ _` | '  \\| / _|   | | |  _/\n" +
                "|___/ \\_, |_||_\\__,_|_|_|_|_\\__|   |_| |_|  \n" +
                "      |__/                                  \n";
        if (properties.getBanner()) {
            String version = getVersion();
            version = (version != null) ? " (v" + version + ")" : "no version.";
            StringBuilder padding = new StringBuilder();
            while (padding.length() < STRAP_LINE_SIZE - (version.length() + DYNAMIC_THREAD_POOL.length())) {
                padding.append(" ");
            }
            System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                    padding.toString(), AnsiStyle.FAINT, version, "\n\n", HIPPO4J_GITHUB, "\n", HIPPO4J_SITE, "\n"));

        }
    }

    public static String getVersion() {
        final Package pkg = DynamicThreadPoolBannerHandler.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : "";
    }
}
