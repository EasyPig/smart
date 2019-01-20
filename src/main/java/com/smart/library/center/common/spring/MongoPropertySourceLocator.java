package com.smart.library.center.common.spring;

 import com.smart.library.center.common.config.CloudPropertySource;
 import com.smart.library.center.common.config.Config;
 import org.springframework.core.env.*;

import java.util.HashMap;
import java.util.Map;
// import org.springframework.cloud.bootstrap.config.PropertySourceLocator;

import static java.util.Collections.singletonMap;

/**
 *
 *
 */
//public class MongoPropertySourceLocator implements PropertySourceLocator {
public class MongoPropertySourceLocator {
    private Config config;

    public MongoPropertySourceLocator(Config config) {
        this.config = config;
    }

    /*@Override
    public PropertySource<?> locate(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            CompositePropertySource propertySource = new CompositePropertySource("mongoProperty");
            //propertySource.addPropertySource(new CloudPropertySource(config));
            MapPropertySource springConfig = new MapPropertySource("springConfig", createSpringConfig(propertySource));
            propertySource.addPropertySource(springConfig);

            String appName = environment.getProperty("spring.application.name", "application");
            if ("application".equals(appName)) {
                Map<String, Object> appNameMap = singletonMap("spring.application.name", config.getAppName());
                propertySource.addPropertySource(new MapPropertySource("appName", appNameMap));
            } else {
                config.setAppName(appName);
            }

            return propertySource;
        }
        return null;
    }
*/
    private Map<String, Object> createSpringConfig(CompositePropertySource source) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("spring.data.mongodb.host", config.getPublic().getMongodbHost());
        map.put("spring.data.mongodb.authentication-database", "admin");
        map.put("spring.data.mongodb.username", config.getPublic().getMongodbUsername());
        map.put("spring.data.mongodb.password", config.getPublic().getMongodbPassword());
        map.put("spring.data.mongodb.port", config.getPublic().getMongodbPort());

        map.put("spring.rabbitmq.host", config.getPublic().getMqHost());
        map.put("spring.rabbitmq.port", config.getPublic().getMqPort());
        map.put("spring.rabbitmq.username", config.getPublic().getMqUsername());
        map.put("spring.rabbitmq.password", config.getPublic().getMqPassword());
        map.put("spring.rabbitmq.addresses", config.getPublic().getRabbitAddress());

        //map.put("spring.cloud.zookeeper.connect-string", config.getPublic().getZookeeper().getConnectString());
        //map.put("spring.cloud.zookeeper.discovery.root", config.getPublic().getZookeeper().getPrefix());

        map.put("security.oauth2.client.access-token-uri", config.getPublic().getApiServerUrl() + "/oauth2/access_token");
        return map;
    }
}
