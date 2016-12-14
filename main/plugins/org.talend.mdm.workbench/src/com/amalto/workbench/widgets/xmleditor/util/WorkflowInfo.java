// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.widgets.xmleditor.util;

public class WorkflowInfo {

    private String name = "";//$NON-NLS-1$

    private String version = "";//$NON-NLS-1$

    private String poolName;
    
    public WorkflowInfo(String name, String version) {
        this.name = name;
        this.poolName = name;
        this.version = version;
    }

    public WorkflowInfo(String name, String poolName ,String version) {
    	this.name = name;
    	this.poolName = poolName;
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

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

}
