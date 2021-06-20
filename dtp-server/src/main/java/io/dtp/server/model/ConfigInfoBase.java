package io.dtp.server.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础配置信息
 *
 * @author chen.ma
 * @date 2021/6/20 14:05
 */
@Data
public class ConfigInfoBase implements Serializable {

    private static final long serialVersionUID = -1892597426099265730L;

    /**
     * TpId
     */
    private String tpId;

    /**
     * ItemId
     */
    private String itemId;

    /**
     * 内容
     */
    private String content;

    /**
     * MD5
     */
    private String md5;
}
