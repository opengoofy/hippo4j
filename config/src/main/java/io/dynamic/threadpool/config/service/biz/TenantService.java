package io.dynamic.threadpool.config.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dynamic.threadpool.config.model.biz.tenant.TenantQueryReqDTO;
import io.dynamic.threadpool.config.model.biz.tenant.TenantRespDTO;
import io.dynamic.threadpool.config.model.biz.tenant.TenantSaveReqDTO;
import io.dynamic.threadpool.config.model.biz.tenant.TenantUpdateReqDTO;

/**
 * 业务接口
 *
 * @author chen.ma
 * @date 2021/6/29 21:59
 */
public interface TenantService {

    /**
     * 根据 Id 获取租户
     *
     * @param tenantIdId
     * @return
     */
    TenantRespDTO getTenantById(String tenantIdId);

    /**
     * 分页查询租户
     *
     * @param reqDTO
     * @return
     */
    IPage<TenantRespDTO> queryTenantPage(TenantQueryReqDTO reqDTO);

    /**
     * 新增租户
     *
     * @param reqDTO
     */
    void saveTenant(TenantSaveReqDTO reqDTO);

    /**
     * 修改租户
     *
     * @param reqDTO
     */
    void updateTenant(TenantUpdateReqDTO reqDTO);

    /**
     * 根据 Id 删除租户
     *
     * @param tenantId
     */
    void deleteTenantById(String tenantId);

}
