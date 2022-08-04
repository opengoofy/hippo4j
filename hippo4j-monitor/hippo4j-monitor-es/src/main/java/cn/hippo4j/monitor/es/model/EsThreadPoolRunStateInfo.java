package cn.hippo4j.monitor.es.model;

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Create by yuyang
 * 2022/8/4 17:17
 */
@Getter
@Setter
public class EsThreadPoolRunStateInfo extends ThreadPoolRunStateInfo{

    private String Id;

    private String applicationName;

}
