package cn.hippo4j.auth.model.biz.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Permission query page.
 *
 * @author chen.ma
 * @date 2021/10/30 21:47
 */
@Data
public class PermissionQueryPageReqDTO extends Page {

    public PermissionQueryPageReqDTO(long current, long size) {
        super(current, size);
    }

}
