package com.github.dynamic.threadpool.auth.model.biz.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * User query page.
 *
 * @author chen.ma
 * @date 2021/10/30 21:47
 */
@Data
public class UserQueryPageReqDTO extends Page {

    public UserQueryPageReqDTO(long current, long size) {
        super(current, size);
    }

}
