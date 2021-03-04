// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.utils;

public class JobInfo {

    String jobname;

    String jobversion;

    String suffix;

    String lastServerName;

    String path;


    public String getPath() {
        return this.path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getLastServerName() {
        return this.lastServerName;
    }


    public void setLastServerName(String lastServerName) {
        this.lastServerName = lastServerName;
    }

    public JobInfo(String jobname, String jobversion, String suffix) {
        this(jobname, jobversion, suffix, null, null);
    }

    public JobInfo(String jobname, String jobversion, String suffix, String path, String lastServerName) {
        this.jobname = jobname;
        this.jobversion = jobversion;
        this.suffix = suffix;
        this.path = path;
        this.lastServerName = lastServerName;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getJobversion() {
        return jobversion;
    }

    public void setJobversion(String jobversion) {
        this.jobversion = jobversion;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isZip() {
        return ".zip".equalsIgnoreCase(suffix);//$NON-NLS-1$
    }

    public boolean isWar() {
        return ".war".equalsIgnoreCase(suffix);//$NON-NLS-1$
    }
}
