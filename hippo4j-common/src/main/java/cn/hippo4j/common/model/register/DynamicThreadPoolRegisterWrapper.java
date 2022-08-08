package cn.hippo4j.common.model.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dynamic thread-pool register wrapper.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicThreadPoolRegisterWrapper {

    /**
     * Tenant id
     */
    private String tenantId;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Update if exists
     */
    private Boolean updateIfExists = Boolean.TRUE;

    /**
     * Dynamic thread-pool register parameter
     */
    private DynamicThreadPoolRegisterParameter dynamicThreadPoolRegisterParameter;
}
