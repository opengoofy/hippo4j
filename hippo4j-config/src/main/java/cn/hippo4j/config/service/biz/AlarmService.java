package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.alarm.AlarmListRespDTO;
import cn.hippo4j.config.model.biz.alarm.AlarmQueryReqDTO;

import java.util.List;

/**
 * 报警管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:01
 */
public interface AlarmService {

    /**
     * 查询报警配置集合.
     *
     * @param reqDTO
     * @return
     */
    List<AlarmListRespDTO> listAlarmConfig(AlarmQueryReqDTO reqDTO);

}
