package com.smart.library.center.common.mongodb;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.smart.library.center.common.converter.DateToLongConverter;
import com.smart.library.center.common.converter.LongToDateConverter;
import com.smart.library.center.common.converter.StringToDateConverter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.smart.library.center.common.mongodb.DBNames.SYSTEM;


/**
 * Factory of {@link MongoTemplate}, add cache layer for all database connections, share template
 * instance between same database.
 * <p>
 * Setting global mongo template configurations, include common {@link CustomConversions}, {@link MongoTypeMapper}
 */
public class MongoFactory implements Serializable {
    public final static ObjectId SYSTEM_ID = new ObjectId("0000000000000000000abcde");
    private MongoClient mongo;
    private ConcurrentMap<String, MongoTemplate> templates = new ConcurrentHashMap<>();
    private ConcurrentMap<String, GridFsTemplate> gridFsTemplates = new ConcurrentHashMap<>();
    private ConcurrentMap<String, GridFSBucket> gridFSBucketCache = new ConcurrentHashMap<>();

    private MongoCustomConversions customConversions;

    public MongoFactory(MongoClient mongo) {
        this.mongo = mongo;
        this.customConversions = defaultCustomConversions();
    }

    public MongoCollection<Document> getCollection(ObjectId oid, String collection) {
        return this.getDatabase(oid).getCollection(collection);
    }

    public MongoCollection<Document> getCollection(String dbName, String collection) {
        return this.getDatabase(dbName).getCollection(collection);
    }

    public MongoDatabase getDatabase(ObjectId oId) {
        return getDatabase(getDatabaseName(oId));
    }

    public MongoDatabase getDatabase(String dbName) {
        return this.mongo.getDatabase(dbName);
    }

    public String getDatabaseName(ObjectId id) {
        if (isSystem(id)) {
            return "ABCDE_db";
        } else {
            return id.toString().toUpperCase() + "_db";
        }
    }

    public String getDbNameByOId(ObjectId id) {
        return getDatabaseName(id);
    }

    public Mongo getMongo() {
        return this.mongo;
    }

    private MappingMongoConverter mappingMongoConverter(MongoDbFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(this.customConversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();

        return converter;
    }

    private MongoMappingContext mongoMappingContext() {
        MongoMappingContext context = new MongoMappingContext();
        context.afterPropertiesSet();
        return context;
    }

    private MongoCustomConversions defaultCustomConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new LongToDateConverter());
        converterList.add(new StringToDateConverter());
        converterList.add(new DateToLongConverter());
        return new MongoCustomConversions(converterList);
    }

    /**
     * Return a MongoTemplate instance of default database ABCDE_db
     *
     * @return MongoTemplate
     */
    public MongoTemplate getMongoTemplate() {
        return getMongoTemplate(SYSTEM);
    }

    /**
     * Get a {@link MongoTemplate} instance of required database
     *
     * @return {@link MongoTemplate}
     */
    public MongoTemplate getMongoTemplate(ObjectId oid) {
        return getMongoTemplate(getDatabaseName(oid));
    }

    public MongoTemplate getMongoTemplate(String database) {
        MongoTemplate template = templates.get(database);
        if (template != null) {
            return template;
        } else {
            SimpleMongoDbFactory factory = new SimpleMongoDbFactory(mongo, database);
            MappingMongoConverter converter = mappingMongoConverter(factory);
            template = new MongoTemplate(factory, converter);
            templates.putIfAbsent(database, template);
            return templates.get(database);
        }
    }

    /**
     * Get a MongoTemplate instance of required database
     *
     * @param database database name
     * @return GridFsTemplate
     */
    public GridFsTemplate getGridFsTemplate(String database) {
        GridFsTemplate template = gridFsTemplates.get(database);
        if (template != null) {
            return template;
        } else {
            MongoTemplate mongoTemplate = getMongoTemplate(database);
            MongoDbFactory factory = mongoTemplate.getMongoDbFactory();
            MongoConverter converter = mongoTemplate.getConverter();
            gridFsTemplates.putIfAbsent(database, new GridFsTemplate(factory, converter));
            return gridFsTemplates.get(database);
        }
    }

    /**
     * Get a {@link GridFsTemplate} instance of organization database
     *
     * @param oid organization id
     * @return {@link GridFsTemplate}
     */
    public GridFsTemplate getGridFsTemplate(ObjectId oid) {
        return getGridFsTemplate(getDatabaseName(oid));
    }

    public GridFSBucket getGridFSBucketTemplate(ObjectId oid,String bucketName) {
        String database = getDatabaseName(oid);
        return GridFSBuckets.create(getMongoTemplate(database).getDb(),bucketName);
    }

    /**
     * Return a {@link GridFsTemplate} instance of default database ABCDE_db
     *
     * @return {@link GridFsTemplate}
     */
    public GridFsTemplate getGridFsTemplate() {
        return getGridFsTemplate(SYSTEM);
    }


    /**
     * Get a {@link MongoTemplate} instance of organization database, the template is thread safe so we
     * share templates of same organizations.
     *
     * @deprecated use {@link MongoFactory#getMongoTemplate(ObjectId)} instead
     * @param oId organization id
     * @return {@link MongoTemplate}
     */
    @Deprecated
    public MongoTemplate getMongoTemplateByOId(ObjectId oId) {
        return getMongoTemplate(getDatabaseName(oId));
    }

    /**
     * @deprecated use {@link MongoFactory#getMongoTemplate(String)} instead
     * @param database
     * @return
     */
    @Deprecated
    public MongoTemplate getMongoTemplateByDBName(String database) {
        return getMongoTemplate(database);
    }

    public static boolean isSystem(ObjectId oid) {
        return SYSTEM_ID.equals(oid);
    }

    public GridFSBucket getGridFSBucket(ObjectId oid) {
        return getGridFSBucket(oid, null);
    }

    public GridFSBucket getGridFSBucket(String database) {
        return getGridFSBucket(database, null);
    }

    public GridFSBucket getGridFSBucket(ObjectId oid, String bucket) {
        return getGridFSBucket(getDatabaseName(oid), bucket);
    }

    public GridFSBucket getGridFSBucket(String database, String bucket) {
        String cacheKey = bucketCacheKey(database, bucket);
        GridFSBucket gridFSBucket = gridFSBucketCache.get(cacheKey);
        MongoDatabase db = getMongoTemplate(database).getDb();
        if (gridFSBucket == null) {
            gridFSBucket = bucket == null ? GridFSBuckets.create(db) : GridFSBuckets.create(db, bucket);
            gridFSBucketCache.putIfAbsent(cacheKey, gridFSBucket);
        }
        return gridFSBucket;
    }

    private String bucketCacheKey(String database, String bucket) {
        return bucket == null ? database : database + "." + bucket;
    }

    public void setCustomConversions(MongoCustomConversions customConversions) {
        this.customConversions = customConversions;
    }
}
