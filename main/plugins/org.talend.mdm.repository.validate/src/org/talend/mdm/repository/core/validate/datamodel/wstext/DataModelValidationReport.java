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
package org.talend.mdm.repository.core.validate.datamodel.wstext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.talend.mdm.repository.core.validate.datamodel.validator.ModelValidationMessage;

/**
 * created by HHB on 2013-1-8 Detailled comment
 *
 */
public class DataModelValidationReport implements ValidationReport {

    private boolean valid = true;

    private String fileUri;

    public DataModelValidationReport(String uri) {
        this.fileUri = uri;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.wst.xml.core.internal.validation.core.ValidationReport#getFileURI()
     */
    @Override
    public String getFileURI() {
        return fileUri;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.wst.xml.core.internal.validation.core.ValidationReport#isValid()
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.wst.xml.core.internal.validation.core.ValidationReport#getValidationMessages()
     */
    @Override
    public ValidationMessage[] getValidationMessages() {

        return messages.toArray(new ValidationMessage[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.wst.xml.core.internal.validation.core.ValidationReport#getNestedMessages()
     */
    @Override
    public HashMap getNestedMessages() {
        return null;
    }

    List<ModelValidationMessageAdapter> messages = new ArrayList<ModelValidationMessageAdapter>();

    public boolean addMessage(ModelValidationMessageAdapter message) {
        if (message == null) {
            valid = false;
            return false;
        }

        messages.add(message);
        valid = true;
        return true;
    }

    public boolean addMessage(ModelValidationMessage message) {
        if (messages == null) {
            valid = false;
            return false;
        }
        ModelValidationMessageAdapter msgAdapter = new ModelValidationMessageAdapter(message);
        return addMessage(msgAdapter);
    }

    public boolean addMessages(List<ModelValidationMessage> msgs) {
        if (messages == null) {
            valid = false;
            return false;
        }
        for (ModelValidationMessage msg : msgs) {
            boolean result = addMessage(msg);
            if (!result) {
                return false;
            }
        }
        return true;
    }

}
