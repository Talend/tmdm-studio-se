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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditorPage;
import com.amalto.workbench.widgets.xmleditor.pagecontent.ExtensibleXMLEditorPageContent;

public class ExtensiblePageContentTextModifyListener implements ModifyListener {

    private static Log log = LogFactory.getLog(ExtensiblePageContentTextModifyListener.class);

    private ExtensibleContentEditorPage parentPage;

    private Text text;

    private ExtensibleXMLEditorPageContent modifiedObj;

    private String modifyMethodName;

    private String multiValueDelimiter;

    public ExtensiblePageContentTextModifyListener(ExtensibleContentEditorPage parentPage, Text text,
            ExtensibleXMLEditorPageContent modifiedObj, String modifyMethodName) {
        this(parentPage, text, modifiedObj, modifyMethodName, null);
    }

    public ExtensiblePageContentTextModifyListener(ExtensibleContentEditorPage parentPage, Text text,
            ExtensibleXMLEditorPageContent modifiedObj, String modifyMethodName, String multiValueDelimiter) {

        this.parentPage = parentPage;
        this.text = text;
        this.modifiedObj = modifiedObj;
        this.modifyMethodName = modifyMethodName;
        this.multiValueDelimiter = multiValueDelimiter;
    }

    public void modifyText(ModifyEvent e) {

        if (modifiedObj == null || modifyMethodName == null)
            return;

        if (multiValueDelimiter != null)
            doModifyTextForMultiValues();
        else
            doModifyTextForSingleValue();

    }

    private void doModifyTextForMultiValues() {
        try {
            Method method = modifiedObj.getClass().getMethod(modifyMethodName, List.class);
            method.invoke(modifiedObj, fromMultiValuesExpression(text.getText().trim(), multiValueDelimiter));
            parentPage.getContent().setContent(modifiedObj.toXMLExpression());
            parentPage.notifyOnXMLDocumentChanged();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private void doModifyTextForSingleValue() {

        try {
            Method method = modifiedObj.getClass().getMethod(modifyMethodName, String.class);
            method.invoke(modifiedObj, text.getText().trim());
            parentPage.getContent().setContent(modifiedObj.toXMLExpression());
            parentPage.notifyOnXMLDocumentChanged();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private List<String> fromMultiValuesExpression(String multiValueExpression, String delimiter) {

        List<String> results = new ArrayList<String>();

        for (String eachValue : multiValueExpression.split(delimiter))
            results.add(eachValue);

        return results;
    }
}
