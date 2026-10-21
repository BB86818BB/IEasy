package cn.ieasy.framework.base.filestorage.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import cn.ieasy.framework.base.filestorage.IeasyStorageConfiguration;

/**
 * 启用ieasy文件上传下载模块
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IeasyStorageConfiguration.class)
public @interface EnableIeasyStorage {
}
