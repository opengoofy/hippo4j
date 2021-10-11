package com.github.dynamic.threadpool.config.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantQueryReqDTO;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantRespDTO;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantSaveReqDTO;
import com.github.dynamic.threadpool.config.model.biz.tenant.TenantUpdateReqDTO;

/**
 * Tenant service.
 *
 * @author chen.ma
 * @date 2021/6/29 21:59
 */
public interface TenantService {

    /**
     * Get tenant by id.
     *
     * @param tenantIdId
     * @return
     */
    TenantRespDTO getTenantById(String tenantIdId);

    /**
     * Query tenant page.
     *
     * @param reqDTO
     * @return
     */
    IPage<TenantRespDTO> queryTenantPage(TenantQueryReqDTO reqDTO);

    /**
     * Save tenant.
     *
     * @param reqDTO
     */
    void saveTenant(TenantSaveReqDTO reqDTO);

    /**
     * Update tenant.
     *
     * @param reqDTO
     */
    void updateTenant(TenantUpdateReqDTO reqDTO);

    /**
     * Delete tenant by id.
     *
     * @param tenantId
     */
    void deleteTenantById(String tenantId);

}
