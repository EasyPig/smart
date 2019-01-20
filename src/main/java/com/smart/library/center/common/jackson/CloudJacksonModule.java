package com.smart.library.center.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.time.Instant;

/**
 *
 *
 */
public class CloudJacksonModule extends SimpleModule {

    public CloudJacksonModule() {
        super("elementsModule", Version.unknownVersion());

        addSerializer(ObjectId.class, new ToStringSerializer());
        addSerializer(Instant.class, new InstantSerializer());
        addSerializer(BsonObjectId.class, new JsonSerializer<BsonObjectId>() {
            @Override public void serialize(BsonObjectId value,
                                            JsonGenerator gen,
                                            SerializerProvider serializers) throws IOException {
                gen.writeString(value.getValue().toString());
            }
        });

        addDeserializer(BsonObjectId.class, new JsonDeserializer<BsonObjectId>() {
            @Override public BsonObjectId deserialize(JsonParser p,
                                                      DeserializationContext ctxt) throws IOException, JsonProcessingException {
                String value = p.getValueAsString();
                if (value == null) {
                    return null;
                } else if (ObjectId.isValid(value)) {
                    return new BsonObjectId(new ObjectId(value));
                } else {
                    throw new InvalidFormatException(p, "wrong ObjectId format", value, BsonObjectId.class);
                }
            }
        });

        addSerializer(GridFSFile.class, new JsonSerializer<GridFSFile>() {
            @Override public void serialize(GridFSFile value,
                                            JsonGenerator gen,
                                            SerializerProvider serializers) throws IOException {
                Document doc = new Document();

                doc.put("id", value.getId());
                doc.put("filename", value.getFilename());
                doc.put("length", value.getLength());
                doc.put("chunkSize", value.getChunkSize());
                doc.put("uploadDate", value.getUploadDate());
                doc.put("md5", value.getMD5());

                // Optional values
                doc.put("metadata", value.getMetadata());

                gen.writeObject(doc);
            }
        });
    }
}
