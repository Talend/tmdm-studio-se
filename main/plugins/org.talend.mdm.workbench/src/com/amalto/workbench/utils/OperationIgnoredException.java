// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

/**
 * created by HHB on 2017-3-21 Detailled comment
 *
 */
public class OperationIgnoredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperationIgnoredException() {
    }

    public OperationIgnoredException(String message) {
        super(message);
    }
}
