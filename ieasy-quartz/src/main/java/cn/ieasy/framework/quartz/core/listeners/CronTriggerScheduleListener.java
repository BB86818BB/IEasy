/**
 *
 */
package cn.ieasy.framework.quartz.core.listeners;

/**
 * 定时触发器Job派发监听。
 */
public interface CronTriggerScheduleListener {

    void registerCronTrigger(org.quartz.CronTrigger cronTrigger);

    void removeCronTrigger(org.quartz.CronTrigger cronTrigger);
}
