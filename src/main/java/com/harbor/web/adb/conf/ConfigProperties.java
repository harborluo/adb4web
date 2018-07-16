package com.harbor.web.adb.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Created by harbor on 7/16/2018.
 */
@Configuration
@PropertySource("classpath:config.properties")
@ConfigurationProperties(prefix = "config")
public class ConfigProperties {

    private List<AppDefinition> androidApps;

    public List<AppDefinition> getAndroidApps() {
        return androidApps;
    }

    public void setAndroidApps(List<AppDefinition> androidApps) {
        this.androidApps = androidApps;
    }

}
