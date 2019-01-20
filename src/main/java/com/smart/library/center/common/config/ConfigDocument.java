package com.smart.library.center.common.config;

import lombok.Data;

import java.util.Map;


@Data
public class ConfigDocument {
    private Object id;
    private String name;
    private Map<String, Object> config;
}
