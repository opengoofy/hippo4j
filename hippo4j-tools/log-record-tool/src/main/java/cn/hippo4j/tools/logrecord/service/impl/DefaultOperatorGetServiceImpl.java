package cn.hippo4j.tools.logrecord.service.impl;

import cn.hippo4j.tools.logrecord.model.Operator;
import cn.hippo4j.tools.logrecord.service.OperatorGetService;

/**
 * 默认实现.
 *
 * @author chen.ma
 * @date 2021/10/24 17:58
 */
public class DefaultOperatorGetServiceImpl implements OperatorGetService {

    @Override
    public Operator getUser() {
        return new Operator("-");
    }

}
