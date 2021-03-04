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

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTerm;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDUtil;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class XSDSkipToFKAction extends Action {

    /**
     *
     */
    private static final String FK_SEPERATOR = "/"; //$NON-NLS-1$

    /**
     *
     */
    private static final String X_FOREIGN_KEY = "X_ForeignKey"; //$NON-NLS-1$

    private final DataModelMainPage page;

    public XSDSkipToFKAction(DataModelMainPage page) {
        this.page = page;
        setText(Messages.XSDSkipToFKAction_actionTitle);
        setImageDescriptor(ImageCache.getImage(EImage.STEP_INTO.getPath()));
    }

    @Override
    public void run() {
        TreeViewer treeViewer = page.getTreeViewer();
        ISelection selection = treeViewer.getSelection();

        Object selObj = ((IStructuredSelection) selection).getFirstElement();
        if (selObj instanceof XSDParticle) {
            XSDTerm term = ((XSDParticle) selObj).getTerm();
            if (term instanceof XSDElementDeclaration) {
                XSDElementDeclaration element = (XSDElementDeclaration) term;

                String fkPath = getFKInfo(element);
                if (fkPath == null) {
                    MessageDialog
                            .openInformation(
                                    null,
                                    Messages.XSDSkipToFKAction_actionTitle, Messages.XSDSkipToFKAction_NotFoundFkInfo);
                    return;
                }
                String entityName = getEntityName(fkPath);

                EList<XSDElementDeclaration> elementDeclarations = page.getXSDSchema().getElementDeclarations();
                for (XSDElementDeclaration elementDeclaration : elementDeclarations) {
                    String name = elementDeclaration.getName();
                    if (entityName.equals(name)) {
                        StructuredSelection fkSelection = new StructuredSelection(elementDeclaration);
                        page.getElementsViewer().setSelection(fkSelection);
                        break;
                    }
                }

            }
        }

    }

    private String getEntityName(String fkDef) {
        if (fkDef == null)
            return null;
        int index = fkDef.indexOf(FK_SEPERATOR);
        if (index > 0) {
            return fkDef.substring(0, index);
        }
        return fkDef;
    }

    public String getFKInfo(XSDElementDeclaration element) {
        return XSDUtil.getAnnotationValue(element, X_FOREIGN_KEY);
    }
}
