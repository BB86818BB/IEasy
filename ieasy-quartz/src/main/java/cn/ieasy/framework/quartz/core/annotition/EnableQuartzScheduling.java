package cn.ieasy.framework.quartz.core.annotition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import cn.ieasy.framework.quartz.QuartzConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAsync
@EnableScheduling
@Import(QuartzConfiguration.class)
@Documented
@Configuration
public @interface EnableQuartzScheduling {

}
