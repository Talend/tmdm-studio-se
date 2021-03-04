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
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.AnnotationOrderedListsDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationForeignKeyInfoAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDSetAnnotationForeignKeyInfoAction.class);

    protected AnnotationOrderedListsDialog dlg = null;

    protected String dataModelName;

    public XSDSetAnnotationForeignKeyInfoAction(DataModelMainPage page, String dataModelName) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.KEYINFO.getPath()));
        setText(Messages.SetForeignKeyInfos);
        setToolTipText(Messages.SetForeignKeyInfos);
        this.dataModelName = dataModelName;
    }

    @Override
    public IStatus doAction() {
        try {
            IStructuredSelection selection = (TreeSelection) page.getTreeViewer().getSelection();
            XSDComponent xSDCom = null;
            if (selection.getFirstElement() instanceof Element) {
                TreePath tPath = ((TreeSelection) selection).getPaths()[0];
                for (int i = 0; i < tPath.getSegmentCount(); i++) {
                    if (tPath.getSegment(i) instanceof XSDAnnotation) {
                        xSDCom = (XSDAnnotation) (tPath.getSegment(i));
                    }
                }
            } else {
                xSDCom = (XSDComponent) selection.getFirstElement();
            }
            XSDAnnotationsStructure struc = new XSDAnnotationsStructure(xSDCom);
            if (struc.getAnnotation() == null) {
                throw new RuntimeException(Messages.bind(Messages.UnableEditAnnotationType, xSDCom.getClass().getName()));
            }

            dlg = getNewAnnotaionOrderedListsDialog(struc.getForeignKeyInfos().values());


            dlg.setLock(true);
            dlg.setRetrieveFKinfos(struc.getRetrieveFKinfos());
            dlg.setFormatFKInfo(struc.getFormatForeignKeyInfo());
            dlg.setBlockOnOpen(true);
            int ret = dlg.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            struc.setForeignKeyInfos(dlg.getXPaths());
            struc.setRetrieveFKinfos(dlg.isRetrieveFKinfos());
            struc.setFormatForeignKeyInfo(dlg.getFormatFKInfo());
            if (struc.hasChanged()) {
                page.refresh();
                page.getTreeViewer().expandToLevel(xSDCom, 2);
                page.markDirty();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.ErrorForeignKey, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    protected AnnotationOrderedListsDialog getNewAnnotaionOrderedListsDialog(Collection<String> values) {
        return new AnnotationOrderedListsDialog(new ArrayList<String>(values), new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                dlg.close();
            }
        }, page.getSite().getShell(), Messages.SetForeignKeyInfos, "xPaths", page, //$NON-NLS-1$
                AnnotationOrderedListsDialog.AnnotationForeignKeyInfo_ActionType, dataModelName);
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
