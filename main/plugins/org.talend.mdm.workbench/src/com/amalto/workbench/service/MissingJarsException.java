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
package com.amalto.workbench.service;

import com.amalto.workbench.utils.XtentisException;

/**
 * created by HHB on 2013-10-24
 *
 *
 */
public class MissingJarsException extends XtentisException {

    public MissingJarsException() {
        super();
    }

    public MissingJarsException(String message) {
        super(message);
    }

    public MissingJarsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingJarsException(Throwable cause) {
        super(cause);
    }
}
