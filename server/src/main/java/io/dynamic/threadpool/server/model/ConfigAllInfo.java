package io.dynamic.threadpool.server.model;

import com.alibaba.fastjson.annotation.JSONField;
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

    @JSONField(serialize = false)
    private String createUser;

    @JSONField(serialize = false)
    private String desc;

    @JSONField(serialize = false)
    private Long createTime;

    @JSONField(serialize = false)
    private Long modifyTime;
}
