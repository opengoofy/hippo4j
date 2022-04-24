package cn.hippo4j.auth.model.biz.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Role query page.
 *
 * @author chen.ma
 * @date 2021/10/30 21:47
 */
@Data
public class RoleQueryPageReqDTO extends Page {

    public RoleQueryPageReqDTO(long current, long size) {
        super(current, size);
    }

}
