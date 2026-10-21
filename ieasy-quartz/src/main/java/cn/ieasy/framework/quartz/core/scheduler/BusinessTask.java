/**
 *
 */
package cn.ieasy.framework.quartz.core.scheduler;

public interface BusinessTask<T> {
    /**
     * 执行业务逻辑
     *
     * @param params
     */
    void runBusinessTask(T params);

    String getJobKey();
}
