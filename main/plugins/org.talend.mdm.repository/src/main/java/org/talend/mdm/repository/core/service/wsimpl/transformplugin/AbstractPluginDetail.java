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

import java.util.ArrayList;
import java.util.List;

import com.amalto.workbench.webservices.WSTransformerPluginV2Details;
import com.amalto.workbench.webservices.WSTransformerPluginV2VariableDescriptor;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractPluginDetail extends WSTransformerPluginV2Details {

    protected final String twoLettersLanguageCode;

    public AbstractPluginDetail(String twoLetterLanguageCode) {
        this.twoLettersLanguageCode = twoLetterLanguageCode;
        inputVariableDescriptors = getNewVarDescriptor(getInputVarNames());
        outputVariableDescriptors = getNewVarDescriptor(getOutputVarNames());
    }

    //    public final String TRANSFORMER_PLUGIN = "amalto/local/transformer/plugin/"; //$NON-NLS-1$

    public abstract String getJNDIName();

    private List<WSTransformerPluginV2VariableDescriptor> getNewVarDescriptor(String[] names) {
        if (names != null) {
            List<WSTransformerPluginV2VariableDescriptor> vars = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
            for (String name : names) {
                vars.add(new WSTransformerPluginV2VariableDescriptor(null, null, false, null, name));
            }
            return vars;
        }
        return null;
    }

    protected abstract String[] getInputVarNames();

    protected abstract String[] getOutputVarNames();
}
