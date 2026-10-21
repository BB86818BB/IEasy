package cn.ieasy.framework.common.webauth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cn.ieasy.framework.common.webauth.access.CurrentUserArgumentResolver;
import cn.ieasy.framework.common.webauth.client.AuthRestTemplate;
import cn.ieasy.framework.common.webauth.util.SecurityUtils;

@Configuration
@ComponentScan
public class IeasyWebAuthConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public CurrentUserArgumentResolver currentUserArgumentResolver() {
        return new CurrentUserArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(this.currentUserArgumentResolver());
    }

    @Bean
    public AuthRestTemplate authRestTemplate() {
        return new AuthRestTemplate();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            final String loginUser = SecurityUtils.getCurrentUserLogin();
            return loginUser != null ? loginUser : "";
        };
    }
}
