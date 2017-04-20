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
package com.amalto.workbench.widgets.xmleditor.infoholder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.amalto.workbench.editors.TransformerMainPage;
import com.amalto.workbench.webservices.WSTransformerProcessStep;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerVariablesMapping;

public class ProcessAllCallJobVariableCandidatesHolder extends AllCallJobVariableCandidatesHolder {

    private WSTransformerV2 service;

    public ProcessAllCallJobVariableCandidatesHolder(WSTransformerV2 service) {
        this.service = service;
    }

    @Override
    public String[] getExternalInfo() {

        if (service == null) {
            return new String[0];
        }

        Set<String> inputVariables = new HashSet<String>();
        for (WSTransformerProcessStep step : service.getProcessSteps()) {
            for (WSTransformerVariablesMapping mapping : step.getInputMappings()) {
                inputVariables.add(mapping.getPipelineVariable() == null ? TransformerMainPage.DEFAULT_VAR : mapping
                        .getPipelineVariable());
            }
        }

        String[] results = inputVariables.toArray(new String[0]);

        Arrays.sort(results);

        return results;
    }

}
