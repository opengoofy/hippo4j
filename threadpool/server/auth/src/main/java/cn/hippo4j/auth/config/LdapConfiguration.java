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

package cn.hippo4j.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Ldap config.
 */
@Configuration
public class LdapConfiguration {

    private LdapTemplate ldapTemplate;

    @Value("${spring.ldap.urls:}")
    private String url;

    @Value("${spring.ldap.base:}")
    private String base;

    @Value("${spring.ldap.embedded.credential.username:}")
    private String username;

    @Value("${spring.ldap.embedded.credential.password:}")
    private String password;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        Map<String, Object> config = new HashMap<>(10);
        contextSource.setUrl(url);
        contextSource.setBase(base);
        contextSource.setUserDn(username);
        contextSource.setPassword(password);
        // fix garbled characters
        config.put("java.naming.ldap.attributes.binary", "objectGUID");

        contextSource.setPooled(true);
        contextSource.setBaseEnvironmentProperties(config);
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        if (null == ldapTemplate) {
            ldapTemplate = new LdapTemplate(contextSource());
        }
        return ldapTemplate;
    }
}
