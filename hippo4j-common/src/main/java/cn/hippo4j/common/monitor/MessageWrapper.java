package cn.hippo4j.common.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Message wrapper.
 *
 * @author chen.ma
 * @date 2021/12/7 22:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper extends AbstractMessage implements Serializable {

    /**
     * messageObj
     */
    private Object messageObj;

}
