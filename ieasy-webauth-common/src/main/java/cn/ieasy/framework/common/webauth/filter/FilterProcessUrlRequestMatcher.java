package cn.ieasy.framework.common.webauth.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

public class FilterProcessUrlRequestMatcher implements RequestMatcher {
    private final String filterProcessesUrl;

    public FilterProcessUrlRequestMatcher(final String filterProcessesUrl) {
        Assert.hasLength(filterProcessesUrl, "filterProcessesUrl must be specified");
        Assert.isTrue(UrlUtils.isValidRedirectUrl(filterProcessesUrl),
                filterProcessesUrl + " isn't a valid redirect URL");
        this.filterProcessesUrl = filterProcessesUrl;
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        String uri = request.getRequestURI();
        final int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        if ("".equals(request.getContextPath())) {
            return uri.equals(this.filterProcessesUrl);
        }

        return uri.equals(request.getContextPath() + this.filterProcessesUrl);
    }
}
