package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.tenant.TenantQueryReqDTO;
import cn.hippo4j.config.model.biz.tenant.TenantRespDTO;
import cn.hippo4j.config.model.biz.tenant.TenantSaveReqDTO;
import cn.hippo4j.config.model.biz.tenant.TenantUpdateReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
     * @param id
     * @return
     */
    TenantRespDTO getTenantById(String id);

    /**
     * Get tenant by tenantId.
     *
     * @param tenantId
     * @return
     */
    TenantRespDTO getTenantByTenantId(String tenantId);

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
