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

package cn.hippo4j.auth.service.impl;

import cn.hippo4j.auth.service.LdapService;
import cn.hippo4j.server.common.base.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
public class LdapServiceImpl implements LdapService {

    private final LdapTemplate ldapTemplate;

    @Value("${spring.ldap.object-class:}")
    private String objectClassName;

    @Value("${spring.ldap.account-attribute:}")
    private String accountAttribute;

    public LdapServiceImpl(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    @Override
    public void login(String username, String password) {
        try {
            ldapTemplate.authenticate(LdapQueryBuilder.query()
                    .where(accountAttribute).is(username)
                    .and(query().where("objectClass").is(objectClassName)), password);
            log.debug("{} ldap Login successful", username);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("ldap Can't find the user information ");
        } catch (AuthenticationException e) {
            log.debug("The user name or account error");
            throw new BadCredentialsException("The username or password error");
        } catch (UncategorizedLdapException e) {
            log.debug("Please check whether the user name password input");
            throw new BadCredentialsException("Please check whether the username password input");
        } catch (Exception e) {
            throw new ServiceException("Abnormal server");
        }
    }

}
