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

package cn.hippo4j.core.executor.handler;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.boot.info.BuildProperties;

/**
 * Dynamic thread-pool print banner.
 */
@Slf4j
public class DynamicThreadPoolBannerHandler implements InitializingBean {

    private final BootstrapPropertiesInterface properties;

    private static final String DYNAMIC_THREAD_POOL = " :: Dynamic ThreadPool :: ";

    private static final String HIPPO4J_GITHUB = "GitHub:  https://github.com/opengoofy/hippo4j";

    private static final String HIPPO4J_SITE = "Site:    https://www.hippo4j.cn";

    private static final int STRAP_LINE_SIZE = 50;

    private final String version;

    public DynamicThreadPoolBannerHandler(BootstrapPropertiesInterface properties, BuildProperties buildProperties) {
        this.properties = properties;
        this.version = buildProperties != null ? buildProperties.getVersion() : "";
    }

    @Override
    public void afterPropertiesSet() {
        printBanner();
    }

    /**
     * Print banner.
     */
    private void printBanner() {
        String banner = "  __     __                       ___ ___   __ \n"
                + " |  |--.|__|.-----..-----..-----.|   |   | |__|\n"
                + " |     ||  ||  _  ||  _  ||  _  ||   |   | |  |\n"
                + " |__|__||__||   __||   __||_____||____   | |  |\n"
                + "            |__|   |__|              |:  ||___|\n"
                + "                                     `---'     \n";
        if (Boolean.TRUE.equals(properties.getBanner())) {
            String bannerVersion = StringUtil.isNotEmpty(version) ? " (v" + version + ")" : "no version.";
            StringBuilder padding = new StringBuilder();
            while (padding.length() < STRAP_LINE_SIZE - (bannerVersion.length() + DYNAMIC_THREAD_POOL.length())) {
                padding.append(" ");
            }
            System.out.println(AnsiOutput.toString(banner, AnsiColor.GREEN, DYNAMIC_THREAD_POOL, AnsiColor.DEFAULT,
                    padding.toString(), AnsiStyle.FAINT, bannerVersion, "\n\n", HIPPO4J_GITHUB, "\n", HIPPO4J_SITE, "\n"));

        }
    }

    /**
     * Get version.
     *
     * @return hippo4j version
     */
    public String getVersion() {
        return version;
    }
}
