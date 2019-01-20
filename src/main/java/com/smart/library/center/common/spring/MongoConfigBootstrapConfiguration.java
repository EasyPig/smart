package com.smart.library.center.common.spring;


import com.smart.library.center.common.config.Config;
import com.smart.library.center.common.mongodb.ConfigMongoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 *
 */
@Slf4j
@Configuration
@PropertySource("classpath:mongo.properties")
@EnableConfigurationProperties(ConfigMongoProperties.class)
public class MongoConfigBootstrapConfiguration {


    @Bean
    public Config config(ConfigMongoProperties properties) {
        return Config.from(properties);
    }

    @Bean
    public MongoPropertySourceLocator mongoPropertySourceLocator(Config config) {
        return new MongoPropertySourceLocator(config);
    }
}
