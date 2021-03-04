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
public class XSLTPluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     *
     * @param twoLetterLanguageCode
     */
    public XSLTPluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_XML = "xml"; //$NON-NLS-1$

    private static final String INPUT_PARAMETERS = "parameters"; //$NON-NLS-1$

    private static final String OUTPUT_TEXT = "text"; //$NON-NLS-1$


    protected String[] getInputVarNames() {
        return new String[] { INPUT_XML, INPUT_PARAMETERS };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_TEXT };
    }

    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return "Transforme un XML en utilisant une XSLT"; //$NON-NLS-1$
        return "Transform an XML using an XSLT"; //$NON-NLS-1$
    }

    public String getDocumentation() {
        return "Simply drop your xslt in the parameters box." //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "To xslt can do cross referencing on the fly if the output method is set to 'xml' or 'xhtml'" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "Cross-referencing is carried out AFTER the xlst is processed on ALL elements with the following attributes:" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   <Country  " //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           xrefCluster='MYCLUSTER' " //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           xrefIn='.=Country/Codes/ISO2, ../Customer/Name=[ACME]' " //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           xrefOut='Country/Name/FR'" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   >" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           <xsl:value-of select='State/CountryCode'/>" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   </Country>" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "where" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   xrefCluster is the cluster holmding the Country (concept/table) data" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   xrefIn is a list of comma separated match expressions. " //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           The left part specifies an xPath relative to the current context of the *target* document" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           or a hard coded value between brackets" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "           The right part specifies an xPath in the cluster" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   xrefOut is an xPath in the Cluster holding the final value that will be inserted in the *target* document" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "" //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   if xrefIgnore is 'true' or '1', no exception will be thrown if there is no entry in the transcodification table." //$NON-NLS-1$
                + "\n" //$NON-NLS-1$
                + "   xrefDefault is the value that's used if there is no entry in the transcodification table and xrefIgnore is true." //$NON-NLS-1$
                + "\n" + "" + "\n" + "The example above does the following:" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "   1-the xslt generates a <Country> element in the target document" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   2-the value of State/CountryCode of the source document is inserted as the value of the <Country> element" //$NON-NLS-1$
                + "\n" + "   3-the rest of the xsl transformations complete" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "   4-the system queries the Country data in cluster MYCLUSTER where " + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "           Codes/ISO2Code is equal to  State/CountryCode (the current value of the Country element)" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "           and ../Customer/Name in the target document is equal to hard coded value ACME." + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   5-the matching Country document is returned and the value in Name/FR is extracted" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   6- the value in Cuuntry of the traget document is replaced with the extracted value"; //$NON-NLS-1$

    }

    public String getParametersSchema() {
        return null;
    }

    public String getJNDIName() {
        return "xslt"; //$NON-NLS-1$
    }
}
