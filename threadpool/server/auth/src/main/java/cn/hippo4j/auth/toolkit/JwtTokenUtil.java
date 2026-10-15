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

package cn.hippo4j.auth.toolkit;

import cn.hippo4j.auth.constant.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static cn.hippo4j.common.constant.Constants.MAP_INITIAL_CAPACITY;

/**
 * JWT token util.
 */
public class JwtTokenUtil {

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    private static final Key SIGNING_KEY = loadSigningKey();

    public static Key getSigningKey() {
        return SIGNING_KEY;
    }

    private static Key loadSigningKey() {
        String secret = System.getProperty("hippo4j.core.auth.jwt-secret");
        if (secret == null) {
            secret = System.getenv("HIPPO4J_JWT_SECRET");
        }
        return parseSigningKey(secret);
    }

    static Key parseSigningKey(String secret) {
        String message = "JWT signing key must be Base64-encoded with at least 64 decoded bytes; "
                + "set HIPPO4J_JWT_SECRET or -Dhippo4j.core.auth.jwt-secret before starting the server";
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        byte[] key;
        try {
            key = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(message);
        }
        if (key.length < 64) {
            throw new IllegalArgumentException(message);
        }
        return new SecretKeySpec(key, "HmacSHA512");
    }

    public static final String ISS = "admin";

    /**
     * Character key.
     */
    private static final String ROLE_CLAIMS = "rol";

    /**
     * Expiration time is 3600 seconds, which is 24 hours.
     */
    private static final long EXPIRATION = 86400L;

    /**
     * 7 days after selecting Remember me.
     */
    private static final long EXPIRATION_REMEMBER = 7 * EXPIRATION;

    /**
     * Create Token.
     *
     * @param id
     * @param username
     * @param role
     * @param isRememberMe
     * @return
     */
    public static String createToken(Long id, String username, String role, boolean isRememberMe) {
        long expiration = isRememberMe ? EXPIRATION_REMEMBER : EXPIRATION;
        HashMap<String, Object> map = new HashMap(MAP_INITIAL_CAPACITY);
        map.put(ROLE_CLAIMS, role);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
                .setClaims(map)
                .setIssuer(ISS)
                .setSubject(id + Constants.SPLIT_COMMA + username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * Constants.STEP))
                .compact();
    }

    /**
     * Get the username from Token.
     *
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        List<String> userInfo = Arrays.asList(getTokenBody(token).getSubject().split(Constants.SPLIT_COMMA));
        return userInfo.get(1);
    }

    /**
     * Get the username from Token.
     *
     * @param token
     * @return
     */
    public static Integer getUserId(String token) {
        List<String> userInfo = Arrays.asList(getTokenBody(token).getSubject().split(Constants.SPLIT_COMMA));
        return Integer.parseInt(userInfo.get(0));
    }

    /**
     * Get user role.
     *
     * @param token
     * @return
     */
    public static String getUserRole(String token) {
        return (String) getTokenBody(token).get(ROLE_CLAIMS);
    }

    /**
     * Has it expired.
     *
     * @param token
     * @return
     */
    public static boolean isExpiration(String token) {
        try {
            return getTokenBody(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private static Claims getTokenBody(String token) {
        return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
    }
}
