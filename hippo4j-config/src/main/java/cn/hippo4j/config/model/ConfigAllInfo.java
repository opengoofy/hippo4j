package cn.hippo4j.config.model;

import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.toolkit.JSONUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * Config all info.
 *
 * @author chen.ma
 * @date 2021/6/20 15:14
 */
@Data
@TableName("config")
public class ConfigAllInfo extends ConfigInfo implements PoolParameter {

    private static final long serialVersionUID = -2417394244017463665L;

    /**
     * desc
     */
    @JsonIgnore
    @TableField(exist = false, fill = FieldFill.UPDATE)
    private String desc;

    /**
     * gmtCreate
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * gmtModified
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * delFlag
     */
    @TableLogic
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }

}
