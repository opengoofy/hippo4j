package cn.hippo4j.starter.remote;

import cn.hippo4j.common.web.base.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static cn.hippo4j.common.constant.Constants.HEALTH_CHECK_PATH;
import static cn.hippo4j.common.constant.Constants.UP;

/**
 * Server health check.
 *
 * @author chen.ma
 * @date 2021/12/8 20:16
 */
@Slf4j
@AllArgsConstructor
public class HttpScheduledHealthCheck extends AbstractHealthCheck {

    private final HttpAgent httpAgent;

    @Override
    protected boolean sendHealthCheck() {
        boolean healthStatus = false;
        try {
            Result healthResult = httpAgent.httpGetSimple(HEALTH_CHECK_PATH);
            if (healthResult != null && Objects.equals(healthResult.getData(), UP)) {
                healthStatus = true;
            }
        } catch (Throwable ex) {
            log.error("Failed to periodically check the health status of the server.", ex.getMessage());
        }

        return healthStatus;
    }

}
