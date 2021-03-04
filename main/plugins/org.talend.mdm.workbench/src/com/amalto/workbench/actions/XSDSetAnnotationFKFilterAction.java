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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.FKFilterDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationFKFilterAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDSetAnnotationFKFilterAction.class);

    protected FKFilterDialog fkd = null;

    protected String dataModelName;

    public XSDSetAnnotationFKFilterAction(DataModelMainPage page, String dataModelName) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.FILTER_PS.getPath()));
        setText(Messages.SetForeignKeyFilter);
        setToolTipText(Messages.SetForeignKeyFilter);
        this.dataModelName = dataModelName;
    }

    @Override
    public IStatus doAction() {
        try {
            IStructuredSelection selection = (TreeSelection) page.getTreeViewer().getSelection();
            XSDComponent xSDCom = null;
            String conceptName = null;
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
            if (xSDCom instanceof XSDElementDeclaration) {
                conceptName = xSDCom.getElement().getAttributes().getNamedItem("name").getNodeValue();//$NON-NLS-1$
            }
            if (xSDCom instanceof XSDParticle) {
            }
            XSDAnnotationsStructure struc = null;
            if (xSDCom != null) {
                struc = new XSDAnnotationsStructure(xSDCom);
            }
            if (struc == null || struc.getAnnotation() == null) {
                throw new RuntimeException(Messages.bind(Messages.UnableEditAnnotationType, xSDCom.getClass().getName()));
            }

            fkd = getNewFKFilterDialog(page.getSite().getShell(), struc.getFKFilter(), page, conceptName);
            fkd.setDataModel(dataModelName);
            fkd.setLock(true);
            fkd.setBlockOnOpen(true);
            int ret = fkd.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            String fkfilter = fkd.getFilter();
            struc.setFKFilter(fkfilter);

            if (struc.hasChanged()) {
                page.refresh();
                page.getTreeViewer().expandToLevel(xSDCom, 2);
                page.markDirty();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.ErrorFKFilter, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    protected FKFilterDialog getNewFKFilterDialog(Shell shell, String filter, DataModelMainPage page, String conceptName) {
        return new FKFilterDialog(shell, Messages.SetForeignKeyFilter, filter, page, conceptName);
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }
}
