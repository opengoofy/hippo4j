package cn.hippo4j.config.model.biz.alarm;

import cn.hippo4j.config.model.AlarmInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Alarm list resp dto.
 *
 * @author chen.ma
 * @date 2021/11/17 22:53
 */
@Data
@AllArgsConstructor
public class AlarmListRespDTO {

    /**
     * 线程池ID
     */
    private String threadPoolId;

    /**
     * 报警配置
     */
    private List<AlarmInfo> alarmNotifyList;

}
