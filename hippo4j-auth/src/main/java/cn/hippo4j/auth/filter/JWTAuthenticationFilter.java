package cn.hippo4j.auth.filter;

import cn.hippo4j.auth.model.biz.user.JwtUser;
import cn.hippo4j.auth.model.biz.user.LoginUser;
import cn.hippo4j.auth.toolkit.JwtTokenUtil;
import cn.hippo4j.auth.toolkit.ReturnT;
import cn.hippo4j.common.web.base.Results;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static cn.hippo4j.auth.constant.Constants.SPLIT_COMMA;
import static cn.hippo4j.common.constant.Constants.BASE_PATH;
import static cn.hippo4j.common.constant.Constants.MAP_INITIAL_CAPACITY;

/**
 * JWT authentication filter.
 *
 * @author chen.ma
 * @date 2021/11/9 22:21
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final ThreadLocal<Integer> rememberMe = new ThreadLocal();

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl(BASE_PATH + "/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // 从输入流中获取到登录的信息
        try {
            LoginUser loginUser = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
            rememberMe.set(loginUser.getRememberMe());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList())
            );
        } catch (IOException e) {
            logger.error("attemptAuthentication error :{}", e);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        try {
            JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
            boolean isRemember = rememberMe.get() == 1;

            String role = "";
            Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                role = authority.getAuthority();
            }

            String token = JwtTokenUtil.createToken(jwtUser.getId(), jwtUser.getUsername(), role, isRemember);
            response.setHeader("token", JwtTokenUtil.TOKEN_PREFIX + token);
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> maps = new HashMap(MAP_INITIAL_CAPACITY);
            maps.put("data", JwtTokenUtil.TOKEN_PREFIX + token);
            maps.put("roles", role.split(SPLIT_COMMA));
            response.getWriter().write(JSONUtil.toJsonStr(Results.success(maps)));
        } finally {
            rememberMe.remove();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(new ReturnT(-1, "Server Error")));
    }

}
