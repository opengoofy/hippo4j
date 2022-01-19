package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolParameterInfo;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import java.util.concurrent.Executor;

/**
 * Undertow web thread pool handler.
 *
 * @author chen.ma
 * @date 2022/1/19 21:19
 */
@AllArgsConstructor
public class UndertowWebThreadPoolHandler extends AbstractWebThreadPoolService {

    private final ServletWebServerApplicationContext applicationContext;

    @Override
    protected Executor getWebThreadPoolByServer() {
        return null;
    }

    @Override
    public void updateWebThreadPool(PoolParameterInfo poolParameterInfo) {

    }

}
