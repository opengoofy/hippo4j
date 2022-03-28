package cn.hippo4j.tools.logrecord.model;

import lombok.*;

/**
 * 方法执行结果.
 *
 * @author chen.ma
 * @date 2021/10/24 21:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class MethodExecuteResult {

    /**
     * 是否成功
     */
    @NonNull
    private boolean success;

    /**
     * 异常
     */
    private Throwable throwable;

    /**
     * 错误日志
     */
    private String errorMsg;

}
