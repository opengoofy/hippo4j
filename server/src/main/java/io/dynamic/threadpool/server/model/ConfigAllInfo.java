package io.dynamic.threadpool.server.model;

import io.dynamic.threadpool.common.model.PoolParameter;
import lombok.Data;

/**
 * 配置全部信息
 *
 * @author chen.ma
 * @date 2021/6/20 15:14
 */
@Data
public class ConfigAllInfo extends ConfigInfo implements PoolParameter {

    private static final long serialVersionUID = -2417394244017463665L;

    private String createUser;

    private String desc;

    private Long createTime;

    private Long modifyTime;
}
