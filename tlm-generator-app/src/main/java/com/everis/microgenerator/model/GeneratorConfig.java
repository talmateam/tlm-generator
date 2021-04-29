package com.everis.microgenerator.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties("configtemplates")
public class GeneratorConfig {

    private List<Template> templates = new ArrayList<>();

    @Data
    public static class Group {
        private String name;
        private String displayName;
        private String description;
        private String icon;
        private List<Property> properties;
    }

    @Data
    public static class Childs {
        private String name;
        private String displayName;
        private String inputType;
        private String defaultValue;
        private String active;
    }

    @Data
    public static class Property {
        private String name;
        private String displayName;
        private String inputType;
        private String defaultValue;
        private String active;
        private List<Childs> childs;
    }

    @Data
    public static class Template {
        private  String name;
        private  String displayName;
        private  String language;
        private  String framework;
        private  String gitUrl;
        private  String gitUrlReadme;
        private  List<Group> groups;
    }

}
