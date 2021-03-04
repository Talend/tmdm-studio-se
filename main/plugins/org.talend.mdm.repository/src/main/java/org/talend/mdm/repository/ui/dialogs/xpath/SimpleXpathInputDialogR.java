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
package org.talend.mdm.repository.ui.dialogs.xpath;

import org.eclipse.swt.events.SelectionListener;
import org.talend.mdm.repository.i18n.Messages;

import com.amalto.workbench.dialogs.SimpleXpathInputDialog;
import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.editors.DataModelMainPage;


/**
 * DOC hbhong  class global comment. Detailled comment
 */
public class SimpleXpathInputDialogR extends SimpleXpathInputDialog {

    /**
     * DOC hbhong SimpleXpathInputDialogR constructor comment.
     *
     * @param parentPage
     * @param title
     * @param dialogMessage
     * @param initialValue
     * @param caller
     * @param dataModelName
     */
    public SimpleXpathInputDialogR(DataModelMainPage parentPage, String title, String dialogMessage, String initialValue,
            SelectionListener caller, String dataModelName) {
        super(parentPage, title, dialogMessage, initialValue, caller, dataModelName);
    }

    protected XpathSelectDialog getNewXpathSelectDialog(DataModelMainPage parentPage, String dataModelName) {
        return new XpathSelectDialog2(parentPage.getSite().getShell(), Messages.SimpleXpathInputDialogR_title, parentPage.getSite(), false, dataModelName);
    }

}
