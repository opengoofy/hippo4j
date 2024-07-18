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

import cn.hippo4j.auth.constant.Constants;
import cn.hippo4j.auth.filter.JWTAuthenticationFilter;
import cn.hippo4j.auth.filter.JWTAuthorizationFilter;
import cn.hippo4j.auth.filter.LdapAuthenticationFilter;
import cn.hippo4j.auth.security.JwtTokenManager;
import cn.hippo4j.auth.service.impl.UserDetailsServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.stream.Stream;

/**
 * Global security config.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class GlobalSecurityConfig {

    @Value("${hippo4j.core.auth.enabled:true}")
    private Boolean enableAuthentication;

    @Resource(name = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Resource(name = "ldapUserDetailsServiceImpl")
    private UserDetailsService ldapUserDetailsService;

    @Resource
    private JwtTokenManager tokenManager;

    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public UserDetailsService customUserService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Autowired
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationConfiguration = authenticationConfiguration;
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod(Constants.SPLIT_STAR);
        config.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .requestMatchers("/static/**", "/index.html", "/favicon.ico", "/avatar.jpg").permitAll()
                .requestMatchers("/doc.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs").anonymous()

                .and()
                .authenticationProvider(authenticationProvider())
                .authenticationProvider(ldapAuthenticationProvider())
                // .addFilter(new JWTAuthenticationFilter(authenticationManager())).authenticationProvider(authenticationProvider())
                .addFilter(JWTAuthenticationFilter()).authenticationProvider(ldapAuthenticationProvider())
                .addFilter(LdapAuthenticationFilter()).authenticationProvider(ldapAuthenticationProvider())
                .addFilter(new JWTAuthorizationFilter(tokenManager, authenticationManagerBean(authenticationConfiguration)))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        disableAuthenticationIfNeeded(http);
        http.authorizeRequests().anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        String[] ignores = Stream.of("/hippo4j/v1/cs/auth/users/apply/token/**").toArray(String[]::new);
        return (web) -> web.ignoring().requestMatchers(ignores);
    }

    private LdapAuthenticationFilter LdapAuthenticationFilter() throws Exception {
        LdapAuthenticationFilter filter = new LdapAuthenticationFilter(authenticationManagerBean(authenticationConfiguration));
        filter.setLdapUserDetailsService(ldapUserDetailsService);
        filter.setAuthenticationManager(authenticationManagerBean(authenticationConfiguration));
        return filter;
    }

    private JWTAuthenticationFilter JWTAuthenticationFilter() throws Exception {
        JWTAuthenticationFilter filter = new JWTAuthenticationFilter(authenticationManagerBean(authenticationConfiguration));
        filter.setLdapUserDetailsService(userDetailsService);
        filter.setAuthenticationManager(authenticationManagerBean(authenticationConfiguration));
        return filter;
    }

    /**
     * Injection DaoAuthenticationProvider
     * Modify hideUserNotFoundExceptions initial value to false
     * Solve the problem of UserNotFoundException don't throw
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider ldapAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(ldapUserDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    private void disableAuthenticationIfNeeded(HttpSecurity http) throws Exception {
        if (Boolean.FALSE.equals(enableAuthentication)) {
            http.authorizeRequests().requestMatchers("/hippo4j/v1/cs/**").permitAll();
        }
    }
}
