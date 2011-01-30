package com.amalto.workbench.widgets.xmleditor.util;

public class WorkflowInfo {

    private String name = "";

    private String version = "";

    public WorkflowInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
