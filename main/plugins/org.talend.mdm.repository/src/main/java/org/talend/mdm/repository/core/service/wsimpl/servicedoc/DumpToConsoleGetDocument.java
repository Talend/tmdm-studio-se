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
package org.talend.mdm.repository.core.service.wsimpl.servicedoc;

import org.talend.mdm.repository.core.service.ws.AbstractGetDocument;
import org.talend.mdm.repository.i18n.Messages;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class DumpToConsoleGetDocument extends AbstractGetDocument {

    /**
     * DOC hbhong CopyOfCallJobGetDocument constructor comment.
     *
     * @param twoLettersLanguageCode
     */
    public DumpToConsoleGetDocument(String twoLettersLanguageCode) {
        super(twoLettersLanguageCode);
    }

    @Override
    public String getJNDIName() {
        return "dumptoconsole"; //$NON-NLS-1$
    }

    @Override
    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return Messages._DumpToConsoleDescription;
        return Messages._DumpToConsoleDescription;
    }

    @Override
    public String getDocument() {
        return "N/A"; //$NON-NLS-1$
    }

}
