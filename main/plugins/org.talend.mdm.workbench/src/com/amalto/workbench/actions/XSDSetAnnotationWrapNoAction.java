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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.AnnotationOrderedListsDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.datamodel.SchemaTreeContentProvider;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

/**
 * set the Roles with Hidden Accesses when selections is multiple
 * 
 * @author liyanmei
 */
public class XSDSetAnnotationWrapNoAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDSetAnnotationWrapNoAction.class);

    protected AnnotationOrderedListsDialog dlg = null;

    protected String dataModelName;

    protected boolean isChanged = false;

    public XSDSetAnnotationWrapNoAction(DataModelMainPage page, String dataModelName) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.SECURITYANNOTATION.getPath()));
        setText(Messages.XSDSetXXAction_SetRoleWithNoAccess);
        setToolTipText(Messages.XSDSetXXAction_SetRoleCannotSeeField);
        this.dataModelName = dataModelName;
    }

    public IStatus doAction() {
        try {

            schema = ((SchemaTreeContentProvider) page.getTreeViewer().getContentProvider()).getXsdSchema();
            IStructuredSelection selections = (TreeSelection) page.getTreeViewer().getSelection();
            XSDComponent xSDCom = null;
            XSDAnnotationsStructure struc = new XSDAnnotationsStructure(xSDCom);
            dlg = getNewAnnotaionOrderedListsDialog(Collections.EMPTY_LIST);
            dlg.setBlockOnOpen(true);
            int ret = dlg.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            for (Iterator iterator = selections.iterator(); iterator.hasNext();) {
                Object toHid = (Object) iterator.next();
                if (toHid instanceof Element) {
                    TreePath tPath = ((TreeSelection) selections).getPaths()[0];
                    for (int i = 0; i < tPath.getSegmentCount(); i++) {
                        if (tPath.getSegment(i) instanceof XSDAnnotation)
                            xSDCom = (XSDAnnotation) (tPath.getSegment(i));
                    }
                } else
                    xSDCom = (XSDComponent) toHid;
                struc = new XSDAnnotationsStructure(xSDCom);
                if (struc.getAnnotation() == null) {
                    throw new RuntimeException(Messages.bind(Messages.XSDSetXXAction_UnableEditAnnotation, xSDCom.getClass().getName()));
                }

                struc.setAccessRole(dlg.getXPaths(), dlg.getRecursive(), (IStructuredContentProvider) page.getTreeViewer()
                        .getContentProvider(), "X_Hide");//$NON-NLS-1$

                if (struc.hasChanged())
                    isChanged = true;
            }

            if (isChanged) {
                page.refresh();
                page.getTreeViewer().expandToLevel(xSDCom, 2);
                page.markDirty();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.XSDSetXXAction_ErrorSetNoAccess + e.getLocalizedMessage());
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    protected AnnotationOrderedListsDialog getNewAnnotaionOrderedListsDialog(Collection<String> values) {
        return new AnnotationOrderedListsDialog(new ArrayList(values), new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                dlg.close();
            }
        }, page.getSite().getShell(), Messages.XSDSetXXAction_RolesCannotAccessField, "Roles", page, //$NON-NLS-1$
                AnnotationOrderedListsDialog.AnnotationHidden_ActionType, dataModelName);
    }
}
