package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.config.mapper.LogRecordMapper;
import cn.hippo4j.config.model.biz.log.LogRecordQueryReqDTO;
import cn.hippo4j.config.model.biz.log.LogRecordRespDTO;
import cn.hippo4j.config.service.biz.LogRecordBizService;
import cn.hippo4j.config.toolkit.BeanUtil;
import cn.hippo4j.tools.logrecord.model.LogRecordInfo;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志.
 *
 * @author chen.ma
 * @date 2021/11/17 21:50
 */
@Service
@AllArgsConstructor
public class LogRecordBizServiceImpl implements LogRecordBizService {

    private final LogRecordMapper logRecordMapper;

    @Override
    public IPage<LogRecordRespDTO> queryPage(LogRecordQueryReqDTO pageQuery) {
        LambdaQueryWrapper<LogRecordInfo> queryWrapper = Wrappers.lambdaQuery(LogRecordInfo.class)
                .eq(StrUtil.isNotBlank(pageQuery.getBizNo()), LogRecordInfo::getBizNo, pageQuery.getBizNo())
                .eq(StrUtil.isNotBlank(pageQuery.getCategory()), LogRecordInfo::getCategory, pageQuery.getCategory())
                .eq(StrUtil.isNotBlank(pageQuery.getOperator()), LogRecordInfo::getOperator, pageQuery.getOperator())
                .orderByDesc(LogRecordInfo::getCreateTime);
        IPage<LogRecordInfo> selectPage = logRecordMapper.selectPage((IPage)pageQuery, queryWrapper);
        return selectPage.convert(each -> BeanUtil.convert(each, LogRecordRespDTO.class));
    }

}
