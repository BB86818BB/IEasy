package cn.ieasy.framework.quartz.core.conf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import cn.ieasy.framework.quartz.core.managment.JobsManager;

public class QuartzConfigurationAdapter {
    /** logger */
    private static final Log LOGGER = LogFactory.getLog(QuartzConfigurationAdapter.class);
    @Autowired
    protected DataSource dataSource;
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Autowired
    protected JobsManager clusteredJobsManager;

    @Bean(name = "quartzDbInitializer")
    @DependsOn(value = {"dataSource" })
    public DataSourceInitializer buildQuartzDataSourceInitializer() {
        final DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();

        dataSourceInitializer.setDataSource(this.dataSource);

        final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.setContinueOnError(true);
        resourceDatabasePopulator.setIgnoreFailedDrops(true);
        resourceDatabasePopulator.setSqlScriptEncoding("UTF-8");
        resourceDatabasePopulator.setScripts(new org.springframework.core.io.Resource[] {new ClassPathResource(
                this.getSqlScriptPath()) });
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        dataSourceInitializer.setEnabled(false);
        Statement stmt = null;
        try {
            final Connection conn = this.dataSource.getConnection();
            stmt = conn.createStatement();
            stmt.executeQuery("SELECT count(*) FROM QRTZ_JOB_DETAILS");
        } catch (final SQLException e) {
            dataSourceInitializer.setEnabled(true);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return dataSourceInitializer;
    }

    @Bean
    @DependsOn(value = {"quartzDbInitializer" })
    public SchedulerFactoryBean buildSchedulerFactoryBean() {
        final SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setStartupDelay(10);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactoryBean.setDataSource(this.dataSource);
        schedulerFactoryBean.setTransactionManager(this.transactionManager);
        final Map<String, Object> schedulerContextAsMap = new HashMap<String, Object>();
        schedulerContextAsMap.put("commonJobDispatcher", this.clusteredJobsManager);
        schedulerFactoryBean.setSchedulerContextAsMap(schedulerContextAsMap);
        try {
            schedulerFactoryBean.setQuartzProperties(this.getScheduleProperties());
        } catch (final IOException ex) {
            QuartzConfigurationAdapter.LOGGER
                    .warn("[classpath:app-quartz.properties] was not found, use default properties!");
        }
        return schedulerFactoryBean;
    }

    /**
     * Quartz閰嶇疆鏂囦欢
     *
     * @return
     * @throws IOException
     */
    protected Properties getScheduleProperties() throws IOException {
        ClassPathResource resource = new ClassPathResource(this.getQuartzPropertiesPath());
        if (!resource.exists()) {
            resource = new ClassPathResource("quartz.properties");
        }
        return PropertiesLoaderUtils.loadProperties(resource);
    }

    /**
     * 鏁版嵁搴撳缓琛⊿QL鏂囦欢璺緞
     *
     * @return
     */
    protected String getSqlScriptPath() {
        return "quartz/tables_mysql_innodb.sql";
    }

    protected String getQuartzPropertiesPath() {
        return "app-quartz.properties";
    }
}
