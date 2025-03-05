package com.solr.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "solr.config")
public class SolrConfig {

    private String host;
    private String core;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setCore(String core) {
        this.core = core;
    }

    public String getCore() {
        return core;
    }
}
