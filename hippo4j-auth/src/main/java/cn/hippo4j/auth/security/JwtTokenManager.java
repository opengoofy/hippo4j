package cn.hippo4j.auth.security;

import cn.hutool.core.util.StrUtil;
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

import static cn.hippo4j.auth.constant.Constants.TOKEN_VALIDITY_IN_SECONDS;
import static cn.hippo4j.auth.toolkit.JwtTokenUtil.SECRET;
import static cn.hippo4j.common.constant.Constants.AUTHORITIES_KEY;

/**
 * Jwt token manager.
 *
 * @author chen.ma
 * @date 2021/12/20 20:36
 */
@Component
public class JwtTokenManager {

    public String createToken(String userName) {
        long now = System.currentTimeMillis();

        Date validity;
        validity = new Date(now + TOKEN_VALIDITY_IN_SECONDS * 1000L);

        Claims claims = Jwts.claims().setSubject(userName);
        return Jwts.builder().setClaims(claims).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }

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

        User principal = new User(claims.getSubject(), StrUtil.EMPTY, authorities);
        return new UsernamePasswordAuthenticationToken(principal, StrUtil.EMPTY, authorities);
    }

}
