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
public class RegexpPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     *
     * @param twoLetterLanguageCode
     */
    public RegexpPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_TEXT = "text"; //$NON-NLS-1$

    private static final String OUTPUT_GROUP = "group"; //$NON-NLS-1$

    protected String[] getInputVarNames() {
        return new String[] { INPUT_TEXT };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_GROUP };
    }

    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return "Execute un regexp sur un text et retourne le résultat"; //$NON-NLS-1$
        return "Executes a regexp on a text and returns the result"; //$NON-NLS-1$
    }

    public String getDocumentation() {
        return "The regexp plugin executes a regexp on a text\n" + "The plugin will loop over the matching results\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "\n" + "Parameters\n" + "   the regexp expression [mandatory]: the regexp expression to run" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "   contentType [optional]: the content type of the result of the regexp fragment" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   resultPattern [mandatory]: a sequence of characters where each pattern {x} " + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "                                   is replaced by the value of capturing group x" + "\n" + "\n" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "Example" + "\n" + "   <parameters>" + "\n" + "       <regexp>start(.*)end</regexp>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "       <contentType>text/xml</contentType>" + "\n" + "       <resultPattern><![CDATA[" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "               <result>{1}</result>" + "\n" + "       ]]></resultPattern>" + "\n" + "   </parameters>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "\n" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getParametersSchema() {
        return null;
    }

    public String getJNDIName() {
        return "regexp"; //$NON-NLS-1$
    }
}
