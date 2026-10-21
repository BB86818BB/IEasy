/**
 *
 */
package cn.ieasy.framework.quartz.core.managment;

import org.quartz.JobExecutionContext;

import cn.ieasy.framework.quartz.core.JobRunnable;
import cn.ieasy.framework.quartz.core.annotition.JobMapping;
import cn.ieasy.framework.quartz.core.scheduler.CommonJobsExecutor;

/**
 * 公共的Job任务调度器。作为调度器代为调度任务（目前只支持调用 Service Bean 的 Public 方法）
 *
 * <pre>
 * 由容器托管的、注释了<code>{@link org.springframework.stereotype.Service}</code>的
 * Bean实例，通过下面的方式可以作为Job任务，由该调度器调度管理。
 * <li>所有注释了<code>{@link JobMapping}</code> 的方法，都能够当作Job的执行任务, 通过<code>{@link CommonJobsExecutor}</code>触发。
 * <li>所有实现了<code>{@link JobRunnable}</code>接口的类，接口方法都可以当中Job的执行任务，通过<code>{@link CommonJobsExecutor}</code>触发。
 * <li>所有注释了<code>{@link SimpleTrigger}</code>的方法，会生成Job任务的SimpleTrigger触发器，在Web应用启动的时候被运行。
 * <li>所有注释了<code>{@link CronTrigger}</code>的方法，会生成Job任务的CronTrigger触发器，在Web应用启动的时候被运行。
 * </pre>
 *
 * @version 1.0
 * @see JobMapping
 * @see SimpleTrigger
 * @see CronTrigger
 */
public interface JobsManager {

    /** job type key */
    static final String JOB_ID = "job.id.key";
    /** job parameters key */
    static final String JOB_PARAMS = "job.params.key";
    /** job parameter type key */
    static final String JOB_PARAMS_TYPE = "job.params.type.key";
    /** job parameter type key */
    static final String TYPE_IS_REFERANCE = "type.is.referance.key";

    /**
     * 派发Job任务。
     *
     * @param context job运行上下文
     */
    void dispatch(final JobExecutionContext context);
}
