package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.threadpool.ThreadPoolQueryReqDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolRespDTO;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.config.model.biz.threadpool.ThreadPoolDelReqDTO;

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

    /**
     * Alarm enable.
     *
     * @param id
     * @param isAlarm
     */
    void alarmEnable(String id, Integer isAlarm);

}
