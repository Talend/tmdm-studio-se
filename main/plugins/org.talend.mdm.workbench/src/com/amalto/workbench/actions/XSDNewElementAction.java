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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.NewConceptOrElementDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class XSDNewElementAction extends UndoAction implements SelectionListener {

    private static Log log = LogFactory.getLog(XSDNewElementAction.class);

    public XSDNewElementAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
        setText(Messages.XSDNewElementAction_Text);
        setToolTipText(Messages.XSDNewElementAction_ActionTip);
    }

    @Override
    public IStatus doAction() {
        try {
            List<String> customTypes = new ArrayList<String>();
            for (Iterator<XSDTypeDefinition> iter = schema.getTypeDefinitions().iterator(); iter.hasNext();) {
                XSDTypeDefinition type = iter.next();
                if (type instanceof XSDSimpleTypeDefinition)
                 {
                    customTypes
                            .add(type.getName() + (type.getTargetNamespace() != null ? " : " + type.getTargetNamespace() : ""));//$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            List<String> builtInTypes = new ArrayList<String>();
            for (Iterator<XSDTypeDefinition> iter = schema.getSchemaForSchema().getTypeDefinitions().iterator(); iter.hasNext();) {
                XSDTypeDefinition type = iter.next();
                if (type instanceof XSDSimpleTypeDefinition) {
                    builtInTypes.add(type.getName());
                }
            }

            NewConceptOrElementDialog id = new NewConceptOrElementDialog(this, page.getSite().getShell(), schema, Messages.XSDNewElementAction_Text,
                    customTypes, builtInTypes);

            id.setBlockOnOpen(true);
            id.open();
            if (id.getReturnCode() == Window.CANCEL) {
                return Status.CANCEL_STATUS;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDNewElementAction_ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    /**
     * author :fliu this fun is to support button click event invoked from the new expansion of new Element dialog
     */
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    /**
     * author: fliu this fun is to support button click event invoked from the new expansion of Element creation dialog
     */
    public void widgetSelected(SelectionEvent e) {
        NewConceptOrElementDialog dlg = (NewConceptOrElementDialog) ((Widget) e.getSource()).getData("dialog");//$NON-NLS-1$
        if (dlg.getReturnCode() == Window.OK) {
            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

            XSDElementDeclaration decl = factory.createXSDElementDeclaration();
            decl.setName(dlg.getTypeName());
            decl.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));//$NON-NLS-1$

            schema.getContents().add(decl);
            // schema.getElementDeclarations().add(decl);
            decl.updateElement();

            // page.markDirty();

            UndoAction changeAction = null;
            if (dlg.isComplexType()) {
                changeAction = new XSDChangeToComplexTypeAction(page, decl, dlg.getComplexType(), dlg.isChoice(), dlg.isAll(),
                        dlg.isAbstract(), dlg.getSuperTypeName());
            } else {
                changeAction = new XSDChangeToSimpleTypeAction(page, decl, dlg.getElementType(), dlg.isBuildIn());
            }
            dlg.close();

            changeAction.setOmitTrack(true);
            IStatus status = changeAction.execute();
            if(status == Status.CANCEL_STATUS) {
                schema.getContents().remove(decl);
            }

            page.refresh();
        }
    }
}
