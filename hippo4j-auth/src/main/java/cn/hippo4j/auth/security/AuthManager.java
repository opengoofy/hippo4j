package cn.hippo4j.auth.security;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.expression.AccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Auth manager
 *
 * @author chen.ma
 * @date 2021/12/20 20:34
 */
@Component
@AllArgsConstructor
public class AuthManager {

    private final JwtTokenManager jwtTokenManager;

    private final AuthenticationManager authenticationManager;

    /**
     * Resolve token from user.
     *
     * @param userName
     * @param rawPassword
     * @return
     * @throws AccessException
     */
    @SneakyThrows
    public String resolveTokenFromUser(String userName, String rawPassword) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, rawPassword);
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new AccessException("Unknown user.");
        }

        return jwtTokenManager.createToken(userName);
    }

}
