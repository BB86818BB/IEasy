package cn.ieasy.framework.common.web.config.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;

// @Configuration
public class InfluxdbMetricWriter {

    //@Bean(name = "influxdbMetricReporter")
    public ScheduledReporter influxdbMetricReporter(final MetricRegistry metrics) throws Exception {
        //
        //        final ScheduledReporter reporter = InfluxdbReporter.forRegistry(metrics)
        //                .protocol(new HttpInfluxdbProtocol("localhost", 8086, "influxdb", "123456", "metrics_db"))
        //                .convertRatesTo(TimeUnit.SECONDS)
        //                .convertDurationsTo(TimeUnit.MILLISECONDS)
        //                .filter(MetricFilter.ALL)
        //                .skipIdleMetrics(false)
        //                .build();
        //        reporter.start(10, TimeUnit.SECONDS);
        return null;
    }
}
