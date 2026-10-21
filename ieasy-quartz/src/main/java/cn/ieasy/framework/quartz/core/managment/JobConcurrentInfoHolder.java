/**
 *
 */
package cn.ieasy.framework.quartz.core.managment;

public interface JobConcurrentInfoHolder {

    boolean isAllowConcurrentExecute(String jobKey);
}
