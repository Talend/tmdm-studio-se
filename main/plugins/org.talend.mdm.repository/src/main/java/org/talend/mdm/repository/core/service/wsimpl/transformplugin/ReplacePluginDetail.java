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
public class ReplacePluginDetail extends AbstractPluginDetail {

    /**
     * DOC hbhong ProjectPluginDetail constructor comment.
     *
     * @param twoLetterLanguageCode
     */
    public ReplacePluginDetail(String twoLetterLanguageCode) {
        super(twoLetterLanguageCode);
    }

    private static final String INPUT_TEXT = "text"; //$NON-NLS-1$

    private static final String OUTPUT_TEXT = "replaced"; //$NON-NLS-1$

    protected String[] getInputVarNames() {
        return new String[] { INPUT_TEXT };
    }

    protected String[] getOutputVarNames() {
        return new String[] { OUTPUT_TEXT };
    }

    public String getDescription() {
        if ("fr".matches(twoLettersLanguageCode.toLowerCase())) //$NON-NLS-1$
            return "Execute un regexp sur un text, remplace par la valeur en paramètre et retourne le résultat"; //$NON-NLS-1$
        return "Executes a regexp on a text, replaces with parameter value and returns the result"; //$NON-NLS-1$
    }

    public String getDocumentation() {
        return "The replace plugin execute substitution on a text\n" //$NON-NLS-1$
                + "The plugin will replace every subsequence of the input sequence that matches the pattern with the given replacement string\n" //$NON-NLS-1$
                + "\n" + "\n" + "Parameters\n" + "   regexp [mandatory]: the regexp expression to run" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                + "   contentType [optional]: the content type of the result of the replace" + "\n" //$NON-NLS-1$ //$NON-NLS-2$
                + "   replacement [mandatory]: the replacement string " + "\n" + "\n" + "\n" + "Example" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                + "   <parameters>" + "\n" + "       <regexp>(\"[^\",]*)(,)([^\",]*\")</regexp>" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "       <contentType>text/plain</contentType>" + "\n" + "       <replacement>$1##AMALTO##$3</replacement>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "\n" + "   </parameters>" + "\n" + "\n" + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

    public String getParametersSchema() {
        return null;
    }

    public String getJNDIName() {
        return "replace"; //$NON-NLS-1$
    }
}
