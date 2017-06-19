package com.customer.blog.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by lenovo on 2017/3/24.
 */
@Configuration
//TODO mapper扫描路径需要修改
@MapperScan(basePackages = "com.lecloud.cdn.domain.mapper", sqlSessionFactoryRef = "dataSqlSessionFactory")
public class DatabaseConfiguration {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverName;

    // 配置初始化大小、最小、最大
    @Value("${druid.initialSize:1}")
    private int initialSize;
    @Value("${druid.minIdle:1}")
    private int minIdle;
    @Value("${druid.maxActive:100}")
    private int maxActive;

    // 配置获取连接等待超时的时间
    @Value("${druid.maxWait:20000}")
    private long maxWait;

    // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    @Value("${druid.timeBetweenEvictionRunsMillis:60000}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${druid.validationQuery:select 'x'}")
    private String validationQuery;

    @Value("${druid.testWhileIdle:true}")
    private boolean testWhileIdle;

    @Value("${druid.testOnBorrow:false}")
    private boolean testOnBorrow;

    @Value("${druid.testOnReturn:false}")
    private boolean testOnReturn;

    @Value("${druid.removeAbandoned:true}")
    private boolean removeAbandoned;

    @Value("${druid.removeAbandonedTimeout:180}")
    private int removeAbandonedTimeout;

    @Value("${druid.logAbandoned:true}")
    private boolean logAbandoned;

    @Value("${app.debug:false}")
    private boolean debug;

    @Bean(name = "dataSource", destroyMethod = "close", initMethod = "init")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverName);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        //调试查看程序线程泄漏的地方
        if(debug){
            dataSource.setRemoveAbandoned(removeAbandoned);
            dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
            dataSource.setLogAbandoned(logAbandoned);
        }
        return dataSource;
    }

    @Bean(name = "dataSqlSessionFactory")
    @ConditionalOnMissingBean(name = "dataSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception{
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setConfigLocation(new ClassPathResource("/mybatis-config.xml"));
        return sessionFactory.getObject();
    }

    @Bean(name = "dataSourceTrans")
    @ConditionalOnMissingClass
    public DataSourceTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }
}
