package cn.hippo4j.starter.alarm;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;

/**
 * 报警控制实体.
 *
 * @author chen.ma
 * @date 2021/10/28 22:15
 */
@Data
@Builder
public class AlarmControlDTO {

    /**
     * 线程池 Id
     */
    private String threadPool;

    /**
     * 推送报警平台
     */
    private String platform;

    /**
     * 推送报警类型
     */
    private MessageTypeEnum typeEnum;

    /**
     * 构建线程池报警标识
     *
     * @return
     */
    public String buildPk() {
        return StrUtil.builder(threadPool, "+", platform).toString();
    }

}
