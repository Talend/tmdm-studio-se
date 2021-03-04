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
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.SimpleTypeInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDUtil;

public class XSDNewSimpleTypeDefinition extends UndoAction implements SelectionListener {

    private static Log log = LogFactory.getLog(XSDNewSimpleTypeDefinition.class);

    private SimpleTypeInputDialog dialog;

    String typeName = null;

    boolean builtIn = false;

    public XSDNewSimpleTypeDefinition(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
        setText(Messages.XSDNewSimpleTypeDefinition_CreateSimpleType);
        setToolTipText(Messages.XSDNewSimpleTypeDefinition_CreateSimpleTypeX);
        setDescription(getToolTipText());
    }

    @Override
    public IStatus doAction() {

        List<String> customTypes = new ArrayList<String>();
        List<String> builtInTypes = XSDUtil.getBuiltInTypes();

        dialog = new SimpleTypeInputDialog(this, page.getSite().getShell(), schema,
                Messages.XSDNewSimpleTypeDefinition_NewSimpleType, customTypes, builtInTypes, null);
        dialog.setBlockOnOpen(true);
        int ret = dialog.open();
        if (ret == Dialog.CANCEL) {
            return Status.CANCEL_STATUS;
        }

        XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
        XSDSimpleTypeDefinition typedef = factory.createXSDSimpleTypeDefinition();
        // set new one
        if (!builtIn) {
            // check if simple type definition already exists
            XSDSimpleTypeDefinition std = schema.resolveSimpleTypeDefinition(typeName);
            if (!schema.getTypeDefinitions().contains(std)) {
                std.setBaseTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string")); //$NON-NLS-1$
                schema.getContents().add(std);
            }
            typedef.setBaseTypeDefinition(std);
        }

        // remove current facets
        typedef.getFacetContents().removeAll(typedef.getFacetContents());
        typedef.updateElement();
        page.refresh();
        page.markDirty();
        return Status.OK_STATUS;
    }

    public void widgetSelected(SelectionEvent e) {
        if (dialog.getReturnCode() == -1) {
            return;
        }
        typeName = dialog.getTypeName();
        if (typeName.trim().length() == 0) {
            return;
        }
        builtIn = dialog.isBuiltIn();

        // if built in, check that the type actually exists
        if (builtIn) {
            boolean found = false;
            for (Object element : schema.getSchemaForSchema().getTypeDefinitions()) {
                XSDTypeDefinition type = (XSDTypeDefinition) element;
                if (type instanceof XSDSimpleTypeDefinition) {
                    if (type.getName().equals(typeName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                        Messages.bind(Messages.XSDNewSimpleTypeDefinition_ErrorMsg, typeName));
                return;
            }
        }
        dialog.close();
    }

    public void widgetDefaultSelected(SelectionEvent e) {

    }

    private boolean validateType() {
        if (!"".equals(typeName)) { //$NON-NLS-1$
            EList list = schema.getTypeDefinitions();
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                XSDTypeDefinition td = (XSDTypeDefinition) iter.next();
                if (td.getName().equals(typeName)) {
                    if (td instanceof XSDSimpleTypeDefinition) {
                        MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                                Messages.bind(Messages.XSDNewSimpleTypeDefinition_ErrorMsg1, typeName));
                        return false;
                    }
                }
            }// for
        } else {
            MessageDialog.openError(page.getSite().getShell(), Messages._Error, Messages.XSDNewSimpleTypeDefinition_ErrorMsg2);
            return false;
        }

        return true;
    }
}
