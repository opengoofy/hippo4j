package cn.hippo4j.auth.model.biz.user;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Jwt user.
 *
 * @author chen.ma
 * @date 2021/11/9 22:34
 */
@Data
public class JwtUser implements UserDetails {

    /**
     * id
     */
    private Long id;

    /**
     * userName
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * authorities
     */
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
