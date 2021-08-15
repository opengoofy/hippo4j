package com.github.dynamic.threadpool.starter.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ThreadPool Alarm.
 *
 * @author chen.ma
 * @date 2021/8/15 13:13
 */
@Data
@AllArgsConstructor
public class ThreadPoolAlarm {

    /**
     * 是否报警
     */
    private Boolean isAlarm;

    /**
     * 活跃度报警
     */
    private Integer livenessAlarm;

    /**
     * 容量报警
     */
    private Integer capacityAlarm;

}
