package cn.hippo4j.common.notify.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Thread pool notify request.
 *
 * @author chen.ma
 * @date 2022/2/24 19:06
 */
@Data
@AllArgsConstructor
public class ThreadPoolNotifyRequest {

    /**
     * groupKeys
     */
    private List<String> groupKeys;

}
