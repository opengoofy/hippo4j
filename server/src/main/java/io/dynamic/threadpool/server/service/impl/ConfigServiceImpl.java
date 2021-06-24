package io.dynamic.threadpool.server.service.impl;

import io.dynamic.threadpool.common.toolkit.ContentUtil;
import io.dynamic.threadpool.common.toolkit.Md5Util;
import io.dynamic.threadpool.server.mapper.RowMapperManager;
import io.dynamic.threadpool.server.model.ConfigAllInfo;
import io.dynamic.threadpool.server.service.ConfigService;
import io.dynamic.threadpool.server.toolkit.Md5ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * 服务端配置接口实现
 *
 * @author chen.ma
 * @date 2021/6/20 15:21
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String namespace) {
        ConfigAllInfo configAllInfo = jdbcTemplate.queryForObject(
                "select * from config_info where tp_id = ? and item_id = ? and tenant_id = ?",
                new Object[]{tpId, itemId, namespace},
                RowMapperManager.CONFIG_ALL_INFO_ROW_MAPPER);

        return configAllInfo;
    }

    @Override
    public void insertOrUpdate(ConfigAllInfo configAllInfo) {
        try {
            addConfigInfo(configAllInfo);
        } catch (Exception ex) {
            updateConfigInfo(configAllInfo);
        }
    }

    private Long addConfigInfo(ConfigAllInfo config) {
        final String sql = "INSERT INTO `config_info` (`tenant_id`, `item_id`, `tp_id`, `core_size`, `max_size`, `queue_type`, `capacity`, `keep_alive_time`, `content`, `md5`, `is_alarm`, `capacity_alarm`, `liveness_alarm`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, config.getNamespace());
                ps.setString(2, config.getItemId());
                ps.setString(3, config.getTpId());
                ps.setInt(4, config.getCoreSize());
                ps.setInt(5, config.getMaxSize());
                ps.setInt(6, config.getQueueType());
                ps.setInt(7, config.getCapacity());
                ps.setInt(8, config.getKeepAliveTime());
                ps.setString(9, config.getContent());
                ps.setString(10, Md5ConfigUtil.getTpContentMd5(config));
                ps.setInt(11, config.getIsAlarm());
                ps.setInt(12, config.getCapacityAlarm());
                ps.setInt(13, config.getLivenessAlarm());
                return ps;
            }, keyHolder);

            Number number = keyHolder.getKey();
            if (number == null) {
                throw new IllegalArgumentException("insert config_info fail");
            }
            return number.longValue();
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private void updateConfigInfo(ConfigAllInfo config) {
        try {
            String poolContent = ContentUtil.getPoolContent(config);
            String md5 = Md5Util.md5Hex(poolContent, "UTF-8");
            jdbcTemplate.update("update config_info set core_size = ?, max_size = ?, queue_type = ?, capacity = ?, keep_alive_time = ?, content = ?, md5 = ?, is_alarm = ?, capacity_alarm = ?, liveness_alarm = ? " +
                            "where tenant_id = ? and item_id = ? and tp_id = ?",
                    config.getCoreSize(), config.getMaxSize(), config.getQueueType(), config.getCapacity(), config.getKeepAliveTime(),
                    poolContent, md5, config.getIsAlarm(), config.getCapacityAlarm(),
                    config.getLivenessAlarm(), config.getNamespace(), config.getItemId(), config.getTpId());
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

}
