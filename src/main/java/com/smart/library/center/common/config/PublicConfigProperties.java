package com.smart.library.center.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@Slf4j
@ConfigurationProperties("config.public")
public class PublicConfigProperties {
    private String platformIP;
    private int platformPort;
	private String apiServerIP;
	private int apiServerPort;
	private String apiServerUrl;

    public String getApiServerUrl() {
        if (apiServerUrl == null) {
            apiServerUrl = UriComponentsBuilder.newInstance().scheme("http").host(apiServerIP).port(apiServerPort)
                                               .build().toUriString();
            log.info("Auto generated apiServerUrl: {}", apiServerUrl);
        }
        return apiServerUrl;
    }

    private int timeoutHttp;
	private String oauth2Server;
	private String fileServer;
	private int dbConnNoAct;
	private int dbConnCheckInterval;
	private int taskStatusInterval;
	private String mongodbHost;
	private int mongodbPort;
	private String mongodbUsername;
	private String mongodbPassword;
	private String mongodbWriteMode;
	private String mongodbReadMode;
	private String mongodbRsName;
	private String mqHost;
	private int mqPort;
	private String mqUsername;
	private String mqPassword;
    private String rabbitAddress;
    private String logLevel;
	private String platformUrl;
    private String platformType;
    private ApiGateway apiGateway = new ApiGateway();
	private Zookeeper zookeeper = new Zookeeper();

	@Data
    public static class Zookeeper {
        private String host = "zookeeper";
        private int port = 2181;
        private int timeout = 60 * 1000;
        private int heartbeat = 5000;
        private String prefix = "/elements/apps";

        public String getConnectString() {
            return host + ":" + port;
        }
    }

    @Data
    public static class ApiGateway {
	    private String prefix = "/traefik/apps/";
    }
}
