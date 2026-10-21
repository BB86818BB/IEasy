package cn.ieasy.framework.base.dict.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import cn.ieasy.framework.base.dict.IeasyDictConfiguration;

/**
 * 启用Ieasy数据字典(码表)模块
 *
 * @author LinWang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IeasyDictConfiguration.class)
public @interface EnableIeasyDict {
}
