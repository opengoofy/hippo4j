package io.dynamic.threadpool.config.model.biz.threadpool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Thread Pool Query Req DTO.
 *
 * @author chen.ma
 * @date 2021/6/30 21:22
 */
@Data
public class ThreadPoolQueryReqDTO extends Page {

    private String namespace;

    private String itemId;

    private String tpId;

    private String tpName;

}
