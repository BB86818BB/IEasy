package cn.ieasy.framework.common.webauth.config.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    public final static String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private TokenProvider tokenProvider;

    private final List<GenericFilterBean> customAuthFilters = new ArrayList<>();

    public void addCustomAuthFilter(final GenericFilterBean filter) {
        this.customAuthFilters.add(filter);
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        final JWTFilter customFilter = new JWTFilter(this.tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        this.customAuthFilters.forEach(filter -> {
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        });
    }
}
