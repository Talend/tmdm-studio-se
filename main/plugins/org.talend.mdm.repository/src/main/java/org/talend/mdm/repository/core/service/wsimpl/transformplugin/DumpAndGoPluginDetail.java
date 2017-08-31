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
package org.talend.mdm.repository.core.service.wsimpl.transformplugin;

import org.talend.mdm.repository.core.service.ws.AbstractPluginDetail;
import org.talend.mdm.repository.i18n.Messages;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class DumpAndGoPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     * 
     * @param twoLetterLanguageCode
     */
    public DumpAndGoPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_TEXT = "in_text"; //$NON-NLS-1$

    private static final String OUTPUT_TEXT = "out_text"; //$NON-NLS-1$

    protected String[] getInputVarNames() {
        return new String[] { INPUT_TEXT };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_TEXT };
    }

    public String getDescription() {

        String description = ""; //$NON-NLS-1$
        if (twoLettersLanguageCode.toLowerCase().equals("en")) { //$NON-NLS-1$
            description = Messages.DumpAndGoPluginDetail_PlugDesc;
        } else {
            description = Messages.BatchProjectXX_UNSupportedLan;
        }
        return description;

    }

    public String getDocumentation() {

        return "The DumpAndGo plugin can dump your input text to console and pass it to the next step. \n" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "Notes for Plugin Developers: " + "\n" + "       empty"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    }

    public String getParametersSchema() {
        return null;
    }

    public String getJNDIName() {
        return "dumpandgo"; //$NON-NLS-1$
    }
}
