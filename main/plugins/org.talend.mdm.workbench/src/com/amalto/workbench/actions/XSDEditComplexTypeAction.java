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
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDComplexTypeDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDUtil;

public class XSDEditComplexTypeAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDEditComplexTypeAction.class);

    public XSDEditComplexTypeAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.EDIT_OBJ.getPath()));
        setText(Messages.EditAComplexType);
        setToolTipText(Messages.EditAComplexTypeTip);
        setDescription(getToolTipText());
    }

    @Override
    public IStatus doAction() {
        try {
            ISelection selection = page.getTreeViewer().getSelection();
            IStructuredContentProvider provider = (IStructuredContentProvider) page.getSchemaContentProvider();
            XSDComplexTypeDefinition decl = (XSDComplexTypeDefinition) ((IStructuredSelection) selection).getFirstElement();

            String oldName = decl.getName();
            InputDialog id = new InputDialog(page.getSite().getShell(), Messages.XSDEditComplexTypeAction_EditComplexType, Messages.XSDEditComplexTypeAction_EnterNameForEntity,
                    oldName, new IInputValidator() {

                        public String isValid(String newText) {
                            if ((newText == null) || "".equals(newText)) //$NON-NLS-1$
                                return Messages.XSDEditComplexTypeAction_ComplexTypeCannotBeEmpty;

                            if (Pattern.compile("^\\s+\\w+\\s*").matcher(newText).matches()//$NON-NLS-1$
                                    || newText.trim().replaceAll("\\s", "").length() != newText.trim().length())//$NON-NLS-1$//$NON-NLS-2$
                                return Messages.XSDEditComplexTypeAction_NameCannotContainEmpty;
                            if (!XSDUtil.isValidatedXSDName(newText)) {
                                return Messages.InvalidName_Message;
                            }
                            EList list = schema.getTypeDefinitions();
                            for (Iterator iter = list.iterator(); iter.hasNext();) {
                                Object d = iter.next();
                                if (d instanceof XSDComplexTypeDefinition) {
                                    XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) d;
                                    if (type.getName().equals(newText.trim()))
                                        return Messages.XSDEditComplexTypeAction_ComplexAlreadyExists;
                                }
                            }
                            return null;
                        };
                    });

            id.setBlockOnOpen(true);
            int ret = id.open();
            if (ret == Dialog.CANCEL) {
                return Status.CANCEL_STATUS;
            }
            decl.setName(id.getValue().trim());

            Util.updateReferenceToXSDTypeDefinition(page.getSite(), decl, provider);
            page.refresh();
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.XSDEditComplexTypeAction_ErrorEditEntity + e.getLocalizedMessage());
            return Status.CANCEL_STATUS;
        }

        return Status.OK_STATUS;
    }
}
