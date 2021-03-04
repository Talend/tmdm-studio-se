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
package com.amalto.workbench.detailtabs.exception;

public class CommitException extends Exception {

    private static final long serialVersionUID = 1076006458876794715L;

    public CommitException(String msg, Exception reason) {
        super(msg, reason);
    }

    public CommitException(String msg) {
        super(msg);
    }

}
