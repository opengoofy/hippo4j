package cn.hippo4j.config.service.handler;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.UserContext;
import cn.hippo4j.tools.logrecord.model.Operator;
import cn.hippo4j.tools.logrecord.service.OperatorGetService;
import org.springframework.stereotype.Component;

/**
 * Custom operator get service.
 *
 * @author chen.ma
 * @date 2021/11/28 17:57
 */
@Component
public class CustomOperatorGetServiceImpl implements OperatorGetService {

    @Override
    public Operator getUser() {
        String userName = UserContext.getUserName();
        userName = StringUtil.isEmpty(userName) ? "-" : userName;
        return new Operator(userName);
    }

}
