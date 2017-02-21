// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.xsd.XSDElementDeclaration;

import com.amalto.workbench.dialogs.AddBrowseItemsWizard;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDNewBrowseItemViewAction extends AbstractXSDNewAction {

    private static Log log = LogFactory.getLog(XSDNewBrowseItemViewAction.class);

    protected DataModelMainPage page;

    private List<XSDElementDeclaration> declList = new ArrayList<XSDElementDeclaration>();

    public XSDNewBrowseItemViewAction(DataModelMainPage page) {
        super();

        this.page = page;
        setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
        setText(Messages.XSDNewBrowseItemViewAction_Text);
        setToolTipText(Messages.XSDNewBrowseItemViewAction_Text);
    }

    protected boolean isDirty() {

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage activePage = window.getActivePage();
            if (activePage != null) {
                return activePage.getActiveEditor().isDirty();
            }
        }
        return false;
    }

    protected void saveEditor() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage activePage = window.getActivePage();
            if (activePage != null) {
                activePage.saveEditor(activePage.getActiveEditor(), false);
            }
        }
    }

    private String getDisplayName() {
        return ((XSDEditor) getEditorPart()).getdMainPage().getXObject().getDisplayName();
    }

    @Override
    public void doRun() {

        if (isDirty()) {
            boolean save = MessageDialog.openConfirm(getShell(), Messages.SaveResource,
                    Messages.bind(Messages.modifiedChanges, getDisplayName()));
            if (save) {
                saveEditor();
            } else {
                return;
            }
        }

        declList.clear();
        List elementDeclarations = getSelectedXSDElementDeclarations();
        for (Object obj : elementDeclarations) {
            if (obj instanceof XSDElementDeclaration) {
                XSDElementDeclaration declaration = (XSDElementDeclaration) obj;
                if (Util.getParent(obj) == obj) {
                    declList.add(declaration);
                }
            }
        }

        if (!declList.isEmpty()) {
            //
            AddBrowseItemsWizard wizard = getAddBrowseItemsWizard(declList);
            WizardDialog dialog = new WizardDialog(getShell(), wizard);
            dialog.open();
        }
    }

    protected List getSelectedXSDElementDeclarations() {
        IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
        return selection.toList();
    }

    protected Shell getShell() {
        return page.getSite().getShell();
    }

    private AddBrowseItemsWizard getAddBrowseItemsWizard(List<XSDElementDeclaration> declList) {
        Object object = getEditorPart().getAdapter(AddBrowseItemsWizard.class);
        if (object != null) {
            AddBrowseItemsWizard wizard = (AddBrowseItemsWizard) object;
            wizard.setDeclarations(declList);
            return wizard;
        } else {

            return new AddBrowseItemsWizard(((XSDEditor) getEditorPart()).getdMainPage(), declList);
        }
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    protected MultiPageEditorPart getEditorPart() {
        return ((MultiPageEditorSite) page.getEditorSite()).getMultiPageEditor();
    }
}
