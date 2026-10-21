package cn.ieasy.framework.common.webauth.client;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import cn.ieasy.framework.common.webauth.config.jwt.JWTConfigurer;
import cn.ieasy.framework.common.webauth.config.jwt.TokenProvider;

public class AuthRestTemplate {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    @Qualifier("poolingConnRestTemplate")
    private RestTemplate pollingRestTemplate;

    @Value("${application.appId:''}")
    private String appId;

    private final RestTemplate simpleRestTemplate = new RestTemplate();

    private final Object[] defaultVars = new Object[0];

    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final Object entity,
            final Class<T> responseType, final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, this.getDefaultHeaders());
        return this.pollingRestTemplate.exchange(url, method, httpEntity, responseType,
                vars);
    }

    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final Object entity,
            final ParameterizedTypeReference<T> responseType, final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, this.getDefaultHeaders());
        return this.pollingRestTemplate.exchange(url, method, httpEntity, responseType,
                vars);
    }

    public <T> ResponseEntity<T> exchange(final HttpHeaders headers, final String url, final HttpMethod method,
            final Object entity,
            final ParameterizedTypeReference<T> responseType, final Object... uriVariables) {
        this.addAuthHeader(headers);
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, headers);
        return this.pollingRestTemplate.exchange(url, method, httpEntity, responseType,
                vars);
    }

    public <T> ResponseEntity<T> exchange(final HttpHeaders headers, final String url, final HttpMethod method,
            final Object entity,
            final Class<T> responseType, final Object... uriVariables) {
        this.addAuthHeader(headers);
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, headers);
        return this.pollingRestTemplate.exchange(url, method, httpEntity, responseType,
                vars);
    }

    public <T> T post(final String url, final Object entity, final Class<T> responseType,
            final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, this.getDefaultHeaders());
        final ResponseEntity<T> rs = this.pollingRestTemplate.exchange(url, HttpMethod.POST,
                httpEntity, responseType,
                vars);
        return rs.getBody();
    }

    public <T> T uploadFile(final String url, final Resource file, final Class<T> responseType, final String fileName) {
        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", file);
        if (StringUtils.hasText(fileName)) {
            map.add("fileName", fileName);
        }
        final HttpHeaders headers = this.getDefaultHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        final ResponseEntity<T> result =
                this.simpleRestTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        responseType);
        return result.getBody();
    }

    public ResponseEntity<byte[]> downloadFile(final String url, final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        final ResponseEntity<byte[]> resp =
                this.simpleRestTemplate.exchange(url, HttpMethod.GET, entity, byte[].class,
                        vars);
        return resp;
    }

    public <T> T get(final String url, final Class<T> responseType, final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(this.getDefaultHeaders());
        final ResponseEntity<T> rs = this.pollingRestTemplate.exchange(url, HttpMethod.GET,
                httpEntity, responseType,
                vars);
        return rs.getBody();
    }

    public <T> T post(final String url, final Object entity, final ParameterizedTypeReference<T> responseType,
            final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, this.getDefaultHeaders());
        final ResponseEntity<T> rs = this.pollingRestTemplate.exchange(url, HttpMethod.POST,
                httpEntity, responseType,
                vars);
        return rs.getBody();
    }

    public <T> T put(final String url, final Object entity, final ParameterizedTypeReference<T> responseType,
            final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> httpEntity = new HttpEntity<>(entity, this.getDefaultHeaders());
        final ResponseEntity<T> rs = this.pollingRestTemplate.exchange(url, HttpMethod.PUT,
                httpEntity, responseType,
                vars);
        return rs.getBody();
    }

    public <T> T get(final String url, final ParameterizedTypeReference<T> responseType, final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> entity = new HttpEntity<>(this.getDefaultHeaders());
        final ResponseEntity<T> rs = this.pollingRestTemplate.exchange(url, HttpMethod.GET,
                entity, responseType, vars);
        return rs.getBody();
    }

    public <T> T delete(final String url, final ParameterizedTypeReference<T> responseType,
            final Object... uriVariables) {
        final Object[] vars = this.getUriVariables(uriVariables);
        final HttpEntity<?> entity = new HttpEntity<>(this.getDefaultHeaders());
        final ResponseEntity<T> rs = this.pollingRestTemplate.exchange(url,
                HttpMethod.DELETE, entity, responseType,
                vars);
        return rs.getBody();
    }

    private HttpHeaders getDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        this.addAuthHeader(headers);
        return headers;
    }

    private void addAuthHeader(final HttpHeaders headers) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final String token = this.tokenProvider.createToken(authentication, false);
            headers.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + token);
        }
    }

    private Object[] getUriVariables(final Object... uriVariables) {
        final Object[] vars = uriVariables != null ? uriVariables : this.defaultVars;
        return vars;
    }
}
