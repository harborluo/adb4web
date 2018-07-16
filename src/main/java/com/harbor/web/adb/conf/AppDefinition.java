package com.harbor.web.adb.conf;

/**
 * Created by harbor on 7/16/2018.
 */
public class AppDefinition {

    private String name;

    private String label;

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
}
