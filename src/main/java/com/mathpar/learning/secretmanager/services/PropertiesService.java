package com.mathpar.learning.secretmanager.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PropertiesService {
    private final PropertiesProvider propertiesProvider;
    private final String defaultNamespace;

    public PropertiesService(PropertiesProvider propertiesProvider, @Value("${mathpar.secretmanager.default-namespace:default}") String defaultNamespace) {
        this.propertiesProvider = propertiesProvider;
        this.defaultNamespace = defaultNamespace;
    }

    public String getProperty(String propertyName){
        return getProperty(defaultNamespace, propertyName);
    }

    public String getProperty(String namespace, String propertyName){
        var property = propertiesProvider.getProperty(namespace, propertyName);
        if(property == null) throw new RuntimeException(String.format("Can't find property %s in namespace %s", propertyName, namespace));
        return property;
    }

    public Map<String, String> getNamespaceProperties(String namespace){
        String properties = propertiesProvider.getNamespace(namespace);
        log.info("PROPERTIES: " + properties);
        if(properties == null) throw new RuntimeException(String.format("Can't load namespace %s", namespace));
        final Map<String, String> propertiesMap = new HashMap<>();
        Arrays.stream(properties.split("\r\n")).forEach(s -> {
            if(s.isEmpty()) return;
            var propertiesRow = s.split("=");
            propertiesMap.put(propertiesRow[0], propertiesRow[1]);
        });
        return propertiesMap;
    }
}
