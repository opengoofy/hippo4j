package cn.hippo4j.tools.logrecord.service;

import cn.hippo4j.tools.logrecord.model.Operator;

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
