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
package org.talend.mdm.repository.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.widgets.SchematronExpressBuilder;


/**
 * DOC achen  class global comment. Detailled comment
 */
public class SchematronExpressBuilderR extends SchematronExpressBuilder {

    /**
     * DOC achen SchematronExpressBuilderR constructor comment.
     *
     * @param parent
     * @param value
     * @param conceptName
     */
    public SchematronExpressBuilderR(Composite parent, String value, String conceptName) {
        super(parent, value, conceptName);
        // TODO Auto-generated constructor stub
    }

    /**
     * DOC achen SchematronExpressBuilderR constructor comment.
     *
     * @param parent
     * @param value
     * @param conceptName
     * @param page
     */
    public SchematronExpressBuilderR(Composite parent, String value, String conceptName, boolean isAbsoluteXPath,
            boolean isSchematron) {
        super(parent, value, conceptName, isAbsoluteXPath, isSchematron);
    }

    @Override
    protected XpathSelectDialog getXPathSelectDialog() {
        XpathSelectDialog2 xpathSelectDlg = new XpathSelectDialog2(parent.getShell(),
                Messages.SchematronExpressBuilder_selectXPath, MDMRepositoryView.show().getSite(), false, null, isAbsoluteXPath);
        return xpathSelectDlg;
    }
}
