package cn.ieasy.framework.common.webauth.config.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.ieasy.framework.common.web.utils.json.JsonUtils;
import cn.ieasy.framework.common.webauth.access.JWTAuthenticationToken;
import cn.ieasy.framework.common.webauth.access.SimpleGrantedAuthority;
import cn.ieasy.framework.common.webauth.config.jwt.DefaultTokenProvider.TokenProviderCondition;
import cn.ieasy.framework.common.webauth.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
@Conditional(TokenProviderCondition.class)
public class DefaultTokenProvider implements TokenProvider {

    private final Logger log = LoggerFactory.getLogger(DefaultTokenProvider.class);

    public static final TypeReference<HashMap<String, String>> MAP_TYPE = new TypeReference<HashMap<String, String>>() {
    };
    private static final String AUTHORITIES_KEY = "auth";
    private static final String USERDETAILS_KEY = "details";
    @Value("${security.authentication.jwt.secret}")
    private String secretKey;
    @Value("${security.authentication.jwt.tokenValidityInSeconds: 1800}")
    private long tokenValidityInSeconds;
    @Value("${security.authentication.jwt.tokenValidityInSeconds: 2592000}")
    private long tokenValidityInSecondsForRememberMe;

    @Override
    public String createToken(final Authentication authentication, final Boolean rememberMe) {
        final String authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));
        final String details = JsonUtils.pojoToJson(authentication.getDetails());

        final long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + (this.tokenValidityInSecondsForRememberMe * 1000));
        } else {
            validity = new Date(now + (this.tokenValidityInSeconds * 1000));
        }

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(DefaultTokenProvider.AUTHORITIES_KEY, authorities)
                .claim(DefaultTokenProvider.USERDETAILS_KEY, details)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .setExpiration(validity)
                .compact();
    }

    @Override
    public Authentication getAuthentication(final String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(this.secretKey)
                .parseClaimsJws(token)
                .getBody();
        final Collection<SimpleGrantedAuthority> authorities =
                Arrays.asList(claims.get(DefaultTokenProvider.AUTHORITIES_KEY).toString().split(",")).stream()
                        .filter(authority -> {
                            return StringUtils.hasText(authority);
                        })
                        .map(authority -> new SimpleGrantedAuthority(authority))
                        .collect(Collectors.toList());

        final Map<String, String> details = JsonUtils
                .jsonToPojo(claims.get(DefaultTokenProvider.USERDETAILS_KEY).toString(), DefaultTokenProvider.MAP_TYPE);
        final JWTAuthenticationToken authentication = new JWTAuthenticationToken();
        authentication.setPrincipal(claims.getSubject());
        authentication.setAuthenticated(true);
        authentication.setAuthorities(authorities);
        authentication.setDetails(details);
        return authentication;
    }

    @Override
    public boolean validateToken(final String authToken) {
        try {
            Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(authToken);
            return true;
        } catch (final SignatureException e) {
            this.log.info("Invalid JWT signature: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getLoginUserToken() {
        if (!SecurityUtils.isAuthenticated()) {
            return null;
        }
        final JWTAuthenticationToken auth =
                (JWTAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String token = auth.getToken();
        if (token == null) {
            final long expireTime = System.currentTimeMillis() + (this.tokenValidityInSeconds * 1000);
            token = this.createToken(auth, false);
            auth.setToken(token, expireTime);
        }
        return token;
    }

    public static class TokenProviderCondition implements Condition {

        @Override
        public boolean matches(final ConditionContext paramConditionContext,
                final AnnotatedTypeMetadata paramAnnotatedTypeMetadata) {
            final String[] providers = paramConditionContext.getBeanFactory().getBeanNamesForType(TokenProvider.class);
            if ((providers == null) || (providers.length == 0)) {
                return true;
            }
            return false;
        }
    }
}
