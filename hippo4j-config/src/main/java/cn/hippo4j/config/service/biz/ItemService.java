package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.item.ItemRespDTO;
import cn.hippo4j.config.model.biz.item.ItemUpdateReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hippo4j.config.model.biz.item.ItemQueryReqDTO;
import cn.hippo4j.config.model.biz.item.ItemSaveReqDTO;

import java.util.List;

/**
 * Item service.
 *
 * @author chen.ma
 * @date 2021/6/29 21:57
 */
public interface ItemService {

    /**
     * Query item page.
     *
     * @param reqDTO
     * @return
     */
    IPage<ItemRespDTO> queryItemPage(ItemQueryReqDTO reqDTO);

    /**
     * Query item by id.
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    ItemRespDTO queryItemById(String tenantId, String itemId);

    /**
     * Query item.
     *
     * @param reqDTO
     * @return
     */
    List<ItemRespDTO> queryItem(ItemQueryReqDTO reqDTO);

    /**
     * Save item.
     *
     * @param reqDTO
     */
    void saveItem(ItemSaveReqDTO reqDTO);

    /**
     * Update item.
     *
     * @param reqDTO
     */
    void updateItem(ItemUpdateReqDTO reqDTO);

    /**
     * Delete item.
     *
     * @param tenantId
     * @param itemId
     */
    void deleteItem(String tenantId, String itemId);

}
