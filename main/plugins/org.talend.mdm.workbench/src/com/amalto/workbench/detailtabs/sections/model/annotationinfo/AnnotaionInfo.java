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
package com.amalto.workbench.detailtabs.sections.model.annotationinfo;

import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.model.ISubmittable;

public abstract class AnnotaionInfo implements ISubmittable {

    private XSDComponent sourceXSDComponent;

    public AnnotaionInfo(XSDComponent sourceXSDComponent) {

        this.sourceXSDComponent = sourceXSDComponent;
    }

    public XSDComponent getSourceXSDComponent() {
        return sourceXSDComponent;
    }
}
