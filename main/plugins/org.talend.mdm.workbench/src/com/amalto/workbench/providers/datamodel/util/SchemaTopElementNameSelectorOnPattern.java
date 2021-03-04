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
package com.amalto.workbench.providers.datamodel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.xsd.XSDElementDeclaration;

import com.amalto.workbench.utils.SchemaElementNameFilterDes;

class SchemaTopElementNameSelectorOnPattern implements SchemaTopElementNameSelector {

    protected SchemaItemLabelCreator labelExtractor;

    protected SchemaElementNameFilterDes nameFitlerDes;

    protected List<Pattern> patterns = new ArrayList<Pattern>();

    protected SchemaTopElementNameSelectorOnPattern(SchemaItemLabelCreator labelExtractor,
            SchemaElementNameFilterDes nameFitlerDes) {

        this.labelExtractor = labelExtractor;
        this.nameFitlerDes = nameFitlerDes;

        initPatterns(nameFitlerDes);
    }

    public boolean isSatisfiedElement(Object parentElement, Object element) {

        if (nameFitlerDes == null || !nameFitlerDes.isEnable()) {
            return true;
        }

        if (isTopElement(element)) {
            return checkElementLabel(element);
        }

        return true;
    }

    private boolean checkElementLabel(Object element) {

        for (Pattern eachPattern : patterns) {
            Matcher matcher = eachPattern.matcher(labelExtractor.getLabel(element).toLowerCase());
            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }

    protected boolean isTopElement(Object element) {
        return (element instanceof XSDElementDeclaration);
    }

    private void initPatterns(SchemaElementNameFilterDes nameFitlerDes) {

        patterns.clear();

        for (String eachPattern : nameFitlerDes.getSeparatedFilterExpressions()) {
            eachPattern = eachPattern.replaceAll("\\*", ".*");//$NON-NLS-1$//$NON-NLS-2$
            eachPattern = eachPattern.replaceAll("\\+", ".+");//$NON-NLS-1$//$NON-NLS-2$
            eachPattern = eachPattern.replaceAll("\\?", ".?");//$NON-NLS-1$//$NON-NLS-2$
            eachPattern = eachPattern.toLowerCase();
            patterns.add(Pattern.compile(eachPattern));
        }
    }
}
