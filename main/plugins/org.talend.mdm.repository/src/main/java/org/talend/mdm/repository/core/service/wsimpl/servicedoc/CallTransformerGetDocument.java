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
package org.talend.mdm.repository.core.service.wsimpl.servicedoc;

import org.talend.mdm.repository.core.service.ws.AbstractGetDocument;
import org.talend.mdm.repository.i18n.Messages;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class CallTransformerGetDocument extends AbstractGetDocument {

    private static final String Param_Transformer_Name = "process"; //$NON-NLS-1$
    /**
     * DOC hbhong CallTransformerGetDocument constructor comment.
     * 
     * @param twoLettersLanguageCode
     */
    public CallTransformerGetDocument(String twoLettersLanguageCode) {
        super(twoLettersLanguageCode);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getJNDIName() {
        return "callprocess"; //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return Messages.CallTransformerGetDocument_FrenchDesc;

        return Messages.CallTransformerGetDocument_EnglishDesc;
    }

    @Override
    public String getDocument() {
        return "This service takes a single parameter: \n" + "process: the name of the process. \n\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "The process should expect to receive the content of the Item sent to the process in the DEFAULT variable \n" //$NON-NLS-1$
                + "with a content-type of text/xml. \n\n" + "Example: " + Param_Transformer_Name + "=tiscall_test"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
