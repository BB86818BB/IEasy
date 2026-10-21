package cn.ieasy.framework.common.webauth.config.jwt;

import org.springframework.security.core.Authentication;

public interface TokenProvider {

    Authentication getAuthentication(final String token);

    boolean validateToken(final String authToken);

    String createToken(final Authentication authentication, final Boolean rememberMe);

    String getLoginUserToken();
}
