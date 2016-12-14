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
package com.amalto.workbench.widgets.xmleditor;

public class ExtensibleEditorContent {

    protected String content = "";//$NON-NLS-1$

    public ExtensibleEditorContent(String content) {

        if (content != null)
            this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {

        if (content == null)
            this.content = "";//$NON-NLS-1$
        else
            this.content = content;
    }
}
