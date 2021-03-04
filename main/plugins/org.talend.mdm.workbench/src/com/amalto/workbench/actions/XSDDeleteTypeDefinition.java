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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDDeleteTypeDefinition extends UndoAction {

    private XSDComplexTypeDefinition xsdCmpexType;

    private XSDSimpleTypeDefinition xsdSimpType;

    public XSDDeleteTypeDefinition(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        setText(Messages.XSDDeleteTypeDefinition_DelTypeDefine);
        setToolTipText(Messages.XSDDeleteTypeDefinition_RemoveTypeDefineFromSchema);
        setDescription(getToolTipText());
    }

    public XSDDeleteTypeDefinition(DataModelMainPage page, boolean isMulti) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        if (isMulti) {
            setText(Messages.XSDDeleteTypeDefinition_DelTypeDefines);
        } else {
            setText(Messages.XSDDeleteTypeDefinition_DelTypeDefine);
        }
        setToolTipText(Messages.XSDDeleteTypeDefinition_RemoveTypeDefineFromSchema);
        setDescription(getToolTipText());
    }

    @Override
    public void run(Object toDel) {
        if (!(toDel instanceof XSDComplexTypeDefinition || toDel instanceof XSDSimpleTypeDefinition)) {
            return;
        }
        if (toDel instanceof XSDComplexTypeDefinition) {
            xsdCmpexType = (XSDComplexTypeDefinition) toDel;
        } else {
            xsdSimpType = (XSDSimpleTypeDefinition) toDel;
        }
        run();
    }

    @Override
    public IStatus doAction() {
        IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
        ArrayList<Object> objList = new ArrayList<Object>();
        Util.getAllObject(page.getSite(), objList, (IStructuredContentProvider) page.getSchemaContentProvider());
        Util.getAllObject(page.getSite(), objList, (IStructuredContentProvider) page.getTypeContentProvider());

        XSDTypeDefinition usingElement = findUsingElement(selection, objList);
        if (usingElement != null) {
            String message = getInfoDialogMessage(usingElement);
            MessageDialog.openInformation(page.getSite().getShell(), Messages.XSDDeleteTypeDefinition_ConfirmDel, message);
            return Status.CANCEL_STATUS;
        }
        // edit by ymli; fix the bug:0012228. Made the multiple types can be deleted.
        for (Iterator<XSDTypeDefinition> iter = selection.iterator(); iter.hasNext();) {
            XSDTypeDefinition type = iter.next();
            if (type instanceof XSDSimpleTypeDefinition) {
                XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) type;
                if (xsdSimpType != null) {
                    simpleType = xsdSimpType;
                }
                schema.getContents().remove(simpleType);
            } else {
                XSDComplexTypeDefinition complxType = (XSDComplexTypeDefinition) type;
                if (xsdCmpexType != null) {
                    complxType = xsdCmpexType;
                }
                schema.getContents().remove(complxType);
            }
        }
        xsdSimpType = null;
        xsdCmpexType = null;
        page.refresh();
        page.markDirty();
        return Status.OK_STATUS;
    }

    private String getInfoDialogMessage(XSDTypeDefinition type) {
        if (type instanceof XSDSimpleTypeDefinition) {
            xsdSimpType = null;
            return Messages.bind(Messages.XSDDeleteTypeDefinition_ConfirmInfo, type.getName());
        }
        xsdCmpexType = null;
        return Messages.bind(Messages.XSDDeleteTypeDefinition_ConfirmInfo1, type.getName());
    }

    private XSDTypeDefinition findUsingElement(IStructuredSelection selection, ArrayList<Object> objList) {
        for (Iterator<XSDTypeDefinition> iter = selection.iterator(); iter.hasNext();) {
            XSDTypeDefinition type = iter.next();
            boolean find = Util.findElementsUsingType(objList, type);
            if (find) {
                return type;
            }
        }
        return null;
    }

    @Override
    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    public void setXSDTODel(XSDComplexTypeDefinition elem) {
        xsdCmpexType = elem;
    }

    public void setXSDTODel(XSDSimpleTypeDefinition xsdSimpType) {
        this.xsdSimpType = xsdSimpType;
    }

    public boolean isTypeDefinition(Object[] selectedObjs) {
        boolean typeDefinition = true;
        boolean isAllComplexType = false;
        for (Object obj : selectedObjs) {
            if (!(obj instanceof XSDSimpleTypeDefinition) && !(obj instanceof XSDComplexTypeDefinition)) {
                typeDefinition = false;
                break;
            } else if (!(obj instanceof XSDComplexTypeDefinition)) {
                isAllComplexType = true;
            }

        }
        if (selectedObjs.length == 1) {
            isAllComplexType = true;
        }
        return typeDefinition && isAllComplexType;
    }
}
