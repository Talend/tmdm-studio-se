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
package com.amalto.workbench.actions;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDElementDeclaration;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDDeleteElementAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDDeleteElementAction.class);

    private XSDElementDeclaration xsdElem = null;

    public XSDDeleteElementAction(DataModelMainPage page) {
        super(page);
        // this.page = page;
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        setText(Messages.XSDDeleteElementAction_Text);
        setToolTipText(Messages.XSDDeleteElementAction_ActionTip);
    }

    /**
     * Author: Fiu this function is meant to support deletion on key press which can pass a specific object to be
     * deleted
     *
     * @param toDel
     */
    public void run(Object toDel) {
        if (!(toDel instanceof XSDElementDeclaration)) {
            return;
        }
        xsdElem = (XSDElementDeclaration) toDel;

        super.run();
    }

    public IStatus doAction() {
        try {

            // xsdElem is to support the multiple delete action on key press,
            // which each delete action on element must be explicit passed a xsdElem to
            // delete
            XSDElementDeclaration decl = xsdElem;
            if (decl == null) {
                ISelection selection = page.getTreeViewer().getSelection();
                decl = (XSDElementDeclaration) ((IStructuredSelection) selection).getFirstElement();
            }

            ArrayList<Object> objList = new ArrayList<Object>();
            IStructuredContentProvider provider = (IStructuredContentProvider) page.getTreeViewer().getContentProvider();
            Object[] all = Util.getAllObject(page.getSite(), objList, provider);
            Util.deleteReference(decl, all);

            // remove declaration
            schema.getContents().remove(decl);

            schema.update();

            xsdElem = null;
            page.refresh();
            page.markDirtyWithoutCommit();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDDeleteElementAction_ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    public void setXSDTODel(XSDElementDeclaration elem) {
        xsdElem = elem;
    }
}
