/**
 *
 */
package cn.ieasy.framework.quartz.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import cn.ieasy.framework.quartz.core.annotition.JobMapping;
import cn.ieasy.framework.quartz.core.managment.JobsManager;

/**
 * 公共的Job。
 *
 * <pre>
 * 由容器托管的<code>{@link org.springframework.stereotype.Service}</code>
 * Bean实例中，所有注释了<code>{@link JobMapping}</code> 的方法都能够通过执行该Job代为触发执行。
 * </pre>
 *
 * @version 1.0
 */
@Component
public class CommonSchedulerJob extends QuartzJobBean {

    /** logger */
    private static final Log LOGGER = LogFactory.getLog("JOBLog");
    /** 共通Job派发处理器，负责所有Job服务的调起 */
    private JobsManager commonJobDispatcher;

    private final String BEGIN_RUN_JOB_MSG = "begin to run the job: jobKey [%s],triggerKey [%s]";

    private final String FINISHED_RUN_JOB_MSG = "the job was finished: jobKey [%s],triggerKey [%s]";

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org
     * .quartz.JobExecutionContext)
     */
    @Override
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
        final String triggerKey = context.getTrigger().getKey().toString();
        final String jobKey = context.getTrigger().getJobKey().toString();
        if (CommonSchedulerJob.LOGGER.isInfoEnabled()) {
            CommonSchedulerJob.LOGGER.info(String.format(this.BEGIN_RUN_JOB_MSG, jobKey, triggerKey));
        }

        this.commonJobDispatcher.dispatch(context);

        if (CommonSchedulerJob.LOGGER.isInfoEnabled()) {
            CommonSchedulerJob.LOGGER.info(String.format(this.FINISHED_RUN_JOB_MSG, jobKey, triggerKey));
        }

    }

    /**
     * @return commonJobDispatcher
     */
    public JobsManager getCommonJobDispatcher() {
        return this.commonJobDispatcher;
    }

    /**
     * @param commonJobDispatcher commonJobDispatcher
     */
    public void setCommonJobDispatcher(final JobsManager commonJobDispatcher) {
        this.commonJobDispatcher = commonJobDispatcher;
    }
}
