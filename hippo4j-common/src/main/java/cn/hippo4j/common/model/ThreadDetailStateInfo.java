package cn.hippo4j.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Thread detail state info.
 *
 * @author chen.ma
 * @date 2022/1/9 12:36
 */
@Data
@Accessors(chain = true)
public class ThreadDetailStateInfo {

    /**
     * threadId
     */
    private Long threadId;

    /**
     * threadName
     */
    private String threadName;

    /**
     * threadStatus
     */
    private String threadStatus;

    /**
     * threadStack
     */
    private List<String> threadStack;

}
