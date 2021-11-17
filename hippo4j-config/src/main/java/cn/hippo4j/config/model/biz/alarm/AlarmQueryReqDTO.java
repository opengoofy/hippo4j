package cn.hippo4j.config.model.biz.alarm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * Alarm query req dto.
 *
 * @author chen.ma
 * @date 2021/11/17 22:52
 */
@Data
public class AlarmQueryReqDTO extends Page {

    /**
     * groupKeys
     */
    private List<String> groupKeys;

}
