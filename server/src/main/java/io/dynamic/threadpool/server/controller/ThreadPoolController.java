package io.dynamic.threadpool.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.web.base.Result;
import io.dynamic.threadpool.common.web.base.Results;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolQueryReqDTO;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolRespDTO;
import io.dynamic.threadpool.server.model.biz.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import io.dynamic.threadpool.server.service.biz.ThreadPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Thread Pool Controller.
 *
 * @author chen.ma
 * @date 2021/6/30 20:54
 */
@RestController
@RequestMapping(Constants.BASE_PATH)
public class ThreadPoolController {

    @Autowired
    private ThreadPoolService threadPoolService;

    @PostMapping("/thread/pool/query/page")
    public Result<IPage<ThreadPoolRespDTO>> queryNameSpacePage(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(threadPoolService.queryThreadPoolPage(reqDTO));
    }

    @PostMapping("/thread/pool/query")
    public Result<ThreadPoolRespDTO> queryNameSpace(@RequestBody ThreadPoolQueryReqDTO reqDTO) {
        return Results.success(threadPoolService.getThreadPool(reqDTO));
    }

    @PostMapping("/thread/pool/save_or_update")
    public Result saveOrUpdateThreadPoolConfig(@RequestBody ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        threadPoolService.saveOrUpdateThreadPoolConfig(reqDTO);
        return Results.success();
    }

}
