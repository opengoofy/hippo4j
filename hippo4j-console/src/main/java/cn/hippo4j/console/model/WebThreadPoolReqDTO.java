package cn.hippo4j.console.model;

import lombok.Data;

import java.util.List;

/**
 * Web thread-pool req dto.
 */
@Data
public class WebThreadPoolReqDTO {

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Keep alive time
     */
    private Integer keepAliveTime;

    /**
     * Client address list
     */
    private List<String> clientAddressList;
}
