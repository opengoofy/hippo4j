package com.github.dynamic.threadpool.logrecord.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作人.
 *
 * @author chen.ma
 * @date 2021/10/24 21:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operator {

    /**
     * 操作人 Id
     */
    private String operatorId;

}
