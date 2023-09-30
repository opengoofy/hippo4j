/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.server.init;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.server.config.DataBaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

/**
 * Local datasource loader.
 *
 * <p> Quoted from org.apache.shenyu.admin.spring.LocalDataSourceLoader
 */
@Slf4j
@Component
@ConditionalOnExpression("'${hippo4j.database.dialect}' == 'mysql' or '${hippo4j.database.dialect}' == 'h2'")
public class LocalDataSourceLoader implements InstantiationAwareBeanPostProcessor {

    private static final String PRE_FIX = "file:";

    @Resource
    private DataBaseProperties dataBaseProperties;

    @Override
    public Object postProcessAfterInitialization(@NonNull final Object bean, final String beanName) throws BeansException {
        if ((bean instanceof DataSourceProperties) && dataBaseProperties.getInitEnable()) {
            this.init((DataSourceProperties) bean);
        }
        return bean;
    }

    private void init(final DataSourceProperties properties) {
        try {
            String jdbcUrl = properties.getUrl();
            // If jdbcUrl in the configuration file specifies the hippo4j database, it is removed,
            // because the hippo4j database does not need to be specified when executing the SQL file,
            // otherwise the hippo4j database will be disconnected when the hippo4j database does not exist
            if (Objects.equals(dataBaseProperties.getDialect(), "mysql")) {
                jdbcUrl = StringUtil.replace(properties.getUrl(), "/hippo4j_manager?", "?");
            }
            Connection connection = DriverManager.getConnection(jdbcUrl, properties.getUsername(), properties.getPassword());
            execute(connection, dataBaseProperties.getInitScript());
        } catch (Exception ex) {
            log.error("Datasource init error.", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void execute(final Connection conn, final String script) throws Exception {
        ScriptRunner runner = new ScriptRunner(conn);
        try {
            // Doesn't print logger
            runner.setLogWriter(null);
            runner.setAutoCommit(true);
            Resources.setCharset(StandardCharsets.UTF_8);
            String[] initScripts = StringUtil.split(script, ";");
            for (String sqlScript : initScripts) {
                if (sqlScript.startsWith(PRE_FIX)) {
                    String sqlFile = sqlScript.substring(PRE_FIX.length());
                    try (Reader fileReader = getResourceAsReader(sqlFile)) {
                        log.info("Execute hippo4j schema sql: {}", sqlFile);
                        runner.runScript(fileReader);
                    }
                } else {
                    try (Reader fileReader = Resources.getResourceAsReader(sqlScript)) {
                        log.info("Execute hippo4j schema sql: {}", sqlScript);
                        runner.runScript(fileReader);
                    }
                }
            }
        } finally {
            conn.close();
        }
    }

    private static Reader getResourceAsReader(final String resource) throws IOException {
        return new InputStreamReader(new FileInputStream(resource), StandardCharsets.UTF_8);
    }
}
