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
package org.talend.mdm.repository.core.service.ws;

import com.amalto.workbench.webservices.WSServiceGetDocument;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractGetDocument extends WSServiceGetDocument {

    public AbstractGetDocument(String twoLettersLanguageCode) {
        this.twoLettersLanguageCode = twoLettersLanguageCode;
    }

    protected final String twoLettersLanguageCode;

    public abstract String getJNDIName();
}
