package com.github.dynamic.threadpool.console.model;

import com.github.dynamic.threadpool.config.model.ConfigAllInfo;
import lombok.Data;

/**
 * ThreadPool instance info.
 *
 * @author chen.ma
 * @date 2021/11/11 23:39
 */
@Data
public class ThreadPoolInstanceInfo extends ConfigAllInfo {

    /**
     * identify
     */
    private String identify;

    /**
     * clientBasePath
     */
    private String clientBasePath;

}
