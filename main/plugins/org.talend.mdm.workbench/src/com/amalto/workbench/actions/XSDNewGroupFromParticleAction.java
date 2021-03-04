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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.NewGroupDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class XSDNewGroupFromParticleAction extends UndoAction implements SelectionListener {

    private static Log log = LogFactory.getLog(XSDNewGroupFromParticleAction.class);

    private NewGroupDialog dialog = null;

    private XSDParticle selParticle = null;

    private boolean isChoice;

    private boolean isAll;

    private int minOccurs;

    private int maxOccurs;

    public XSDNewGroupFromParticleAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
        setText(Messages.XSDNewGroupFromParticleAction_Text);
        setToolTipText(Messages.XSDNewGroupFromParticleAction_ActionTip);
    }

    public IStatus doAction() {
        try {
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            selParticle = (XSDParticle) selection.getFirstElement();

            if (!(selParticle.getContainer() instanceof XSDModelGroup))
                return Status.CANCEL_STATUS;

            XSDModelGroup group = (XSDModelGroup) selParticle.getContainer();
            // get position of the selected particle in the container
            int index = 0;
            int i = 0;
            for (Iterator iter = group.getContents().iterator(); iter.hasNext();) {
                XSDParticle p = (XSDParticle) iter.next();
                if (p.equals(selParticle)) {
                    index = i;
                    break;
                }
                i++;
            }

            dialog = new NewGroupDialog(this, page.getSite().getShell());
            dialog.setBlockOnOpen(true);
            int ret = dialog.open();
            if (ret == Dialog.CANCEL) {
                return Status.CANCEL_STATUS;
            }

            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

            // add an element declaration
            XSDElementDeclaration subElement = factory.createXSDElementDeclaration();
            subElement.setName("subelement");//$NON-NLS-1$
            subElement.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));//$NON-NLS-1$

            XSDParticle subParticle = factory.createXSDParticle();
            subParticle.setMinOccurs(1);
            subParticle.setMaxOccurs(1);
            subParticle.setContent(subElement);
            subParticle.updateElement();

            XSDModelGroup newGroup = factory.createXSDModelGroup();
            if (isChoice)
                newGroup.setCompositor(XSDCompositor.CHOICE_LITERAL);
            else if (isAll)
                newGroup.setCompositor(XSDCompositor.ALL_LITERAL);
            else
                newGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            newGroup.getContents().add(0, subParticle);
            newGroup.updateElement();

            XSDParticle particle = factory.createXSDParticle();
            particle.setContent(newGroup);
            particle.setMinOccurs(this.minOccurs);
            particle.setMaxOccurs(this.maxOccurs);

            group.getContents().add(index + 1, particle);
            group.updateElement();

            page.refresh();
            page.getTreeViewer().setSelection(new StructuredSelection(particle), true);

            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDNewGroupFromParticleAction_ErrorMsg, e.getLocalizedMessage()));

            return Status.CANCEL_STATUS;
        }

        return Status.CANCEL_STATUS;
    }

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    /********************************
     * Listener to input dialog
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public void widgetSelected(SelectionEvent e) {
        if (dialog.getReturnCode() == -1)
            return; // there was a validation error
        if (dialog.getReturnCode() == -1)
            return; // there was a validation error
        isChoice = dialog.isChoice();
        isAll = dialog.isAll();
        minOccurs = dialog.getMinOccurs();
        maxOccurs = dialog.getMaxOccurs();
        dialog.close();
    }

}
