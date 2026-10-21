package cn.ieasy.framework.quartz.core.scheduler;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.ieasy.framework.quartz.core.CommonSchedulerJob;
import cn.ieasy.framework.quartz.core.DisallowConcurrentExectionJob;
import cn.ieasy.framework.quartz.core.JobRunnable;
import cn.ieasy.framework.quartz.core.annotition.JobMapping;
import cn.ieasy.framework.quartz.core.listeners.CronTriggerScheduleListener;
import cn.ieasy.framework.quartz.core.managment.JobConcurrentInfoHolder;
import cn.ieasy.framework.quartz.core.managment.JobsManager;
import cn.ieasy.framework.quartz.utils.ParamsSerializer;

/**
 * * 公共的Job执行器。
 *
 * <pre>
 * 所有<code>{@link JobMapping}</code>注释的方法都可以被该执行器调用 实现
 * <code>{@link JobRunnable}</code>接口的类，其接口方法可以被该执行器调用
 *
 * @author LinWang
 */
@Component
public class CommonJobsExecutor {

    /** logger */
    private static final Log LOGGER = LogFactory.getLog("JOBLog");

    /** Spring瀹瑰櫒缁存姢鐨凧ob娲惧彂鍣� */
    @Resource
    private Scheduler scheduler;
    @Resource
    private ParamsSerializer paramsSerializer;

    /**
     * 瀹氭椂瑙﹀彂鍣ㄦ淳鍙戠洃鍚櫒锛堝唴瀛樻柟寮忕殑Quartz璋冨害锛岄渶瑕佺▼搴忓疄鐜版湇鍔″啀鍚姩鏃剁殑Job鎭㈠锛屾墍浠ヨ
     * 鐩戝惉姣忎釜鏂拌Е鍙戝櫒鐨勪娇鐢紱闆嗙兢鏂瑰紡涓嶉渶瑕佽鐩戝惉锛�
     */
    private Set<CronTriggerScheduleListener> cronTriggerScheduleListeners;

    private JobConcurrentInfoHolder jobConcurrentInfoHolder;

    public void addJob(final String jobId) throws SchedulerException {
        if (this.scheduler.checkExists(new JobKey(jobId))) {
            return;
        }
        final JobDetail detail = this.createJobDetail(new JobKey(jobId));
        this.scheduler.addJob(detail, false);
    }

    /**
     * 娲惧彂瀹氭椂瑙﹀彂鐨凧ob銆�
     *
     * @param jobId jobId
     * @param triggerId 瑙﹀彂鍣↖d
     * @param cronExpression 瀹氭椂瑙﹀彂瑙勫垯
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final String jobId, final String triggerId, final String cronExpression)
            throws SchedulerException {
        this.scheduleJob(jobId, triggerId, cronExpression, null, null);
    }

    /**
     * 娲惧彂瀹氭椂瑙﹀彂鐨凧ob銆�
     *
     * @param jobId jobId
     * @param triggerId 瑙﹀彂鍣↖d
     * @param cronExpression 瀹氭椂瑙﹀彂瑙勫垯
     * @param triggerParams 鍙傛暟
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final String jobId, final String triggerId, final String cronExpression,
            final Object... triggerParams) throws SchedulerException {
        this.scheduleJob(jobId, triggerId, cronExpression, triggerParams, null);
    }

    /**
     * 娲惧彂瀹氭椂瑙﹀彂鐨凧ob銆�
     *
     * @param jobId jobId
     * @param triggerId 瑙﹀彂鍣↖d
     * @param cronExpression 瀹氭椂瑙﹀彂瑙勫垯
     * @param params 鍙傛暟
     * @param paramTypes 鍙傛暟绫诲瀷锛堣В鍐虫硾鍨嬫摝闄わ級
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final String jobId, final String triggerId, final String cronExpression,
            final Object[] params, final TypeReference<?>[] paramTypes) throws SchedulerException {
        final Trigger cronTrigger = this.createCronTrigger(jobId, new TriggerKey(triggerId), cronExpression);
        this.scheduleJob(cronTrigger, params, paramTypes);
    }

    /**
     * 娲惧彂涓�涓寚瀹氫簡瑙﹀彂鍣ㄧ殑Job銆�
     *
     * @param trigger 瑙﹀彂鍣�
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final Trigger trigger) throws SchedulerException {
        this.scheduleJob(trigger, null, null);
    }

    /**
     * 娲惧彂涓�涓寚瀹氫簡瑙﹀彂鍣ㄧ殑Job銆�
     *
     * @param trigger 瑙﹀彂鍣�
     * @param params 鍙傛暟
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final Trigger trigger, final Object... params) throws SchedulerException {
        this.scheduleJob(trigger, params, null);
    }

    /**
     * 娲惧彂涓�涓寚瀹氫簡瑙﹀彂鍣ㄧ殑Job銆�
     *
     * @param trigger 瑙﹀彂鍣�
     * @param params 鍙傛暟
     * @param paramTypes 鍙傛暟绫诲瀷
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final Trigger trigger, final Object[] params, final TypeReference<?>[] paramTypes)
            throws SchedulerException {

        // 璁剧疆鍙傛暟
        final JobDataMap jobDataMap = trigger.getJobDataMap();
        final String jobId = trigger.getJobKey().getName();
        Assert.hasLength(jobId);
        jobDataMap.put(JobsManager.JOB_ID, jobId);
        this.paramsSerializer.serializeParamsToMap(jobDataMap, jobId, params, paramTypes);

        // 宸茬粡瀛樺湪鐩稿悓鐨勮Е鍙戝櫒
        final boolean isTriggerExists = this.checkTriggerExists(trigger.getKey());
        final Trigger oldTrigger = isTriggerExists ? this.scheduler.getTrigger(trigger.getKey()) : null;
        // JOB骞跺彂灞炴�у彉鏇�
        final boolean isAllowCurrencyChanged = isTriggerExists && this.isAllowCurrencyChanged(oldTrigger, trigger);

        if (!isTriggerExists || isAllowCurrencyChanged) {
            // 濡傛灉JOB骞跺彂灞炴�у彉鏇� 鍒犻櫎鍘烰OB
            if (isAllowCurrencyChanged) {
                this.removeJob(trigger.getJobKey());
            }

            if (this.scheduler.checkExists(trigger.getJobKey())) {
                this.scheduler.scheduleJob(trigger);
            } else {
                this.scheduler.scheduleJob(this.createJobDetail(trigger.getJobKey()), trigger);
            }

            this.fireTriggerScheduleEvent(trigger);
            if (CommonJobsExecutor.LOGGER.isInfoEnabled()) {
                CommonJobsExecutor.LOGGER.info(String.format(
                        "Job started... Job's id is [%s], Trigger's id is [%s]", trigger.getJobKey(),
                        trigger.getKey()));
            }
        } else if (this.isTriggerChanged(oldTrigger, trigger)) { // 瑙﹀彂鍣ㄥ彉鏇寸殑
            this.scheduler.rescheduleJob(oldTrigger.getKey(), trigger);
            if (CommonJobsExecutor.LOGGER.isInfoEnabled()) {
                CommonJobsExecutor.LOGGER.info("Trigger rescheduled, Job key:" + oldTrigger.getJobKey().getName());
            }
        }
    }

    /**
     * 绔嬪嵆娲惧彂涓�涓柊鐨凧ob锛岃Job鎵ц瀹屽悗涓嶄細椹荤暀鍦ㄦ淳鍙戝櫒涓紙鏃犲弬鏁帮級銆�
     *
     * @param jobId job鏍囪瘑
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJobImmediately(final String jobId) throws SchedulerException {
        this.scheduleJob(jobId, 0, null, null);
    }

    /**
     * 娲惧彂涓�涓柊鐨凧ob锛岃Job鎵ц瀹屽悗涓嶄細椹荤暀鍦ㄦ淳鍙戝櫒涓紙鏃犲弬鏁帮級銆�
     *
     * @param jobId job鏍囪瘑
     * @param delaySeconds 寤惰繜绉掓暟
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final String jobId, final int delaySeconds) throws SchedulerException {
        this.scheduleJob(jobId, delaySeconds, null, null);
    }

    /**
     * 绔嬪嵆娲惧彂涓�涓柊鐨凧ob锛岃Job鎵ц瀹屽悗涓嶄細椹荤暀鍦ㄦ淳鍙戝櫒涓紙閽堝闈炴硾鍨嬪弬鏁帮級銆�
     *
     * @param jobId job鏍囪瘑
     * @param params 鍙傛暟锛堢敱浜庡瓨鍦ㄦ硾鍨嬫摝闄わ紝璇ユ柟娉曚笉鑳戒娇鐢ㄦ硾鍨嬬被鍨嬬殑鍙傛暟锛�
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJobImmediately(final String jobId, final Object... params) throws SchedulerException {
        this.scheduleJob(jobId, 0, params, null);
    }

    /**
     * 娲惧彂涓�涓柊鐨凧ob锛岃Job鎵ц瀹屽悗涓嶄細椹荤暀鍦ㄦ淳鍙戝櫒涓紙閽堝闈炴硾鍨嬪弬鏁帮級銆�
     *
     * @param jobId job鏍囪瘑
     * @param delaySeconds 寤惰繜绉掓暟
     * @param params 鍙傛暟锛堢敱浜庡瓨鍦ㄦ硾鍨嬫摝闄わ紝璇ユ柟娉曚笉鑳戒娇鐢ㄦ硾鍨嬬被鍨嬬殑鍙傛暟锛�
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final String jobId, final int delaySeconds, final Object... params)
            throws SchedulerException {
        this.scheduleJob(jobId, delaySeconds, params, null);
    }

    /**
     * 娲惧彂涓�涓柊鐨凧ob锛岃Job鎵ц瀹屽悗涓嶄細椹荤暀鍦ㄦ淳鍙戝櫒涓紙閽堝娉涘瀷鍙傛暟锛夈��
     *
     * @param jobId job鏍囪瘑
     * @param params 鍙傛暟
     * @param paramTypes 鍙傛暟鐨勭被鍨嬪紩鐢ㄦ暟缁勶紙鐢ㄤ簬澶勭悊娉涘瀷绫诲瀷鐨勫弬鏁帮級
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJobImmediately(final String jobId, final Object[] params,
            final TypeReference<?>[] paramTypes) throws SchedulerException {

        this.scheduleJob(jobId, 0, params, paramTypes);
    }

    /**
     * 娲惧彂涓�涓柊鐨凧ob锛岃Job鎵ц瀹屽悗涓嶄細椹荤暀鍦ㄦ淳鍙戝櫒涓紙閽堝娉涘瀷鍙傛暟锛夈��
     *
     * @param jobId job鏍囪瘑
     * @param delaySeconds 寤惰繜绉掓暟
     * @param params 鍙傛暟
     * @param paramTypes 鍙傛暟鐨勭被鍨嬪紩鐢ㄦ暟缁勶紙鐢ㄤ簬澶勭悊娉涘瀷绫诲瀷鐨勫弬鏁帮級
     * @throws SchedulerException Job娲惧彂杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void scheduleJob(final String jobId, final int delaySeconds, final Object[] params,
            final TypeReference<?>[] paramTypes) throws SchedulerException {

        final Trigger trigger = this.createTrigger(jobId, null, null, delaySeconds);

        this.scheduleJob(trigger, params, paramTypes);
    }

    /**
     * 绉婚櫎涓�涓狫ob瑙﹀彂鍣ㄣ��
     *
     * @param triggerKey triggerKey
     * @throws SchedulerException
     */
    public void removeJobTrigger(final TriggerKey triggerKey) throws SchedulerException {
        if (this.scheduler.checkExists(triggerKey)) {
            final Trigger trigger = this.scheduler.getTrigger(triggerKey);
            this.scheduler.unscheduleJob(triggerKey);
            this.fireTriggerRemovedEvent(trigger);
        }
    }

    /**
     * 绉婚櫎鎸囧畾鐨凧ob銆�
     *
     * @param jobKey jobKey
     * @throws SchedulerException Job绉婚櫎杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void removeJob(final JobKey jobKey) throws SchedulerException {
        this.scheduler.deleteJob(jobKey);
    }

    /**
     * 娓呯┖鎵�鏈塉ob銆�
     *
     * @throws SchedulerException Job绉婚櫎杩囩▼涓嚭鐜颁簡寮傚父
     */
    public void removeAllJobs() throws SchedulerException {
        this.scheduler.clear();
    }

    /**
     * 閲嶆柊娲惧彂Job
     *
     * @param triggerKey triggerKey
     * @param newTrigger newTrigger
     * @throws SchedulerException
     */
    public void rescheduleJob(final TriggerKey triggerKey, final Trigger newTrigger) throws SchedulerException {
        this.scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    /**
     * 鐢熸垚瀹氭椂瑙﹀彂鍣ㄣ��
     *
     * @param jobId jobId
     * @param triggerKey triggerKey
     * @param cronExpression cronExpression
     * @return 瀹氭椂瑙﹀彂鍣�
     */
    public Trigger createCronTrigger(final String jobId, final TriggerKey triggerKey, final String cronExpression) {
        return this.createCronTrigger(jobId, triggerKey, cronExpression, null);
    }

    /**
     * 鐢熸垚瀹氭椂瑙﹀彂鍣ㄣ��
     *
     * @param jobId jobId
     * @param triggerKey triggerKey
     * @param cronExpression cronExpression
     * @param jobDataMap 鍙傛暟
     * @return 瀹氭椂瑙﹀彂鍣�
     */
    public Trigger createCronTrigger(final String jobId, final TriggerKey triggerKey, final String cronExpression,
            final JobDataMap jobDataMap) {
        Assert.hasLength(cronExpression);
        final JobKey jobKey = new JobKey(jobId);
        // 鏋勯�犺Е鍙戝櫒
        final CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
        cronTriggerBean.setKey(triggerKey);
        cronTriggerBean.setJobKey(jobKey);
        try {
            cronTriggerBean.setCronExpression(cronExpression);
        } catch (final ParseException e) {
            CommonJobsExecutor.LOGGER.error(e);
            throw new RuntimeException(e);
        }
        if (jobDataMap != null) {
            cronTriggerBean.setJobDataMap(jobDataMap);
        }
        return cronTriggerBean;
    }

    /**
     * 鍒涘缓鍩烘湰瑙﹀彂鍣ㄣ��
     *
     * @param jobId jobId
     * @param name 瑙﹀彂鍣ㄥ悕
     * @param group 缁勫悕
     * @param delaySeconds 寤惰繜绉掓暟
     * @return 瑙﹀彂鍣ㄥ疄渚�
     */
    public Trigger createTrigger(final String jobId, final String name, final String group, final int delaySeconds) {
        final double disturbedTime = Math.random(); // 骞叉壈鏃堕棿(0.0, 0.1)绉掔殑闅忔満鏁帮紝闃叉鍚屼竴鏃跺埢璋冨害寰堝Job
        // Job 瑙﹀彂鏃堕棿
        Date startTime = null;
        if (delaySeconds > 0) {
            // 瑙﹀彂鏃跺埢锛堟绉掞級 = 褰撳墠鏃跺埢 + 寤惰繜姣 - 骞叉壈姣
            final long runTime = (new Date().getTime() + (1000L * delaySeconds)) - (long) (100L * disturbedTime);
            startTime = new Date(runTime);
        } else {
            // 瑙﹀彂鏃跺埢锛堟绉掞級 = 褰撳墠鏃跺埢 + 骞叉壈姣       骞叉壈鏃堕棿鎺у埗鍦�(0.0, 0.01)绉�
            startTime = new Date(new Date().getTime() + (long) (10L * disturbedTime));
        }

        // triggerName = uuid 闃叉瑙﹀彂鍣ㄥ悕鍐茬獊
        String triggerName = name;
        if (!StringUtils.hasText(triggerName)) {
            triggerName = UUID.randomUUID().toString();
        }
        final SimpleTriggerImpl simpleTrigger = new SimpleTriggerImpl();

        simpleTrigger.setName(triggerName);
        simpleTrigger.setGroup(group);
        simpleTrigger.setStartTime(startTime);
        simpleTrigger.setRepeatCount(0);
        simpleTrigger.setRepeatInterval(0);
        simpleTrigger.setJobKey(new JobKey(jobId));
        return simpleTrigger;
    }

    /**
     * 鍒涘缓閫氱敤鐨凧ob鏄庣粏銆�
     *
     * @param jobKey jobKey
     * @return Job鏄庣粏
     */
    public JobDetail createJobDetail(final JobKey jobKey) {
        return this.createJobDetail(jobKey, null);
    }

    /**
     * 鍒涘缓Job鏄庣粏銆�
     *
     * @param jobKey jobKey
     * @param jobClass Job绫�
     * @return Job鏄庣粏
     */
    public <T extends Job> JobDetail createJobDetail(final JobKey jobKey, final Class<T> jobClass) {

        final JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setJobClass(jobClass != null ? jobClass : this.getCommonJobClass(jobKey));
        jobDetail.setKey(jobKey);
        jobDetail.setDurability(true);
        return jobDetail;
    }

    /**
     * 鍏抽棴Job绠＄悊鍣�
     */
    public void shutdownScheduledJobs() {
        try {
            if (CommonJobsExecutor.LOGGER.isInfoEnabled()) {
                CommonJobsExecutor.LOGGER.info("================shutdown scheduler=================");
            }
            this.scheduler.shutdown(true);
        } catch (final SchedulerException e) {
            CommonJobsExecutor.LOGGER.error(e);
        }
    }

    /**
     * 妫�鏌ob鏄惁瀛樺湪銆�
     *
     * @param jobKey jobKey
     * @return true if job is existing.
     * @throws SchedulerException
     */
    public boolean checkJobExists(final JobKey jobKey) throws SchedulerException {
        return this.scheduler.checkExists(jobKey);
    }

    /**
     * 妫�鏌ヨЕ鍙戝櫒鏄惁瀛樺湪銆�
     *
     * @param triggerKey triggerKey
     * @return true if trigger is existing.
     * @throws SchedulerException
     */
    public boolean checkTriggerExists(final TriggerKey triggerKey) throws SchedulerException {
        return this.scheduler.checkExists(triggerKey);
    }

    /**
     * 鑾峰彇璋冨害鍣ㄥ疄渚嬨��
     *
     * @return 璋冨害鍣ㄥ疄渚�
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * @param scheduler scheduler
     */
    public void setScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 娉ㄥ唽涓�涓畾鏃惰Е鍙戝櫒鐨勬淳鍙戠洃鍚櫒銆�
     *
     * @param listener 瀹氭椂瑙﹀彂鍣ㄧ殑娲惧彂鐩戝惉鍣�
     */
    public void registerCronTriggerScheduleListener(final CronTriggerScheduleListener listener) {
        if (this.cronTriggerScheduleListeners == null) {
            this.cronTriggerScheduleListeners = new HashSet<CronTriggerScheduleListener>(5);
        }
        this.cronTriggerScheduleListeners.add(listener);
    }

    /**
     * 鍙栬瑙﹀彂鐨勯�氱敤Job绫汇��
     *
     * @return Job鐨勭被
     */
    protected Class<? extends Job> getCommonJobClass(final JobKey jobKey) {
        boolean isAllowConcurrentExecute = true;
        if (this.jobConcurrentInfoHolder != null) {
            isAllowConcurrentExecute = this.jobConcurrentInfoHolder.isAllowConcurrentExecute(jobKey.getName());
        }
        if (isAllowConcurrentExecute) {
            return CommonSchedulerJob.class;
        } else {
            return DisallowConcurrentExectionJob.class;
        }
    }

    /**
     * @param trigger
     */
    private void fireTriggerScheduleEvent(final Trigger trigger) {
        if ((this.cronTriggerScheduleListeners == null) || (this.cronTriggerScheduleListeners.size() == 0)) {
            return;
        }
        if (trigger instanceof org.quartz.CronTrigger) {
            for (final CronTriggerScheduleListener listener : this.cronTriggerScheduleListeners) {
                listener.registerCronTrigger((org.quartz.CronTrigger) trigger);
            }
        }
    }

    /**
     * @param trigger
     */
    private void fireTriggerRemovedEvent(final Trigger trigger) {
        if ((this.cronTriggerScheduleListeners == null) || (this.cronTriggerScheduleListeners.size() == 0)) {
            return;
        }
        if (trigger instanceof org.quartz.CronTrigger) {
            for (final CronTriggerScheduleListener listener : this.cronTriggerScheduleListeners) {
                listener.removeCronTrigger((org.quartz.CronTrigger) trigger);
            }
        }
    }

    /**
     * @param jobConcurrentInfoHolder jobConcurrentInfoHolder
     */
    public void setJobConcurrentInfoHolder(final JobConcurrentInfoHolder jobConcurrentInfoHolder) {
        this.jobConcurrentInfoHolder = jobConcurrentInfoHolder;
    }

    private boolean isTriggerChanged(final Trigger oldTrigger, final Trigger newTrigger) throws SchedulerException {
        if (oldTrigger.getClass() != newTrigger.getClass()) {
            return true;
        }
        if (oldTrigger instanceof CronTriggerImpl) {
            final CronTriggerImpl oldCronTrigger = (CronTriggerImpl) oldTrigger;
            final CronTriggerImpl newCronTrigger = (CronTriggerImpl) newTrigger;
            return !oldCronTrigger.getCronExpression().equals(newCronTrigger.getCronExpression());
        } else if (oldTrigger instanceof SimpleTriggerImpl) {
            final SimpleTriggerImpl oldSimpleTrigger = (SimpleTriggerImpl) oldTrigger;
            final SimpleTriggerImpl newSimpleTrigger = (SimpleTriggerImpl) newTrigger;
            return (oldSimpleTrigger.getRepeatInterval() != newSimpleTrigger.getRepeatInterval()) || (oldSimpleTrigger
                    .getRepeatCount() != newSimpleTrigger.getRepeatCount());
        } else {
            return false;
        }
    }

    /**
     * Job 骞跺彂灞炴�ф槸鍚﹀彉鏇�
     *
     * @param oldTrigger
     * @param newTrigger
     * @return
     * @throws SchedulerException
     */
    private boolean isAllowCurrencyChanged(final Trigger oldTrigger, final Trigger newTrigger)
            throws SchedulerException {
        final Class<?> oldJobClass = this.scheduler.getJobDetail(oldTrigger.getJobKey()).getJobClass();
        final Class<?> newJobClass = this.getCommonJobClass(newTrigger.getJobKey());
        return oldJobClass != newJobClass;
    }
}
