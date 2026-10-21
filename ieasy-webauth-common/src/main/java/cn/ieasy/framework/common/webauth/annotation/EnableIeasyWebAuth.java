package cn.ieasy.framework.common.webauth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import cn.ieasy.framework.common.webauth.config.IeasyWebAuthConfigurer;

/**
 * 启用 Ieasy 安全认证模块
 *
 * @author LinWang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {IeasyWebAuthConfigurer.class })
@Documented
public @interface EnableIeasyWebAuth {

}
