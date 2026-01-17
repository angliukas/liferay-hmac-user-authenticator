package com.example.hmacauth.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.hmacauth.repository")
@EnableTransactionManagement
public class DataConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(getRequiredProperty("DB_URL", "db.url"));
        dataSource.setUsername(getRequiredProperty("DB_USERNAME", "db.username"));
        dataSource.setPassword(getRequiredProperty("DB_PASSWORD", "db.password"));

        String driverClassName = getOptionalProperty("DB_DRIVER", "db.driver");
        if (driverClassName != null) {
            dataSource.setDriverClassName(driverClassName);
        }

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.example.hmacauth.model");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.show_sql", "false");
        factory.setJpaProperties(properties);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private String getRequiredProperty(String envKey, String sysKey) {
        String value = getOptionalProperty(envKey, sysKey);
        if (value == null) {
            throw new IllegalStateException(
                "Missing required database configuration for " + envKey + " or " + sysKey
            );
        }
        return value;
    }

    private String getOptionalProperty(String envKey, String sysKey) {
        String value = System.getProperty(sysKey);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(envKey);
        }
        if (value != null && value.trim().isEmpty()) {
            return null;
        }
        return value;
    }
}
