package com.mathpar.learning.secretmanager.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


/**
 * This provider shouldn't be reliable or effective, it's just a temporary measure for non-cloud environment
 * Furthermore, it should be called rarely so it's efficiency isn't required
 * Bulletproof behaviour and failure tolerance is strongly required though
 */
//TODO Use encrypted files -> implement decryption mechanism
@Slf4j
@Service
@Profile("production")
public class FilesystemPropertiesProvider implements PropertiesProvider{
    protected final String prefix;
    protected final String suffix;

    public FilesystemPropertiesProvider(@Value("${mathpar.secretmanager.properties-prefix:/var/properties}") String prefix,
                                        @Value("${mathpar.secretmanager.properties-suffix:.namespace}") String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        log.info("PREFIX: " + prefix);
        log.info("SUFFIX: " + suffix);

    }

    @Override
    public String getProperty(String namespace, String propertyKey) {
        try {
            String[] allProperties = loadNamespace(namespace).split("\n");

            var value = Arrays.stream(allProperties).map(property -> property.split("=")).filter(property-> property[0].equals(propertyKey)).findFirst();
            return value.orElseThrow(RuntimeException::new)[1];
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String getNamespace(String namespace) {
        try {
            return loadNamespace(namespace);
        }catch (Exception e){
            return null;
        }
    }

    protected String loadNamespace(String namespace) throws IOException {
        log.info("NAMESPACE FILE: " + Files.readString(Paths.get(prefix, namespace +suffix)));

        return Files.readString(Paths.get(prefix, namespace +suffix));
    }
}
