/**
 *
 */
package cn.ieasy.framework.quartz.core.annotition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 按固定间隔触发Job的基本触发器注解类。
 *
 * @version 1.0
 * @see SimpleTriggerBean
 */
@Target({ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleTrigger {
    /** 触发器名 */
    String name() default "";

    /** 组名 */
    String group() default "";

    /** 触发延迟（毫秒） */
    long startDelay() default 0;

    /** 重复次数（默认一直重复） */
    int repeatCount() default -1;

    /** 重复间隔（毫秒） */
    long repeatInterval() default 0;

    /** 开始时间 */
    String startTime() default "";

    /** 结束时间 */
    String endTime() default "";
}
