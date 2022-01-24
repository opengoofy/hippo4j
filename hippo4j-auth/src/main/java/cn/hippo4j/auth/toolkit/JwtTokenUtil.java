package cn.hippo4j.auth.toolkit;

import cn.hippo4j.auth.constant.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static cn.hippo4j.common.constant.Constants.MAP_INITIAL_CAPACITY;

/**
 * Jwt token util.
 *
 * @author chen.ma
 * @date 2021/11/9 22:43
 */
public class JwtTokenUtil {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String SECRET = "SecretKey039245678901232039487623456783092349288901402967890140939827";
    public static final String ISS = "admin";

    /**
     * 角色的 Key
     */
    private static final String ROLE_CLAIMS = "rol";

    /**
     * 过期时间是 3600 秒, 既 24 小时
     */
    private static final long EXPIRATION = 86400L;

    /**
     * 选择了记住我之后的过期时间为 7 天
     */
    private static final long EXPIRATION_REMEMBER = 7 * EXPIRATION;

    /**
     * 创建 Token.
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
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setClaims(map)
                .setIssuer(ISS)
                .setSubject(id + Constants.SPLIT_COMMA + username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .compact();
    }

    /**
     * Token 中获取用户名.
     *
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        List<String> userInfo = Arrays.asList(getTokenBody(token).getSubject().split(Constants.SPLIT_COMMA));
        return userInfo.get(1);
    }

    /**
     * Token 中获取用户名.
     *
     * @param token
     * @return
     */
    public static Integer getUserId(String token) {
        List<String> userInfo = Arrays.asList(getTokenBody(token).getSubject().split(Constants.SPLIT_COMMA));
        return Integer.parseInt(userInfo.get(0));
    }

    /**
     * 获取用户角色.
     *
     * @param token
     * @return
     */
    public static String getUserRole(String token) {
        return (String) getTokenBody(token).get(ROLE_CLAIMS);
    }

    /**
     * 是否已过期.
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
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

}
