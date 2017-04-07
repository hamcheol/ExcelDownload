package com.rp.pilot.excel.config.spring;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
public class DataSourceConfig {

	@Bean(destroyMethod = "close")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return (DataSource) DataSourceBuilder.create().build();
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
		PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();			
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(pathResolver.getResource("classpath:mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(pathResolver.getResources("classpath:mybatis/order/**/*.xml"));
		return sqlSessionFactoryBean;
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }

}