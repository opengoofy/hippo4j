package io.dynamic.threadpool.server.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolQueryReqDTO;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolRespDTO;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;

import java.util.List;

/**
 * Thread Pool Service.
 *
 * @author chen.ma
 * @date 2021/6/30 21:26
 */
public interface ThreadPoolService {

    /**
     * 分页查询线程池
     *
     * @param reqDTO
     * @return
     */
    IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO);

    /**
     * 查询线程池配置
     *
     * @param reqDTO
     * @return
     */
    ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO);

    /**
     * 根据 ItemId 获取线程池配置
     *
     * @param itemId
     * @return
     */
    List<ThreadPoolRespDTO> getThreadPoolByItemId(String itemId);

    /**
     * 新增或修改线程池配置
     *
     * @param reqDTO
     */
    void saveOrUpdateThreadPoolConfig(ThreadPoolSaveOrUpdateReqDTO reqDTO);

}
