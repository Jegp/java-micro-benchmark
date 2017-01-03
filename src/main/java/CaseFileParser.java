/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Parses cases from a YAML file.
 */
public class CaseFileParser {

    private static final YAMLFactory FACTORY = new YAMLFactory();

    private CaseFileParser() {
        // Should not be instantiated
    }

    public static Map<String, Task> parse(InputStream input) throws IOException {
        return new ObjectMapper(FACTORY).readValue(input, YamlTasks.class).tasks;
    }

    /**
     * Object mapping for Jackson data binding.
     */
    private static final class YamlTasks {
        private Map<String, Task> tasks;

        @JsonCreator
        public YamlTasks(Map<String, Task> tasks) {
            this.tasks = tasks;
        }
    }

}