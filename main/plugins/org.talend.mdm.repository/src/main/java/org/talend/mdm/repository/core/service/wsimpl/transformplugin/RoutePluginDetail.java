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
package org.talend.mdm.repository.core.service.wsimpl.transformplugin;

import org.talend.mdm.repository.core.service.ws.AbstractPluginDetail;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RoutePluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     *
     * @param twoLetterLanguageCode
     */
    public RoutePluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_PK = "item_primary_key"; //$NON-NLS-1$

    private static final String OUTPUT_PK = "item_primary_key"; //$NON-NLS-1$


    protected String[] getInputVarNames() {
        return new String[] { INPUT_PK };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_PK };
    }

    public String getDescription() {

        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return "Soumet un item au moteur d'abonnements"; //$NON-NLS-1$
        return "Submits an item to the subscription engine"; //$NON-NLS-1$

    }

    public String getDocumentation() {
        return "The Route plugin sumits an item to the Subscription Engine." + "No parameters are required" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "Parameters\n" + "   None" + "\n" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public String getParametersSchema() {
        return null;
    }

    public String getJNDIName() {
        return "route"; //$NON-NLS-1$
    }
}
