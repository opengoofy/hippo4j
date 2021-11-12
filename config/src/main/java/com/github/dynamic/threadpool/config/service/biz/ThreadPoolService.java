package com.github.dynamic.threadpool.config.service.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolDelReqDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolRespDTO;
import com.github.dynamic.threadpool.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;

import java.util.List;

/**
 * Thread pool service.
 *
 * @author chen.ma
 * @date 2021/6/30 21:26
 */
public interface ThreadPoolService {

    /**
     * Query thread pool page.
     *
     * @param reqDTO
     * @return
     */
    IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO);

    /**
     * Get thread pool.
     *
     * @param reqDTO
     * @return
     */
    ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO);

    /**
     * Get thread pool by item id.
     *
     * @param itemId
     * @return
     */
    List<ThreadPoolRespDTO> getThreadPoolByItemId(String itemId);

    /**
     * Save or update thread pool config.
     *
     * @param identify
     * @param reqDTO
     */
    void saveOrUpdateThreadPoolConfig(String identify, ThreadPoolSaveOrUpdateReqDTO reqDTO);

    /**
     * Delete pool.
     *
     * @param reqDTO
     */
    void deletePool(ThreadPoolDelReqDTO reqDTO);

}
