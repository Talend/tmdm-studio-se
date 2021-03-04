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
package org.talend.mdm.repository.ui.starting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.mdm.repository.ui.starting.editor.MDMStartingEditor;
import org.talend.mdm.repository.ui.starting.editor.MDMStartingEditorInput;

public class ShowWelcomeEditor {

    private static final Log log = LogFactory.getLog(ShowWelcomeEditor.class);

    public static IEditorPart showWelcomeEditor() {
        if(!isWorkbenchCreated()) {
            return null;
        }

        IEditorPart welcomeEditor = null;

        try {
            org.talend.core.ui.branding.IBrandingService service = null;
            service = (IBrandingService) GlobalServiceRegister.getDefault().getService(IBrandingService.class);

            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            welcomeEditor = activePage.openEditor(new MDMStartingEditorInput(service), MDMStartingEditor.ID);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return welcomeEditor;
    }

    private static boolean isWorkbenchCreated() {
        boolean isHere = false;
        try {
            isHere = PlatformUI.getWorkbench() != null;
        } catch (Exception e) {
            isHere = false;
        }

        return isHere;
    }

}
