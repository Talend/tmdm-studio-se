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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.w3c.dom.Element;

import com.amalto.workbench.dialogs.AnnotationLanguageLabelsDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

/**
 *
 * @author fliu this class is for set Multilingual facet error messages attached to facets in the schema please refer to
 * bugID: 0009157
 */
public class XSDSetFacetMessageAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDSetFacetMessageAction.class);

    protected AnnotationLanguageLabelsDialog dlg = null;

    protected String dataModelName;

    public XSDSetFacetMessageAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DOCUMENTATION.getPath()));
        setText(Messages.XSDSetFacetMessageAction_Text);
        setToolTipText(Messages.XSDSetFacetMessageAction_ActionTip);
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
            // IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
            // XSDAnnotationsStructure struc = new XSDAnnotationsStructure((XSDComponent)selection.getFirstElement());
            if (struc.getAnnotation() == null) {
                throw new RuntimeException(Messages.bind(Messages.XSDSetFacetMessageAction_ExceptionInfo, selection.getFirstElement().getClass().getName()));
            }

            dlg = new AnnotationLanguageLabelsDialog(struc.getFactMessage(), new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    dlg.close();
                }
            }, page.getSite().getShell(), Messages.XSDSetFacetMessageAction_DialogTip);

            dlg.setBlockOnOpen(true);
            int ret = dlg.open();
            if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }
            Map<String, String> facets = dlg.getDescriptionsMap();
            struc.setFactMessage(facets);

            if (struc.hasChanged()) {
                page.refresh();
                page.getTreeViewer().expandToLevel(selection.getFirstElement(), 2);
                page.markDirty();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDSetFacetMessageAction_ErrorMsg + e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }
}
