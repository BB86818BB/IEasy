package cn.ieasy.framework.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import cn.ieasy.framework.common.web.config.IeasyWebConfiguration;

/**
 * 启用 Ieasy WebMVC
 *
 * @author LinWang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {IeasyWebConfiguration.class })
@Documented
public @interface EnableIeasyWebMVC {
}
