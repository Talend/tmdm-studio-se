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
public class XPathPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     *
     * @param twoLetterLanguageCode
     */
    public XPathPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_XML = "xml"; //$NON-NLS-1$

    private static final String OUTPUT_TEXT = "text"; //$NON-NLS-1$

    protected String[] getInputVarNames() {
        return new String[] { INPUT_XML };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_TEXT };
    }

    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return "Execute un xpath sur un xml et retourne le résultat"; //$NON-NLS-1$
        return "Executes an XPath on an XML document and returns the result"; //$NON-NLS-1$
    }

    public String getDocumentation() {
        return "The XPath plugin executes an XPath on an xml document\n" //$NON-NLS-1$
                + "The result of the xPath may be an XML fragment (content type of text/xml) or\n" //$NON-NLS-1$
                + "A text fragment (content type text/plain) or\n" //$NON-NLS-1$
                + " A nodes set in which case the plugin will loop over the results\n" + "\n" + "\n" + "Parameters\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "   the xPath expression [mandatory]: the xpath expression to run" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   contentType [optional]: the content type of the result of the x path fragment" + "\n" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "Example" + "\n" + "   <parameters>" + "\n" + "       <xPath>//Buyer/Name[../City='Paris']</xPath>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "       <contentType>text/plain</contentType>" + "\n" + "   </parameters>" + "\n" + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "Notes for Plugin Developers: " + "\n" + "   None"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public String getParametersSchema() {
        return null;
    }

    public String getJNDIName() {
        return "xpath"; //$NON-NLS-1$
    }
}
