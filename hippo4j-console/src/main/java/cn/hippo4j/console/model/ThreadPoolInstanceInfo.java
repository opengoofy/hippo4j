package cn.hippo4j.console.model;

import cn.hippo4j.config.model.ConfigAllInfo;
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
     * clientAddress
     */
    private String clientAddress;

    /**
     * identify
     */
    private String identify;

    /**
     * clientBasePath
     */
    private String clientBasePath;

    /**
     * active
     */
    private String active;

}
