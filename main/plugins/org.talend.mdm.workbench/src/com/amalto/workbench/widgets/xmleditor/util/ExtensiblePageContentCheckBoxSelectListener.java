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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditorPage;
import com.amalto.workbench.widgets.xmleditor.pagecontent.ExtensibleXMLEditorPageContent;

public class ExtensiblePageContentCheckBoxSelectListener extends SelectionAdapter {

    private static Log log = LogFactory.getLog(ExtensiblePageContentCheckBoxSelectListener.class);

    private ExtensibleContentEditorPage parentPage;

    private Button checkBox;

    private ExtensibleXMLEditorPageContent modifiedObj;

    private String modifyMethodName;

    public ExtensiblePageContentCheckBoxSelectListener(ExtensibleContentEditorPage parentPage, Button checkBox,
            ExtensibleXMLEditorPageContent modifiedObj, String modifyMethodName) {

        this.parentPage = parentPage;
        this.checkBox = checkBox;
        this.modifiedObj = modifiedObj;
        this.modifyMethodName = modifyMethodName;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {

        if (modifiedObj == null || modifyMethodName == null)
            return;

        try {
            Method method = modifiedObj.getClass().getMethod(modifyMethodName, Boolean.class);
            method.invoke(modifiedObj, checkBox.getSelection());
            parentPage.getContent().setContent(modifiedObj.toXMLExpression());
            parentPage.notifyOnXMLDocumentChanged();
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
        }
    }
}
