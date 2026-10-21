/**
 *
 */
package cn.ieasy.framework.quartz.core;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

import cn.ieasy.framework.quartz.core.annotition.JobMapping;

/**
 * 非并发处理的Job(顺次执行)。
 *
 * <pre>
 * 由容器托管的<code>{@link org.springframework.stereotype.Service}</code>
 * Bean实例中，所有注释了<code>{@link JobMapping}</code> 的方法都能够通过执行该Job代为触发执行。
 * </pre>
 *
 * @version 1.0
 */
@Component
@DisallowConcurrentExecution
public class DisallowConcurrentExectionJob extends CommonSchedulerJob {
}
