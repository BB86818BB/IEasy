package cn.ieasy.framework.quartz.core.annotition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 按指定日期触发Job的触发器注解类。
 *
 * @version 1.0
 * @see CronTriggerFactoryBean
 */
@Target({ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CronTrigger {
    /** 触发器名 */
    String name() default "";

    /** 组名 */
    String group() default "";

    /** 触发延迟（毫秒） */
    long startDelay() default 0;

    /** 触发时机表达式 */
    String cronExpression() default "";

    /** 开始时间 */
    String startTime() default "";

    /** 结束时间 */
    String endTime() default "";
}
