package cn.hippo4j.config.model.biz.threadpool;

import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import lombok.Data;

/**
 * config modify verify dto
 */
@Data
public class ConfigModifyVerifyReqDTO {

    /**
     * his config verify id
     */
    private Long id;

    /**
     * config verify type
     */
    private Integer type;

    /**
     * weather accept config modification
     */
    private Boolean accept;

    /**
     * thread pool parameter info
     */
    private ThreadPoolParameterInfo threadPoolParameterInfo;
}
