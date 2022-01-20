package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.web.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

/**
 * Web thread pool handler choose.
 *
 * @author chen.ma
 * @date 2022/1/20 20:15
 */
@Slf4j
public class WebThreadPoolHandlerChoose {

    /**
     * Choose the web thread pool service bean.
     *
     * @return
     */
    public WebThreadPoolService choose() {
        WebThreadPoolService webThreadPoolService;
        try {
            webThreadPoolService = ApplicationContextHolder.getBean(WebThreadPoolService.class);
        } catch (Exception ex) {
            throw new ServiceException("Web thread pool service bean not found.", ex);
        }

        return webThreadPoolService;
    }

}
