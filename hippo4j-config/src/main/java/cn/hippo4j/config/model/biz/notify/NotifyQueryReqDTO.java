package cn.hippo4j.config.model.biz.notify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * Notify query req dto.
 *
 * @author chen.ma
 * @date 2021/11/17 22:52
 */
@Data
public class NotifyQueryReqDTO extends Page {

    /**
     * groupKeys
     */
    private List<String> groupKeys;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 线程池id
     */
    private String tpId;

}
