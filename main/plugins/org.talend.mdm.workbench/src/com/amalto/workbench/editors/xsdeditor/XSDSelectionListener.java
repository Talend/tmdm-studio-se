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
package com.amalto.workbench.editors.xsdeditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTerm;

import com.amalto.workbench.editors.DataModelMainPage;

public class XSDSelectionListener implements ISelectionChangedListener {

    XSDEditor editor;

    private final DataModelMainPage dataModelMainPage;


    public XSDSelectionListener(XSDEditor editor, DataModelMainPage dataModelMainPage) {
        this.editor = editor;
        this.dataModelMainPage = dataModelMainPage;
    }

    public void selectionChanged(SelectionChangedEvent event) {
        // do not fire selection in source editor if the current active page is the InternalXSDMultiPageEditor (source)
        // We only want to make source selections if the active page is either the outline or properties (a modify
        // has been done via the outline or properties and not the source view). We don't want to be selecting
        // and unselecting things in the source when editing in the source!!
        boolean makeSelection = true;
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (page.getActivePart() instanceof XSDEditor) {
                if (editor.getActivePage() == editor.SOURCE_PAGE_INDEX) {
                    makeSelection = false;
                }
            }
        }

        // do not fire selection in source editor if selection event came
        // from source editor
        if (event.getSource() != editor.getTextEditor().getSelectionProvider() && makeSelection) {
            ISelection selection = event.getSelection();
            if (selection instanceof StructuredSelection) {

                Object selectedElement = ((StructuredSelection) selection).getFirstElement();
                XSDElementDeclaration element = getElement(selectedElement);
                if (element == null)
                    return;

                editor.getSelectionManager().setSelection(new StructuredSelection(selectedElement));
            }
        }
    }

    /**
     * get the selection element in the datamodelmainpage's TreeViewer
     *
     * @param object
     * @return
     */
    private XSDElementDeclaration getElement(Object object) {
        XSDElementDeclaration el;
        if (object instanceof XSDElementDeclaration) {
            el = (XSDElementDeclaration) object;
            return el;
        }
        if (object instanceof XSDParticle) {
            XSDTerm term = ((XSDParticle) object).getTerm();
            if (term instanceof XSDElementDeclaration) {
                return (XSDElementDeclaration) term;
            }
        }
        if (object instanceof XSDModelGroup) {
            XSDModelGroup group = (XSDModelGroup) object;
            if (group.getContainer() instanceof XSDParticle) {
                if (((XSDParticle) group.getContainer()).getTerm() instanceof XSDElementDeclaration)
                    return (XSDElementDeclaration) ((XSDParticle) group.getContainer()).getTerm();
            }
        }
        return null;
    }

}
