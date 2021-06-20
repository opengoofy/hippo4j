package io.dtp.server.service.impl;

import io.dtp.server.mapper.RowMapperManager;
import io.dtp.server.model.ConfigAllInfo;
import io.dtp.server.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 服务端配置接口实现
 *
 * @author chen.ma
 * @date 2021/6/20 15:21
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenant) {
        ConfigAllInfo configAllInfo = jdbcTemplate.queryForObject(
                "select * from config_info where tp_id = ? and item_id = ? and tenant_id = ?",
                new Object[]{tpId, itemId, tenant},
                RowMapperManager.CONFIG_ALL_INFO_ROW_MAPPER);

        return configAllInfo;
    }
}
