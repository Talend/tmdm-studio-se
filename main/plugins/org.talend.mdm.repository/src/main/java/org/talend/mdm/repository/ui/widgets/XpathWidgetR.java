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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.widgets.XpathWidget;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XpathWidgetR extends XpathWidget {

    /**
     * DOC hbhong XpathWidgetR constructor comment.
     *
     * @param buttonName
     * @param treeParent
     * @param toolkit
     * @param parent
     * @param dialog
     * @param isButtonLeft
     * @param readOnly
     * @param dataModelName
     */
    public XpathWidgetR(String buttonName, FormToolkit toolkit, Composite parent, AMainPageV2 dialog, boolean isButtonLeft,
            boolean readOnly, String dataModelName) {
        super(buttonName, null, toolkit, parent, dialog, isButtonLeft, readOnly, dataModelName);
    }

    /**
     * DOC hbhong XpathWidgetR constructor comment.
     *
     * @param parent
     * @param treeParent
     * @param isMulti
     */
    public XpathWidgetR(Composite parent, boolean isMulti) {
        super(parent, null, isMulti);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {

        if (accommodation != null) {
            if (dlg == null) {
                dlg = new XpathSelectDialog2(accommodation.getSite().getShell(), dlgTitle, accommodation.getSite(), isMulti,
                        dataModelName

                );
                dlg.setConceptName(conceptName);
            }
        } else {
            if (dlg == null) {
                // dlg = new XpathSelectDialog2(parent.getShell(), dlgTitle, site, false, dataModelName);
                dlg = new XpathSelectDialog2(parent.getShell(), dlgTitle, MDMRepositoryView.show().getSite(), false,
                        dataModelName);
                dlg.setConceptName(conceptName);
            }
        }
        disableFocusListener();
        dlg.setLock(isLock());
        dlg.setBlockOnOpen(true);
        dlg.open();
        enableFocusListener();
        if (dlg.getReturnCode() == Window.OK) {
            descriptionText.setText(dlg.getXpath());
            dataModelName = dlg.getDataModelName();
            dlg.close();
            setOutFocus();
        } else {
            lostFocus();
        }
    }

}
