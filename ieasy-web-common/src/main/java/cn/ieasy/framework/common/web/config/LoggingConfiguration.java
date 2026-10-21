package cn.ieasy.framework.common.web.config;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.appender.LogstashSocketAppender;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

@Slf4j
@Configuration
public class LoggingConfiguration {

    private final Logger log = LoggerFactory.getLogger(LoggingConfiguration.class);

    private final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String serverPort;
    @Value("${logging.dailyrolling.enabled:false}")
    private boolean enableDailyRolling;

    @Autowired
    private IeasyWebProperties ieasyWebProperties;

    private final Timer loggingRollingTimer = new Timer();

    @PostConstruct
    public void init() {
        if (this.ieasyWebProperties.getLogging().getLogstash().isEnabled()) {
            this.addLogstashAppender();
        }

        if (this.enableDailyRolling) { // 启动定时器，每天零点输出一行日志
            Date startTime = new Date();
            startTime = DateUtils.addDays(startTime, 1);
            startTime = DateUtils.setHours(startTime, 0);
            startTime = DateUtils.setMinutes(startTime, 0);
            startTime = DateUtils.setSeconds(startTime, 1);
            this.loggingRollingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        LoggingConfiguration.this.log.error("日志文件切换事件。");
                    } catch (final Throwable e) {
                    }
                }
            }, startTime, DateUtils.MILLIS_PER_DAY);
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.enableDailyRolling) { // 启动定时器，每天零点输出一行日志
            this.loggingRollingTimer.cancel();
        }
    }

    public void addLogstashAppender() {
        this.log.info("Initializing Logstash logging");

        final LogstashSocketAppender logstashAppender = new LogstashSocketAppender();
        logstashAppender.setName("LOGSTASH");
        logstashAppender.setContext(this.context);
        final String customFields = "{\"app_name\":\"" + this.appName + "\",\"app_port\":\"" + this.serverPort + "\"}";

        // Set the Logstash appender config from JHipster properties
        logstashAppender.setSyslogHost(this.ieasyWebProperties.getLogging().getLogstash().getHost());
        logstashAppender.setPort(this.ieasyWebProperties.getLogging().getLogstash().getPort());
        logstashAppender.setCustomFields(customFields);

        // Limit the maximum length of the forwarded stacktrace so that it won't exceed the 8KB UDP limit of logstash
        final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
        throwableConverter.setMaxLength(7500);
        throwableConverter.setRootCauseFirst(true);
        logstashAppender.setThrowableConverter(throwableConverter);

        logstashAppender.start();

        // Wrap the appender in an Async appender for performance
        final AsyncAppender asyncLogstashAppender = new AsyncAppender();
        asyncLogstashAppender.setContext(this.context);
        asyncLogstashAppender.setName("ASYNC_LOGSTASH");
        asyncLogstashAppender.setQueueSize(this.ieasyWebProperties.getLogging().getLogstash().getQueueSize());
        asyncLogstashAppender.addAppender(logstashAppender);
        asyncLogstashAppender.start();

        this.context.getLogger("ROOT").addAppender(asyncLogstashAppender);
    }
}
