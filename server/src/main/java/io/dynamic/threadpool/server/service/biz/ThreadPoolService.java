package io.dynamic.threadpool.server.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolQueryReqDTO;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolRespDTO;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;

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
     * 新增或修改线程池配置
     *
     * @param reqDTO
     */
    void saveOrUpdateThreadPoolConfig(ThreadPoolSaveOrUpdateReqDTO reqDTO);

}
