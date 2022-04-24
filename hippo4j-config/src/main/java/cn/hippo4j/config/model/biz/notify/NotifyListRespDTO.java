package cn.hippo4j.config.model.biz.notify;

import cn.hippo4j.config.model.NotifyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Notify list resp dto.
 *
 * @author chen.ma
 * @date 2021/11/17 22:53
 */
@Data
@AllArgsConstructor
public class NotifyListRespDTO {

    /**
     * 通知 Key
     */
    private String notifyKey;

    /**
     * 通知配置
     */
    private List<NotifyInfo> notifyList;

}
