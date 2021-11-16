package cn.hippo4j.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Config servlet inner.
 *
 * @author chen.ma
 * @date 2021/6/22 23:13
 */
@Service
public class ConfigServletInner {

    @Autowired
    private LongPollingService longPollingService;

    public String doPollingConfig(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) {
        if (LongPollingService.isSupportLongPolling(request)) {
            longPollingService.addLongPollingClient(request, response, clientMd5Map, probeRequestSize);
            return HttpServletResponse.SC_OK + "";
        }
        return HttpServletResponse.SC_OK + "";
    }

}
