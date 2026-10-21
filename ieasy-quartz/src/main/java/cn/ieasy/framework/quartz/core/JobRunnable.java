
package cn.ieasy.framework.quartz.core;

import org.springframework.context.annotation.Lazy;

/**
 * Job实行接口。
 */
@Lazy(value = false)
public interface JobRunnable {

    /**
     * 执行任务。
     */
    void execute();

    /**
     * 取Job Key
     *
     * @return Job Key
     */
    String getJobKey();

    /**
     * 取触发器定时表达式
     *
     * @return
     */
    String getTriggerCronExpression();

    /**
     * 是否允许并发执行
     */
    boolean allowConcurrent();
}
