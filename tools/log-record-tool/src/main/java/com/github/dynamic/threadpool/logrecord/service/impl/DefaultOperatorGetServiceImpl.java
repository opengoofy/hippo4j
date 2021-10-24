package com.github.dynamic.threadpool.logrecord.service.impl;

import com.github.dynamic.threadpool.logrecord.model.Operator;
import com.github.dynamic.threadpool.logrecord.service.OperatorGetService;

/**
 * 默认实现.
 *
 * @author chen.ma
 * @date 2021/10/24 17:58
 */
public class DefaultOperatorGetServiceImpl implements OperatorGetService {

    @Override
    public Operator getUser() {
        return new Operator("994924");
    }

}
