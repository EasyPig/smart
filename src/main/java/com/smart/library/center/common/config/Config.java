package com.smart.library.center.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.smart.library.center.common.jackson.ObjectMapperFactory;
import com.smart.library.center.common.mongodb.ConfigMongoProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Data
@NoArgsConstructor
public class Config {

    private PublicConfigProperties publik;
    private Map<String, Object> project = new HashMap<>();
    private String httpServerUri;
    private String appName;

    public Config(PublicConfigProperties publik, Map<String, Object> project) {
        this.publik = publik;
        this.project = project;
    }

    public Config(MongoTemplate template, String table, String appName) {
        Map<String, Object> aPublic = findConfig(template, "public", table);
        Map<String, Object> project = findConfig(template, appName, table);
        ObjectMapper objectMapper = new ObjectMapperFactory().getMapper();
        this.publik = objectMapper.convertValue(aPublic, PublicConfigProperties.class);
        this.project = project;
        this.appName = appName;
    }

    public PublicConfigProperties getPublic() {
        return publik;
    }

    public void setPublic(Map<String, Object> project) {
        this.project = project;
    }

    /**
     * @return example "http://127.0.0.1:8080/"
     */
    public String getHttpServerUri() {
        if (this.httpServerUri == null) {
            this.httpServerUri = "http://" + publik.getApiServerIP() + ":" + publik.getApiServerPort() + "/";
        }
        return this.httpServerUri;
    }

    public String getProjectName() {
        return this.appName;
    }

    public static Config from(ConfigMongoProperties properties) {
        ServerAddress addr = new ServerAddress(properties.getHost(), properties.getPort());
        List<MongoCredential> credentials = new ArrayList<>();
        String username = properties.getUsername();
        String db = properties.getDb();
        if (!Strings.isNullOrEmpty(username)) {
            char[] password = properties.getPassword().toCharArray();
            credentials.add(MongoCredential.createCredential(username, db, password));
        }

        MongoClient mongoClient = new MongoClient(addr, credentials);
        MongoTemplate template = new MongoTemplate(mongoClient, db);
        return new Config(template, properties.getTable(), properties.getName());
    }

    public Map<String, Object> findConfig(MongoTemplate template, String name, String coll) {
        ConfigDocument doc = template.findOne(query(where("name").is(name)), ConfigDocument.class, coll);
        if (doc != null) {
            return doc.getConfig();
        }
        return new HashMap<>();
    }
}
