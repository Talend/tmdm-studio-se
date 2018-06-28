// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.dialogs.xsd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;

import com.amalto.workbench.dialogs.AnnotationOrderedListsDialog;
import com.amalto.workbench.dialogs.IAnnotationOrderedListsDialogExtender;
import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.editors.DataModelMainPage;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class AnnotationOrderedListsDialogR extends AnnotationOrderedListsDialog {

    /**
     * DOC hbhong AnnotationOrderedListsDialogR constructor comment.
     * 
     * @param xPaths
     * @param caller
     * @param parentShell
     * @param title
     * @param columnName
     * @param parentPage
     * @param actionType
     * @param dataModelName
     */
    public AnnotationOrderedListsDialogR(ArrayList<String> xPaths, SelectionListener caller, Shell parentShell, String title,
            String columnName, DataModelMainPage parentPage, int actionType, String dataModelName) {
        super(xPaths, caller, parentShell, title, columnName, parentPage, actionType, dataModelName);
    }

    public AnnotationOrderedListsDialogR(SelectionListener caller, Shell parentShell, String title, DataModelMainPage parentPage,
            int actionType, String dataModelName, IAnnotationOrderedListsDialogExtender extender) {
        super(caller, parentShell, title, parentPage, actionType, dataModelName, extender);
    }

    @Override
    protected List<String> getAllRolesStr() {
        return RepositoryQueryService.findAllRoleNames();
    }

    @Override
    protected XpathSelectDialog getNewXpathSelectDialog(DataModelMainPage parentPage, String dataModelName) {
        return new XpathSelectDialog2(parentPage.getSite().getShell(), Messages.AnnotationXXDialogR_SelectXPath, parentPage.getSite(), false,
                dataModelName);
    }

}
