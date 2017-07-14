// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.models.sync;

import java.util.List;

import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Element;

import com.amalto.workbench.models.IAnnotationConst;

/**
 * created by hbhong on Jul 12, 2017 Detailled comment
 *
 */
public class EntitySyncProcessor extends AnnotationSyncProcessor implements IAnnotationConst {

    public static void syncNameForAnnotation(XSDElementDeclaration decl, String oldName, String newName) {
        XSDAnnotation xsdAnnotation = getAnnotation(decl);

        syncInnerAnnotation(KEY_LOOKUP_FIELD, xsdAnnotation, oldName, newName);
    }

    private static void syncInnerAnnotation(String appInfoType, XSDAnnotation xsdAnnotation, String oldName, String newName) {

        List<Element> elements = getAppInfos(xsdAnnotation, appInfoType);
        if (elements.size() > 0) {
            for (Element element : elements) {
                String value = element.getFirstChild().getNodeValue();
                String newValue = value.replaceFirst(oldName + "/", newName + "/"); //$NON-NLS-1$ //$NON-NLS-2$
                updateAppInfo(element, newValue);

            }
            xsdAnnotation.updateElement();
        }

    }
}
