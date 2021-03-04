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
package com.amalto.workbench.widgets.xmleditor;

public class ExtensibleContentEditorPageSaveException extends Exception {

    private static final long serialVersionUID = -6298820321342916306L;

    public ExtensibleContentEditorPageSaveException(String msg) {
        super(msg);
    }

    public ExtensibleContentEditorPageSaveException(String msg, Exception e) {
        super(msg, e);
    }
}
