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

import com.amalto.workbench.dialogs.SimpleXpathInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.utils.XSDUtil;

public class XSDSetAnnotationForeignKeyAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDSetAnnotationForeignKeyAction.class);

    protected SimpleXpathInputDialog sxid = null;

    protected String dataModelName;

    public XSDSetAnnotationForeignKeyAction(DataModelMainPage page, String dataModelName) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.FK_OBJ.getPath()));
        setText(Messages.SetForeignKey);
        setToolTipText(Messages.SetForeignKey);
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
            XSDAnnotationsStructure struc = null;
            if (xSDCom != null) {
                struc = new XSDAnnotationsStructure(xSDCom);
            }
            if (struc == null || struc.getAnnotation() == null) {
                throw new RuntimeException(Messages.bind(Messages.UnableEditType, xSDCom.getClass().getName()));
            }

            sxid = getNewSimpleXpathInputDlg(struc.getForeignKey());
            sxid.setLock(true);
            sxid.setPKXpaths(XSDUtil.getAllPKXpaths(schema));
            String fksep = struc.getForeignKeyNotSep();
            if (fksep != null) {
                sxid.setFkSep(Boolean.valueOf(fksep));
            }
            sxid.setBlockOnOpen(true);
            int ret = sxid.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            String fk = "".equals(sxid.getXpath()) ? null : sxid.getXpath().replaceAll("'|\"", "");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            // keep the foreignkey in memory to improve performance
            if (Util.getForeignKeys() != null && fk != null) {
                if (struc.getForeignKey() != null) {
                    Util.getForeignKeys().remove(Util.getConceptFromPath(struc.getForeignKey()));
                }
                Util.getForeignKeys().add(Util.getConceptFromPath(fk));
            }
            struc.setForeignKey(fk);
            Boolean sep = sxid.getSepFk();
            struc.setForeignKeyNotSep(sep);
            updateAnnotationStructure(struc);
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

    private void updateAnnotationStructure(XSDAnnotationsStructure struc) {
        String fk = struc.getForeignKey();
        if (fk == null) {
            struc.setForeignKeyNotSep(null);
            struc.setFKFilter(null);
            struc.setForeignKeyInfos(new ArrayList());
            struc.setRetrieveFKinfos(null);
            struc.setFKIntegrity(null);
            struc.setFKIntegrityOverride(null);
        }
    }

    protected SimpleXpathInputDialog getNewSimpleXpathInputDlg(String foreignKey) {
        return new SimpleXpathInputDialog(page, Messages.SetForeignKey, Messages.EnterXpathForeignKey, foreignKey,
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        sxid.close();
                    }
                }, dataModelName

        );
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

}
