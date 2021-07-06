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
     * 根据 Id 获取业务线
     *
     * @param namespaceId
     * @return
     */
    TenantRespDTO getNameSpaceById(String namespaceId);

    /**
     * 分页查询业务线
     *
     * @param reqDTO
     * @return
     */
    IPage<TenantRespDTO> queryNameSpacePage(TenantQueryReqDTO reqDTO);

    /**
     * 新增业务线
     *
     * @param reqDTO
     */
    void saveNameSpace(TenantSaveReqDTO reqDTO);

    /**
     * 修改业务线
     *
     * @param reqDTO
     */
    void updateNameSpace(TenantUpdateReqDTO reqDTO);

    /**
     * 根据 Id 删除业务线
     *
     * @param namespaceId
     */
    void deleteNameSpaceById(String namespaceId);

}
