package cn.hippo4j.tools.logrecord.service.impl;

import cn.hippo4j.tools.logrecord.service.ParseFunction;

/**
 * 默认实现.
 *
 * @author chen.ma
 * @date 2021/10/24 17:57
 */
public class DefaultParseFunction implements ParseFunction {

    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return null;
    }

    @Override
    public String apply(String value) {
        return null;
    }

}
