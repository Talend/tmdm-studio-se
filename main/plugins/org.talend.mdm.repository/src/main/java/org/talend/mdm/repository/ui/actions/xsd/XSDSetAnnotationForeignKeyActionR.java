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
package org.talend.mdm.repository.ui.actions.xsd;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xpath.SimpleXpathInputDialogR;

import com.amalto.workbench.actions.XSDSetAnnotationForeignKeyAction;
import com.amalto.workbench.dialogs.SimpleXpathInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class XSDSetAnnotationForeignKeyActionR extends XSDSetAnnotationForeignKeyAction {

    /**
     * DOC hbhong XSDSetAnnotationForeignKeyActionR constructor comment.
     *
     * @param page
     * @param dataModelName
     */
    public XSDSetAnnotationForeignKeyActionR(DataModelMainPage page, String dataModelName) {
        super(page, dataModelName);
    }

    @Override
    protected SimpleXpathInputDialog getNewSimpleXpathInputDlg(String foreignKey) {
        return new SimpleXpathInputDialogR(page, Messages.XSDSetAnnotationForeignKeyActionR_title,
                Messages.XSDSetAnnotationForeignKeyActionR_message, foreignKey, new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent e) {
                        sxid.close();
                    }
                }, dataModelName

        );
    }

}
