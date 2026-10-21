package cn.ieasy.framework.common.webauth.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import cn.ieasy.framework.common.webauth.util.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;

/**
 * Filters incoming requests and installs a Spring Security principal if a
 * header corresponding to a valid user is found.
 */
public class JWTFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    private final TokenProvider tokenProvider;

    public JWTFilter(final TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain)
                    throws IOException, ServletException {
        if (SecurityUtils.isAuthenticated()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            try {
                final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
                final String jwt = this.resolveToken(httpServletRequest);
                if (StringUtils.hasText(jwt)) {
                    if (this.tokenProvider.validateToken(jwt)) {
                        final Authentication authentication = this.tokenProvider.getAuthentication(jwt);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (final ExpiredJwtException eje) {
                this.log.info("Security exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
                ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private String resolveToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            final String jwt = bearerToken.substring(7, bearerToken.length());
            return jwt;
        }
        return null;
    }
}
