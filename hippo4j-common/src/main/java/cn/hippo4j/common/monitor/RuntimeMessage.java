package cn.hippo4j.common.monitor;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dynamic thread pool runtime data.
 *
 * @author chen.ma
 * @date 2021/12/6 18:18
 */
@Data
@NoArgsConstructor
public class RuntimeMessage extends AbstractMessage {

    /**
     * currentLoad
     */
    private String currentLoad;

    /**
     * peakLoad
     */
    private String peakLoad;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * The maximum number of threads that enter the thread pool at the same time
     */
    private Integer largestPoolSize;

    /**
     * activeSize
     */
    private String activeSize;

    /**
     * queueCapacity
     */
    private Integer queueCapacity;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * queueRemainingCapacity
     */
    private String queueRemainingCapacity;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * rejectCount
     */
    private Integer rejectCount;

    /**
     * timestamp
     */
    private Long timestamp;

}
