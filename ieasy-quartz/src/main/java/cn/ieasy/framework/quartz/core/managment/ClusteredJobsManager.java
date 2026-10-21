/**
 *
 */
package cn.ieasy.framework.quartz.core.managment;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.StringUtils;

import cn.ieasy.framework.quartz.core.JobRunnable;
import cn.ieasy.framework.quartz.core.annotition.CronTrigger;
import cn.ieasy.framework.quartz.core.annotition.Job;
import cn.ieasy.framework.quartz.core.annotition.JobCore;
import cn.ieasy.framework.quartz.core.annotition.JobMapping;
import cn.ieasy.framework.quartz.core.annotition.SimpleTrigger;
import cn.ieasy.framework.quartz.core.scheduler.CommonJobsExecutor;
import cn.ieasy.framework.quartz.core.scheduler.TimeLimitJobExecutor;
import cn.ieasy.framework.quartz.event.StartAllJobsEvent;
import cn.ieasy.framework.quartz.utils.ParamsSerializer;

/**
 * 针对Quartz集群方式的Job任务调度器。作为调度器代为调度任务（目前只支持调用 Service Bean 的 Public 方法）
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
@JobCore
@Component
public class ClusteredJobsManager implements JobsManager, ApplicationContextAware, BeanPostProcessor,
        JobConcurrentInfoHolder, ApplicationListener<ApplicationEvent>, InitializingBean {

    /** logger */
    private static final Log LOGGER = LogFactory.getLog("JOBLog");

    @Value("${job.useStartAllJobsEvent:false}")
    private boolean useStartAllJobsEvent;

    /** job method conflict error */
    private static final String METHOD_CONFLICT_ERROR =
            "The @JobMapping propetied value [%s] is in conflict with bean [%s]'s";
    /** job method not exist error */
    private static final String METHOD_NOTEXISTED_ERROR =
            "@JobMapping propetied method [%s] does not exist in class [%s]";
    /** job method not exist error */
    private static final String TRIGGER_TIME_FORMAT_ERROR =
            "%s propetied date [%s] has format error in class [%s]";

    private final String RUN_JOB_ERROR_MSG = "job error: jobKey [%s],triggerKey [%s]";

    /** Jobs id->info mapping */
    private final Map<String, JobInfo> jobIdInfoMapping = new HashMap<String, JobInfo>();
    /** Jobs id->annotated trigger mapping */
    private final Map<String, Set<Trigger>> jobIdAnnotatedTriggerMapping = new HashMap<String, Set<Trigger>>();
    /** 应用上下文 */
    private ApplicationContext applicationContext;
    /** 公共的Job执行器 */
    @Resource
    private CommonJobsExecutor commonJobsExecutor;
    @Resource
    private ParamsSerializer paramsSerializer;
    /** 触发器Jobs启动标识 */
    private boolean isTriggerJobStarted = false;

    /**
     * Job 派发处理。
     *
     * @param context 上下文
     * @throws JobExecutionException Job异常
     */
    @Override
    public void dispatch(final JobExecutionContext context) {
        final JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
        final String jobId = jobDataMap.getString(JobsManager.JOB_ID);

        if (!this.jobIdInfoMapping.containsKey(jobId)) {
            ClusteredJobsManager.LOGGER.error(String.format("job [%s] does not exists", jobId));
            // 移除不存在的Job、Trigger
            try {
                this.commonJobsExecutor.removeJobTrigger(context.getTrigger().getKey());
            } catch (final SchedulerException e) {
                e.printStackTrace();
            }
            try {
                this.commonJobsExecutor.removeJob(context.getJobDetail().getKey());
            } catch (final SchedulerException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("job type mapped job no exists! jobId is:" + jobId);
        }

        final MethodInvoker methodInvoker = new MethodInvoker();
        // 取 Job Bean
        final JobInfo jobInfo = this.jobIdInfoMapping.get(jobId);
        final Object jobBean = this.applicationContext.getBean(jobInfo.jobBeanName);
        methodInvoker.setTargetClass(ClassUtils.getUserClass(jobBean));
        methodInvoker.setTargetObject(jobBean);
        methodInvoker.setTargetMethod(jobInfo.jobMethod);
        methodInvoker.setArguments(this.paramsSerializer.deserializeParamsFromMap(jobDataMap));

        // 执行
        try {
            methodInvoker.prepare();
            methodInvoker.invoke();
        } catch (final Throwable e) {
            final String triggerKey = context.getTrigger().getKey().toString();
            final String jobKey = context.getJobDetail().getKey().toString();
            ClusteredJobsManager.LOGGER.error(String.format(this.RUN_JOB_ERROR_MSG, jobKey, triggerKey), e);
        }
    }

    /**
     * 应用上下文加载完成后，启动所有已注册了触发器的Job。
     *
     * @param event 上下文刷新事件
     */
    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        boolean isApplicationStarted = false;
        if (this.useStartAllJobsEvent) {
            isApplicationStarted = (event instanceof StartAllJobsEvent);
        } else {
            isApplicationStarted = (event instanceof ContextRefreshedEvent);
        }
        if (isApplicationStarted) {
            this.scheduleJobsAfterApplicationStarted();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                ClusteredJobsManager.LOGGER.info("==============ShutdownHook===============");
                ClusteredJobsManager.this.shutdownAllJobs();
            }));
        }
        if ((event instanceof ContextClosedEvent) || (event instanceof ContextStoppedEvent)) {
            this.shutdownAllJobs();
        }
    }

    private void scheduleJobsAfterApplicationStarted() {

        if (this.isTriggerJobStarted || this.jobIdInfoMapping.isEmpty()) {
            return; // Job注解为空，处理结束
        }

        this.jobIdInfoMapping.keySet().stream().forEach(key -> {
            try {
                this.commonJobsExecutor.addJob(key);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

        // 启动所有注解配置了触发器的Job
        for (final String jobId : this.jobIdAnnotatedTriggerMapping.keySet()) {
            final Set<Trigger> triggers = this.jobIdAnnotatedTriggerMapping.get(jobId);
            for (final Trigger trigger : triggers) {
                this.scheduleJobsWithTrigger(jobId, trigger);
            }
        }

        this.isTriggerJobStarted = true;
    }

    private void shutdownAllJobs() {
        this.commonJobsExecutor.shutdownScheduledJobs();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#
     * postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        // mapping Job implementation bean
        this.registerJobForJobImplementation(bean, beanName);
        // mapping annotated Job bean
        this.registerJobsForJobAnnotations(bean, beanName);
        return bean;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#
     * postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    /**
     * Register Job for <code>{@link JobRunnable}</code> implementation bean.
     *
     * @param bean bean
     * @param beanName bean名称
     */
    private void registerJobForJobImplementation(final Object bean, final String beanName) {

        if (bean instanceof JobRunnable) {
            final JobRunnable job = (JobRunnable) bean;
            final String jobKey = job.getJobKey();
            if (this.jobIdInfoMapping.containsKey(jobKey)) {
                throw new RuntimeException("The [@JobMapping] propetied value has been used by other class");
            }
            this.jobIdInfoMapping.put(jobKey, new JobInfo(beanName, "execute", job.allowConcurrent()));
            final String triggerCronExpression = ((JobRunnable) bean).getTriggerCronExpression();
            if (triggerCronExpression != null) {
                final CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
                cronTriggerBean.setKey(new TriggerKey(jobKey));
                try {
                    cronTriggerBean.setCronExpression(triggerCronExpression);
                } catch (final ParseException e) {
                    ClusteredJobsManager.LOGGER.error(e);
                    throw new RuntimeException(e);
                }
                cronTriggerBean.setJobKey(new JobKey(jobKey));
                this.registerJobTrigger(cronTriggerBean);
            }
        } else if (bean instanceof TimeLimitJobExecutor) {
            final String jobKey = ((TimeLimitJobExecutor<?>) bean).getJobKey();
            if (this.jobIdInfoMapping.containsKey(jobKey)) {
                throw new RuntimeException("The [@JobMapping] propetied value has been used by other class");
            }
            this.jobIdInfoMapping.put(jobKey, new JobInfo(beanName, "runJobTask", false));
        }
    }

    /**
     * Register Jobs for bean which annotated <code>{@link JobMapping}</code>.
     *
     * @param bean bean
     * @param beanName bean名称
     */
    private void registerJobsForJobAnnotations(final Object bean, final String beanName) {
        final Class<?> beanClass = ClassUtils.getUserClass(bean);

        // 非Job Bean 暂不做管理
        if (beanClass.getAnnotation(Job.class) == null) {
            return;
        }

        final JobMapping jobMapping = beanClass.getAnnotation(JobMapping.class);
        if (jobMapping != null) {
            // 注册类注解的JobMapping
            this.registerJobMapping(jobMapping, beanClass, beanName, null);

            // 注册类注解的SimpleTrigger
            this.registerSimpleTrigger(beanClass, jobMapping, beanClass.getAnnotation(SimpleTrigger.class));

            // 注册类注解的CronTrigger
            this.registerCronTrigger(beanClass, jobMapping, beanClass.getAnnotation(CronTrigger.class));
        }

        // 处理方法级别的注解
        for (final Method method : beanClass.getMethods()) {

            final JobMapping methodJobMapping = AnnotationUtils.getAnnotation(method, JobMapping.class);
            if (methodJobMapping == null) {
                continue;
            }
            // 注册方法注解的JobMapping
            this.registerJobMapping(methodJobMapping, beanClass, beanName, method.getName());

            // 注册方法注解的SimpleTrigger
            this.registerSimpleTrigger(beanClass, methodJobMapping,
                    AnnotationUtils.getAnnotation(method, SimpleTrigger.class));

            // 注册方法注解的CronTrigger
            this.registerCronTrigger(beanClass, methodJobMapping,
                    AnnotationUtils.getAnnotation(method, CronTrigger.class));
        }
    }

    /**
     * 注册<code>{@link CronTrigger}</code>触发器。
     *
     * @param jobMapping jobMapping
     * @param beanClass beanClass
     * @param cronTrigger 触发器注解
     */
    private void registerCronTrigger(final Class<?> beanClass, final JobMapping jobMapping,
            final CronTrigger cronTrigger) {
        if (cronTrigger == null) {
            return;
        }
        // 校验触发器触发时间表达式
        Assert.hasLength(cronTrigger.cronExpression());

        final String name = StringUtils.hasText(cronTrigger.name()) ? cronTrigger.name() : jobMapping.id();
        final String group = StringUtils.hasText(cronTrigger.group()) ? cronTrigger.group() : null;
        // 开始、结束时间
        final Date startTime =
                this.parseTriggerTime(cronTrigger.getClass().getName(), cronTrigger.startTime(),
                        beanClass.getName());
        final Date endTime =
                this.parseTriggerTime(cronTrigger.getClass().getName(), cronTrigger.endTime(), beanClass.getName());

        // 构造触发器
        final CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
        cronTriggerBean.setName(name);
        cronTriggerBean.setGroup(group);

        try {
            cronTriggerBean.setCronExpression(cronTrigger.cronExpression());
        } catch (final ParseException e) {
            ClusteredJobsManager.LOGGER.error(e);
            throw new RuntimeException(e);
        }
        if (startTime != null) {
            cronTriggerBean.setStartTime(startTime);
        }
        if (endTime != null) {
            cronTriggerBean.setEndTime(endTime);
        }
        cronTriggerBean.setJobKey(new JobKey(jobMapping.id()));
        this.registerJobTrigger(cronTriggerBean);
    }

    /**
     * 注册<code>{@link SimpleTrigger}</code>触发器。
     *
     * @param jobMapping jobMapping
     * @param beanClass beanClass
     * @param simpleTrigger 触发器注解
     */
    private void registerSimpleTrigger(final Class<?> beanClass, final JobMapping jobMapping,
            final SimpleTrigger simpleTrigger) {
        if (simpleTrigger == null) {
            return;
        }
        final String name = StringUtils.hasText(simpleTrigger.name()) ? simpleTrigger.name() : jobMapping.id();
        final String group = StringUtils.hasText(simpleTrigger.group()) ? simpleTrigger.group() : null;
        // 开始、结束时间
        final Date startTime =
                this.parseTriggerTime(simpleTrigger.getClass().getName(), simpleTrigger.startTime(),
                        beanClass.getName());
        final Date endTime =
                this.parseTriggerTime(simpleTrigger.getClass().getName(), simpleTrigger.endTime(),
                        beanClass.getName());

        // 构造触发器
        final SimpleTriggerImpl simpleTriggerBean = new SimpleTriggerImpl();
        simpleTriggerBean.setName(name);
        simpleTriggerBean.setGroup(group);
        simpleTriggerBean.setRepeatCount(simpleTrigger.repeatCount());
        simpleTriggerBean.setRepeatInterval(simpleTrigger.repeatInterval());
        if (startTime != null) {
            simpleTriggerBean.setStartTime(startTime);
        } else {
            simpleTriggerBean.setStartTime(new Date(new Date().getTime() + simpleTrigger.startDelay()));
        }
        if (endTime != null) {
            simpleTriggerBean.setEndTime(endTime);
        }
        simpleTriggerBean.setJobKey(new JobKey(jobMapping.id()));
        this.registerJobTrigger(simpleTriggerBean);
    }

    /**
     * 注册Job的触发器，跟jobId关联。
     *
     * @param trigger 触发器
     * @param jobMapping jobMapping
     */
    private void registerJobTrigger(final Trigger trigger) {
        final String jobId = trigger.getJobKey().getName();
        Set<Trigger> triggers = this.jobIdAnnotatedTriggerMapping.get(jobId);
        if (triggers == null) {
            triggers = new HashSet<Trigger>();
            this.jobIdAnnotatedTriggerMapping.put(jobId, triggers);
        }
        triggers.add(trigger);
    }

    /**
     * 转换触发器的触发时间。
     *
     * @param triggerClass 触发器类名
     * @param dateStr 时间字符串
     * @param beanClassName bean类名
     * @return 触发器触发时间
     */
    private Date parseTriggerTime(final String triggerClass, final String dateStr, final String beanClassName) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            final SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            timeformat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return timeformat.parse(dateStr);
        } catch (final ParseException e) {
            final String error =
                    String.format(ClusteredJobsManager.TRIGGER_TIME_FORMAT_ERROR, "@SimpleTrigger", dateStr,
                            beanClassName);
            ClusteredJobsManager.LOGGER.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * 注册Job的Mapping信息。
     *
     * @param jobMapping jobMapping
     * @param beanName bean的名称
     * @param jobMethod job方法
     */
    private synchronized void registerJobMapping(final JobMapping jobMapping, final Class<?> beanClass,
            final String beanName, final String jobMethod) {
        final String jobId = jobMapping.id();
        String method = jobMethod;

        // 方法名为空的，从注解中取，并校验方法的存在
        if (!StringUtils.hasText(method)) {
            method = jobMapping.method();
            if (ClassUtils.getMethodCountForName(beanClass, method) < 1) {
                final String error =
                        String.format(ClusteredJobsManager.METHOD_NOTEXISTED_ERROR, method, beanClass.getName());
                ClusteredJobsManager.LOGGER.error(error);
                throw new RuntimeException(error);
            }
        }

        // 校验jobId、method不为空
        Assert.hasLength(jobId);
        Assert.hasLength(method);
        // 校验JobId的唯一性
        if (this.jobIdInfoMapping.containsKey(jobId)) {
            if (this.jobIdInfoMapping.get(jobId).jobBeanName.equals(beanName)) {
                return;
            }
            final String error =
                    String.format(ClusteredJobsManager.METHOD_CONFLICT_ERROR, jobId,
                            this.jobIdInfoMapping.get(jobId).jobBeanName);

            ClusteredJobsManager.LOGGER.error(error);
            throw new RuntimeException(error);
        }

        this.jobIdInfoMapping.put(jobId, new JobInfo(beanName, jobMethod, jobMapping.allowConcurrent()));
    }

    private void scheduleJobsWithTrigger(final String jobId, final Trigger trigger) {
        try {
            this.commonJobsExecutor.scheduleJob(trigger);
        } catch (final SchedulerException e) {
            // Job派发过程中出现了异常
            ClusteredJobsManager.LOGGER.error(
                    String.format("Job's shceduler error occured! Job's id is [%s], Trigger's id is [%s]",
                            trigger.getJobKey(), trigger.getKey()),
                    e);
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * JobInfo
     *
     * @author LinWang
     */

    class JobInfo {
        /** jobBeanName */
        String jobBeanName;
        /** method */
        String jobMethod;
        /** 是否允许并发执行 */
        boolean isAllowConcurrent;

        public JobInfo() {
        }

        /**
         * 构造函数。
         *
         * @param jobBeanName jobBeanName
         * @param jobMethod jobMethod
         */
        public JobInfo(final String jobBeanName, final String jobMethod, final boolean isAllowConcurrent) {
            this.jobBeanName = jobBeanName;
            this.jobMethod = jobMethod;
            this.isAllowConcurrent = isAllowConcurrent;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * cn.com.dhc.common.job.JobConcurrentInfoHolder#isAllowConcurrentExecute
     * (java.lang.String)
     */
    @Override
    public boolean isAllowConcurrentExecute(final String jobKey) {
        if (this.jobIdInfoMapping.containsKey(jobKey)) {
            return this.jobIdInfoMapping.get(jobKey).isAllowConcurrent;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.commonJobsExecutor.setJobConcurrentInfoHolder(this);
    }
}
