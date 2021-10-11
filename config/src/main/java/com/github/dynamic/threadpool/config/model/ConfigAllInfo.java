package com.github.dynamic.threadpool.config.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.dynamic.threadpool.common.model.PoolParameter;
import lombok.Data;

import java.util.Date;

/**
 * Config all info.
 *
 * @author chen.ma
 * @date 2021/6/20 15:14
 */
@Data
@TableName("config_info")
public class ConfigAllInfo extends ConfigInfo implements PoolParameter {

    private static final long serialVersionUID = -2417394244017463665L;

    /**
     * desc
     */
    @JSONField(serialize = false)
    @TableField(exist = false, fill = FieldFill.UPDATE)
    private String desc;

    /**
     * gmtCreate
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * gmtModified
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * delFlag
     */
    @TableLogic
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

}
