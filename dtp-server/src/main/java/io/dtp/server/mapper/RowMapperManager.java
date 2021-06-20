package io.dtp.server.mapper;

import io.dtp.server.model.ConfigAllInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ConfigAllInfoRowMapper
 *
 * @author chen.ma
 * @date 2021/6/20 15:57
 */

public final class RowMapperManager {

    public static final ConfigAllInfoRowMapper CONFIG_ALL_INFO_ROW_MAPPER = new ConfigAllInfoRowMapper();

    public static class ConfigAllInfoRowMapper implements RowMapper<ConfigAllInfo> {

        @Override
        public ConfigAllInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConfigAllInfo configAllInfo = new ConfigAllInfo();
            configAllInfo.setTpId(rs.getString("tp_id"));
            configAllInfo.setItemId(rs.getString("item_id"));
            configAllInfo.setTenant(rs.getString("tenant_id"));
            configAllInfo.setContent(rs.getString("content"));
            configAllInfo.setMd5(rs.getString("md5"));
            configAllInfo.setCreateTime(rs.getTimestamp("gmt_modified").getTime());
            configAllInfo.setModifyTime(rs.getTimestamp("gmt_modified").getTime());
            return configAllInfo;
        }
    }

}

