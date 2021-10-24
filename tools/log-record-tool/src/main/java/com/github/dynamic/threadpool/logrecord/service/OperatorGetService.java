package com.github.dynamic.threadpool.logrecord.service;

import com.github.dynamic.threadpool.logrecord.model.Operator;

/**
 * 获取操作人.
 *
 * @author chen.ma
 * @date 2021/10/23 22:46
 */
public interface OperatorGetService {

    /**
     * 获取操作人.
     *
     * @return
     */
    Operator getUser();

}
