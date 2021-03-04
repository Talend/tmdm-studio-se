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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDComponent;

public abstract class ListContentsAnnotationInfo extends AnnotaionInfo {

    protected List<String> infos = new ArrayList<String>();

    public ListContentsAnnotationInfo(XSDComponent sourceComponent, String... infos) {
        super(sourceComponent);

        for (String eachInfo : infos) {
            this.infos.add(eachInfo);
        }
    }

    public String[] getInfos() {
        return infos.toArray(new String[0]);
    }

    public String[] getValues() {
        return getInfos();
    }
}
