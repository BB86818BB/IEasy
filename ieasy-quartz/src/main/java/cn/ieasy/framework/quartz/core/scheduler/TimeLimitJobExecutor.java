/**
 *
 */
package cn.ieasy.framework.quartz.core.scheduler;

/**
 * 有超时限制的Job执行接口。
 *
 * @version 1.0
 */
public interface TimeLimitJobExecutor<T> extends BusinessTask<T> {
    /** 最大执行次数 */
    static final int MaxRetryTimes = 100;
    /** 执行间隔 */
    static final int JobIntervalSec = 10;

    void startJob(T params);

    /**
     * 执行Job任务
     *
     * @param params
     * @param calledTimes
     */
    void runJobTask(T params, int calledTimes);

}
