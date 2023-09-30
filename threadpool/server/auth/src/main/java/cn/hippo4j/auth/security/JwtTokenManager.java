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

package cn.hippo4j.auth.security;

import cn.hippo4j.common.toolkit.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static cn.hippo4j.auth.constant.Constants.STEP;
import static cn.hippo4j.auth.constant.Constants.TOKEN_VALIDITY_IN_SECONDS;
import static cn.hippo4j.auth.toolkit.JwtTokenUtil.SECRET;
import static cn.hippo4j.common.constant.Constants.AUTHORITIES_KEY;

/**
 * Jwt token manager.
 */
@Component
public class JwtTokenManager {

    /**
     * Create token.
     *
     * @param userName user-name
     * @return new token
     */
    public String createToken(String userName) {
        long now = System.currentTimeMillis();
        Date validity;
        validity = new Date(now + TOKEN_VALIDITY_IN_SECONDS * STEP);
        Claims claims = Jwts.claims().setSubject(userName);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * Validate token.
     *
     * @param token token
     */
    public void validateToken(String token) {
        Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
    }

    /**
     * Get auth Info.
     *
     * @param token token
     * @return auth info
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        List<GrantedAuthority> authorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList((String) claims.get(AUTHORITIES_KEY));
        User principal = new User(claims.getSubject(), StringUtil.EMPTY, authorities);
        return new UsernamePasswordAuthenticationToken(principal, StringUtil.EMPTY, authorities);
    }
}
