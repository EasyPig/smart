package com.smart.library.center.common.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
 import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

import static java.util.Collections.singletonMap;

/**
 *
 *
 */
@Slf4j
public class CloudPropertySource extends EnumerablePropertySource<Config> {

    CompositePropertySource delegate;

    public CloudPropertySource(Config source) {
        super("elementsPropertySource", source);
        this.delegate = new CompositePropertySource("delegate");
        addPropertySource("config.public", source.getPublic());
        addPropertySource("config.project", source.getProject());
    }

    @Override public String[] getPropertyNames() {
        return delegate.getPropertyNames();
    }

    @Override public Object getProperty(String name) {
        return delegate.getProperty(name);
    }

    private void addPropertySource(String prefix, Object config) {

        try {
            byte[] bytes = new YAMLMapper().writeValueAsBytes(singletonMap(prefix, config));
            new YamlPropertySourceLoader()
                    .load(prefix, new ByteArrayResource(bytes))
                    .forEach(delegate::addPropertySource);
        } catch (IOException e) {
            //log.error("Failed to add property source", e);
        }
    }
}
