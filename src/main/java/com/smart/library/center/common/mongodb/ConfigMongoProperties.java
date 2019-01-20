package com.smart.library.center.common.mongodb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 *
 */
@Data
@ConfigurationProperties("config.mongo")
public class ConfigMongoProperties {
    private String host = "localhost";
    private int port = 27017;
    private String db = "dn_config";
    private String table = "V01";
    private String username = "config";
    private String password = "config";
    private String name;

}
