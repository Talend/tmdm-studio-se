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
package org.talend.mdm.repository.core.validate.datamodel.model;

import org.talend.mdm.repository.core.validate.IModelMarkerConst;

/**
 * created by HHB on 2013-1-8 Detailled comment
 *
 */
public interface IDataModelMarkerConst extends IModelMarkerConst {

    public static final String ENTITY = "entity"; //$NON-NLS-1$

    public static final String PATH = "path"; //$NON-NLS-1$

    public static final String DOM_ELEMENT = "domElement"; //$NON-NLS-1$

    public static final String DATA_MODEL = "dataModel"; //$NON-NLS-1$

    public static final String ELEMENT_TYPE = "elementType"; //$NON-NLS-1$

    public static final String OPEN_IN_SOURCE = "openInSource"; //$NON-NLS-1$

    /**
     * the value come from IComponentValidateVisitor's MSG_GROUP_XXX
     */
    public static final String MSG_GROUP = "messageGroup"; //$NON-NLS-1$
}
