package cn.ieasy.framework.common.web.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Springfox Swagger configuration. Warning! When having a lot of REST
 * endpoints, Springfox can become a performance issue. In that case, you can
 * use a specific Spring profile for this class, so that only front-end
 * developers have access to the Swagger view.
 */
@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfiguration {

    private final Logger log = LoggerFactory.getLogger(SwaggerConfiguration.class);

    public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";

    /**
     * Swagger Springfox configuration.
     *
     * @param ieasyWebProperties the properties of the application
     * @return the Swagger Springfox configuration
     */
    @Bean
    public Docket swaggerSpringfoxDocket(final IeasyWebProperties ieasyWebProperties) {
        this.log.debug("Starting Swagger");
        final StopWatch watch = new StopWatch();
        watch.start();
        final Contact contact = new Contact(
                ieasyWebProperties.getSwagger().getContactName(),
                ieasyWebProperties.getSwagger().getContactUrl(),
                ieasyWebProperties.getSwagger().getContactEmail());

        final ApiInfo apiInfo = new ApiInfo(
                ieasyWebProperties.getSwagger().getTitle(),
                ieasyWebProperties.getSwagger().getDescription(),
                ieasyWebProperties.getSwagger().getVersion(),
                ieasyWebProperties.getSwagger().getTermsOfServiceUrl(),
                contact,
                ieasyWebProperties.getSwagger().getLicense(),
                ieasyWebProperties.getSwagger().getLicenseUrl());

        Predicate<String> pathPattern = PathSelectors.regex(SwaggerConfiguration.DEFAULT_INCLUDE_PATTERN);
        final String pathPatterns = ieasyWebProperties.getSwagger().getPathPatterns();
        if (StringUtils.hasText(pathPatterns)) {
            final String[] patterns = pathPatterns.split(",");
            // Predicates.or(components);
            final List<Predicate<String>> p = new ArrayList<>();
            for (final String pattern : patterns) {
                p.add(PathSelectors.regex(pattern.trim()));
            }
            pathPattern = Predicates.or(p);
        }
        final Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .forCodeGeneration(true)
                .genericModelSubstitutes(ResponseEntity.class)
                .ignoredParameterTypes(java.sql.Date.class)
                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
                .select()
                .paths(pathPattern)
                .build();
        watch.stop();
        this.log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        return docket;
    }
}
