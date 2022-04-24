package cn.hippo4j.tools.logrecord.service.impl;

import cn.hippo4j.tools.logrecord.service.FunctionService;
import cn.hippo4j.tools.logrecord.service.ParseFunction;
import lombok.AllArgsConstructor;

/**
 * 默认实现函数接口.
 *
 * @author chen.ma
 * @date 2021/10/24 21:54
 */
@AllArgsConstructor
public class DefaultFunctionServiceImpl implements FunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    @Override
    public String apply(String functionName, String value) {
        ParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            return value;
        }
        return function.apply(value);
    }

    @Override
    public boolean beforeFunction(String functionName) {
        return parseFunctionFactory.isBeforeFunction(functionName);
    }

}
