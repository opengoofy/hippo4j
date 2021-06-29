package io.dynamic.threadpool.server.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.dynamic.threadpool.common.model.PoolParameter;
import lombok.Data;

import java.util.Date;

/**
 * 配置全部信息
 *
 * @author chen.ma
 * @date 2021/6/20 15:14
 */
@Data
@TableName("config_info")
public class ConfigAllInfo extends ConfigInfo implements PoolParameter {

    private static final long serialVersionUID = -2417394244017463665L;

    @JSONField(serialize = false)
    @TableField(exist = false, fill = FieldFill.UPDATE)
    private String desc;

    @JSONField(serialize = false)
    private Date gmtCreate;

    @JSONField(serialize = false)
    private Date gmtModified;
}
