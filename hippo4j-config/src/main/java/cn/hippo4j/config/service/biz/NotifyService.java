package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.notify.NotifyListRespDTO;
import cn.hippo4j.config.model.biz.notify.NotifyQueryReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyReqDTO;
import cn.hippo4j.config.model.biz.notify.NotifyRespDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 通知管理.
 *
 * @author chen.ma
 * @date 2021/11/17 22:01
 */
public interface NotifyService {

    /**
     * 查询通知配置集合.
     *
     * @param reqDTO
     * @return
     */
    List<NotifyListRespDTO> listNotifyConfig(NotifyQueryReqDTO reqDTO);

    /**
     * 分页查询.
     *
     * @param reqDTO
     * @return
     */
    IPage<NotifyRespDTO> queryPage(NotifyQueryReqDTO reqDTO);

    /**
     * 新增通知配置.
     *
     * @param reqDTO
     */
    void save(NotifyReqDTO reqDTO);

    /**
     * 修改通知配置.
     *
     * @param reqDTO
     */
    void update(NotifyReqDTO reqDTO);

    /**
     * 删除通知配置.
     *
     * @param reqDTO
     */
    void delete(NotifyReqDTO reqDTO);

    /**
     * 启用停用通知.
     *
     * @param id
     * @param status
     */
    void enableNotify(String id, Integer status);

}
