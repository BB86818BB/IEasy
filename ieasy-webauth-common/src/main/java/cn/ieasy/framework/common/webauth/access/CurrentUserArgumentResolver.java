package cn.ieasy.framework.common.webauth.access;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import cn.ieasy.framework.common.web.exception.AccessDenyException;
import cn.ieasy.framework.common.web.support.CurrentUserFactoryBean;
import cn.ieasy.framework.common.webauth.annotation.CurrentUser;
import cn.ieasy.framework.common.webauth.util.SecurityUtils;

public final class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired(required = false)
    private UserDetailsService userDetailsService;
    @Autowired(required = false)
    private CurrentUserService currentUserService;
    @Autowired(required = false)
    private CurrentUserFactoryBean currentUserFactoryBean;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return (this.findMethodAnnotation(CurrentUser.class, parameter) != null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {

        final String currentUser = SecurityUtils.getCurrentUserLogin();
        if (!StringUtils.hasText(currentUser)) {
            throw new SessionAuthenticationException("用户未登录.");
        }
        // String 类型返回principal
        if (parameter.getParameterType().isAssignableFrom(String.class)) {
            return currentUser;
        }
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (CurrentUserBean.class.isAssignableFrom(parameter.getParameterType())) {
            final CurrentUserBean user = (CurrentUserBean) parameter.getParameterType().newInstance();
            user.setUserPrincipal(currentUser);
            user.setRoles(authentication.getAuthorities().stream().map(authority -> authority.getAuthority())
                    .collect(Collectors.toList()));
            user.setDetails((Map<String, String>) authentication.getDetails());
            return user;
        }
        if (UserDetails.class.isAssignableFrom(parameter.getParameterType())) {
            if (this.userDetailsService == null) {
                throw new RuntimeException("没有找到UserDetailsService的实现");
            }
            return this.userDetailsService.loadUserByUsername(currentUser);
        }
        if (this.currentUserService != null) {
            return this.currentUserService.loadUserByUsername(currentUser);
        } else if (this.currentUserFactoryBean != null) {
            final Object bean = this.currentUserFactoryBean.getCurrentUser();
            if (parameter.getParameterType().isAssignableFrom(bean.getClass())) {
                return bean;
            } else {
                throw new AccessDenyException("auth.accessDeny", "无访问权限");
            }
        } else {
            throw new RuntimeException("没有找到CurrentUserService的实现");
        }
    }

    private <T extends Annotation> T findMethodAnnotation(final Class<T> annotationClass,
            final MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        final Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for (final Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

}
