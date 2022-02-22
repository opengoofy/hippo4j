package cn.hippo4j.starter.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Config empty exception.
 *
 * @author chen.ma
 * @date 2021/11/28 21:58
 */
@Data
@AllArgsConstructor
public class ConfigEmptyException extends RuntimeException {

    /**
     * description
     */
    private String description;

    /**
     * action
     */
    private String action;

}
