package cn.hippo4j.common.notify;

import lombok.Data;

import java.util.List;

/**
 * Thread pool notify DTO.
 *
 * @author chen.ma
 * @date 2022/2/24 19:08
 */
@Data
public class ThreadPoolNotifyDTO {

    /**
     * notifyKey
     */
    private String notifyKey;

    /**
     * notifyList
     */
    private List<NotifyConfigDTO> notifyList;

}
