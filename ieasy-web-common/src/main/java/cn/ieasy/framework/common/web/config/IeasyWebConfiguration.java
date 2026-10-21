package cn.ieasy.framework.common.web.config;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.SqlDateSerializer;

import cn.ieasy.framework.common.web.support.RequestBodyArgumentResolver;
import cn.ieasy.framework.common.web.support.SearchableArgumentResolver;

@Configuration
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableConfigurationProperties({IeasyWebProperties.class })
@ComponentScan(basePackages = "cn.ieasy.framework.common.web")
public class IeasyWebConfiguration extends WebMvcConfigurerAdapter
        implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

    private final Logger log = LoggerFactory.getLogger(IeasyWebConfiguration.class);

    @Autowired
    private Environment env;

    @Autowired
    private IeasyWebProperties properties;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        if (this.env.getActiveProfiles().length != 0) {
            this.log.info("Web application configuration, using profiles: {}",
                    Arrays.toString(this.env.getActiveProfiles()));
        }
        final EnumSet<DispatcherType> disps =
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        if (this.properties.getMetrics().isEnabled()) {
            this.initMetrics(servletContext, disps);
        }
        if (this.env.acceptsProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)) {
            this.initH2Console(servletContext);
        }
        this.log.info("Web application fully configured");
    }

    /**
     * Customize the Tomcat engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(final ConfigurableEmbeddedServletContainer container) {
        final MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
        mappings.add("html", "text/html;charset=utf-8");
        // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
        mappings.add("json", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
        this.customizeTomcat(container);
    }

    /**
     * Customize Tomcat configuration.
     */
    private void customizeTomcat(final ConfigurableEmbeddedServletContainer container) {
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
            // 判断 WebResourceRoot 是否存在， 向下兼容Tomcat低版本
            try {
                final Class<?> rootClazz = Class.forName("org.apache.catalina.webresources.StandardRoot");
                final TomcatEmbeddedServletContainerFactory tomcatFactory =
                        (TomcatEmbeddedServletContainerFactory) container;
                tomcatFactory.addContextCustomizers((TomcatContextCustomizer) context -> {
                    // See https://github.com/jhipster/generator-jhipster/issues/3995
                    try {
                        final Object root = rootClazz.newInstance();
                        final Method maxSizeMethod = rootClazz.getMethod("setCacheMaxSize", long.class);
                        maxSizeMethod.invoke(root, 40960L);
                        final Method objectMaxSizeMethod =
                                rootClazz.getDeclaredMethod("setCacheObjectMaxSize", int.class);
                        objectMaxSizeMethod.invoke(root, 2048);
                        final Method setResource =
                                Class.forName("org.apache.catalina.Context").getDeclaredMethod("setResources",
                                        Class.forName("org.apache.catalina.WebResourceRoot"));
                        setResource.invoke(context, root);
                        this.log.info("Tomcat缓存配置完成！maxSize=40960Byte，objectMaxSize=2048Byte ");
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                        this.log.warn("Tomcat缓存配置失败！" + ex.getMessage());
                    }
                });
            } catch (final Exception e) {
                this.log.warn("向下兼容Tomcat低版本， 跳过Tomcat缓存配置");
            }
        }
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(final ServletContext servletContext, final EnumSet<DispatcherType> disps) {
        this.log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE,
                this.metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY,
                this.metricRegistry);

        this.log.debug("Registering Metrics Filter");
        final FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
                new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        this.log.debug("Registering Metrics Servlet");
        final ServletRegistration.Dynamic metricsAdminServlet =
                servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping(Constants.METRICS_MAPPING);
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
    }

    @Bean
    @ConditionalOnProperty(name = "ieasy.web.cors.allowed-origins")
    public CorsFilter corsFilter() {
        this.log.debug("Registering CORS filter");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = this.properties.getCors();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/v2/api-docs", config);
        source.registerCorsConfiguration("/metrics/**", config);
        source.registerCorsConfiguration("/oauth/**", config);
        final List<String> paths = this.properties.getCorsPath();
        for (final String path : paths) {
            source.registerCorsConfiguration(path, config);
        }
        return new CorsFilter(source);
    }

    /**
     * Initializes H2 console.
     */
    private void initH2Console(final ServletContext servletContext) {
        this.log.debug("Initialize H2 console");
        final ServletRegistration.Dynamic h2ConsoleServlet =
                servletContext.addServlet("H2Console", new org.h2.server.web.WebServlet());
        h2ConsoleServlet.addMapping("/h2-console/*");
        h2ConsoleServlet.setInitParameter("-properties", "src/main/resources/");
        h2ConsoleServlet.setLoadOnStartup(1);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SearchableArgumentResolver());
        argumentResolvers.add(0, new RequestBodyArgumentResolver());
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpClientFactory() {
        // 长连接保持30秒
        final PoolingHttpClientConnectionManager pollingConnectionManager =
                new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(this.properties.getHttpClient().getMaxTotal());
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(this.properties.getHttpClient().getMaxPerRoute());

        final HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(
                new DefaultHttpRequestRetryHandler(this.properties.getHttpClient().getRetryTimes(), true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
        final List<Header> headers = new ArrayList<>();
        httpClientBuilder.setDefaultHeaders(headers);
        final HttpClient httpClient = httpClientBuilder.setRedirectStrategy(new RedirectStrategy() {

            @Override
            public boolean isRedirected(final HttpRequest request, final HttpResponse response,
                    final HttpContext context)
                            throws ProtocolException {
                return false;
            }

            @Override
            public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response,
                    final HttpContext context)
                            throws ProtocolException {
                return null;
            }
        }).build();

        // httpClient连接配置，底层是配置RequestConfig
        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(this.properties.getHttpClient().getConnectTimeout());
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(this.properties.getHttpClient().getReadTimeout());
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory
                .setConnectionRequestTimeout(this.properties.getHttpClient().getConnectionRequestTimeout());
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        clientHttpRequestFactory.setBufferRequestBody(this.properties.getHttpClient().isBufferRequestBody());
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate poolingConnRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate(this.httpClientFactory());
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final SqlDateTimestampSerializer sqlDateSerializer = new SqlDateTimestampSerializer();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, sqlDateSerializer);
        for (final HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                final MappingJackson2HttpMessageConverter jsonMessageConverter =
                        (MappingJackson2HttpMessageConverter) converter;
                final ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                objectMapper.registerModule(module);
            }
        }
    }

    class SqlDateTimestampSerializer extends SqlDateSerializer {
        private static final long serialVersionUID = -5737284018832500025L;

        public SqlDateTimestampSerializer() {
            super(true);
        }
    }
}
