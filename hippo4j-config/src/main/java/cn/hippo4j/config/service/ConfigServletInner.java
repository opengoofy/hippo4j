package cn.hippo4j.config.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static cn.hippo4j.common.constant.Constants.WEIGHT_CONFIGS;

/**
 * Config servlet inner.
 *
 * @author chen.ma
 * @date 2021/6/22 23:13
 */
@Service
@RequiredArgsConstructor
public class ConfigServletInner {

    @NonNull
    private final LongPollingService longPollingService;

    private final Cache<String, Long> deWeightCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .build();

    /**
     * 轮询配置.
     *
     * @param request
     * @param response
     * @param clientMd5Map
     * @param probeRequestSize
     * @return
     */
    public String doPollingConfig(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) {
        if (LongPollingService.isSupportLongPolling(request) && weightVerification(request)) {
            longPollingService.addLongPollingClient(request, response, clientMd5Map, probeRequestSize);
            return HttpServletResponse.SC_OK + "";
        }

        return HttpServletResponse.SC_OK + "";
    }

    /**
     * 校验复请求是否重.
     * <p>
     * 有使用者提出在公司环境部署时, 会出现相同的请求重复调用.
     * 此问题属于极其个别场景. 由于复现不出, 所以先以这种方式解决问题.
     *
     * @param request
     * @return
     */
    private boolean weightVerification(HttpServletRequest request) {
        String clientIdentify = request.getParameter(WEIGHT_CONFIGS);
        Long timeVal = deWeightCache.getIfPresent(clientIdentify);
        if (timeVal == null) {
            deWeightCache.put(clientIdentify, System.currentTimeMillis());
            return true;
        }

        return false;
    }

}
