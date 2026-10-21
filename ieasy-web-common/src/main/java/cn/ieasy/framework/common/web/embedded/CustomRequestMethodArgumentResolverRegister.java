package cn.ieasy.framework.common.web.embedded;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 自定义Request请求参数解析注册器
 * 
 * @author LinWang
 */
public class CustomRequestMethodArgumentResolverRegister extends WebMvcConfigurerAdapter {

    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    public CustomRequestMethodArgumentResolverRegister(final HandlerMethodArgumentResolver... resolvers) {
        for (final HandlerMethodArgumentResolver resolver : resolvers) {
            this.resolvers.add(resolver);
        }
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.resolvers.forEach(resolver -> {
            argumentResolvers.add(resolver);
        });
    }
}
