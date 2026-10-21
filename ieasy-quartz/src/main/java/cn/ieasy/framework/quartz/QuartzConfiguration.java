package cn.ieasy.framework.quartz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cn.ieasy.framework.quartz.core.conf.QuartzConfigurationAdapter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class QuartzConfiguration extends QuartzConfigurationAdapter {

    @Value("${spring.datasource.driver-class-name}")
    private String dataSourceDriver;

    /**
     * 数据库建表SQL文件路径
     *
     * @return
     */

    @Override
    protected String getSqlScriptPath() {
        if (this.dataSourceDriver.equals("org.h2.Driver")) {
            return "quartzSql/tables_h2.sql";
        }
        if (this.dataSourceDriver.equals("com.ieasy.db2.jcc.DB2Driver")) {
            return "quartzSql/tables_db2.sql";
        }
        if (this.dataSourceDriver.equals("com.mysql.jdbc.Driver")) {
            return "quartzSql/tables_mysql_innodb.sql";
        }
        if (this.dataSourceDriver.equals("org.postgresql.Driver")) {
            return "quartzSql/tables_postgres.sql";
        }
        return super.getSqlScriptPath();
    }
}
