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
package org.talend.mdm.repository.core.validate.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;


/**
 * @author sbliu
 *
 */
public class ViewValidationReport implements ValidationReport {

    boolean isValid;
    private String fileURI;

    public ViewValidationReport(String fileURI){
        this.fileURI = fileURI;
    }
    
    @Override
    public String getFileURI() {
        return fileURI;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
    
    @Override
    public ValidationMessage[] getValidationMessages() {
        return messages.toArray(new ValidationMessage[0]);
    }

    @Override
    public HashMap getNestedMessages() {
        return null;
    }

}
