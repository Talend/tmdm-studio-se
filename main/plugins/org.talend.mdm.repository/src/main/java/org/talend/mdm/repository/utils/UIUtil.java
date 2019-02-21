// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.utils;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeActionProvider;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.ui.editors.IRepositoryViewEditorInput;

import com.amalto.workbench.views.MDMPerspective;

/**
 * created by HHB on 2013-3-6 Detailled comment
 * 
 */
public class UIUtil {

    public static boolean isWorkInUI() {

        try {
            return PlatformUI.isWorkbenchRunning();
        } catch (Exception e) {
            return false;
        }
    }

    public static IEditorPart findOpenedEditor(IEditorInput input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }

        IWorkbenchPage page = getCurrentPage();
        if (page != null) {
            return page.findEditor(input);
        }

        return null;
    }

    private static IWorkbenchPage getCurrentPage() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            return page;
        }
        return null;
    }

    // Note:only work for MDM objects
    public static IEditorPart findOpenedEditor(IRepositoryViewObject viewObj) {
        if (viewObj == null) {
            throw new IllegalArgumentException();
        }

        IRepositoryNodeConfiguration configuration = RepositoryNodeConfigurationManager.getConfiguration(viewObj);
        if (configuration != null) {
            IRepositoryNodeActionProvider actionProvider = configuration.getActionProvider();
            if (actionProvider != null) {
                IRepositoryViewEditorInput editorInput = actionProvider.getOpenEditorInput(viewObj);
                if (editorInput != null) {
                    return findOpenedEditor(editorInput);
                }
            }
        }
        return null;
    }

    public static void closeEditor(IEditorPart editorPart, boolean save) {
        IWorkbenchPage page = getCurrentPage();
        if (page != null) {
            page.closeEditor(editorPart, save);
        }
    }

    public static boolean isMDMPerspective(IWorkbenchPage page) {
        String perspectiveId = page.getPerspective().getId();
        return MDMPerspective.PERPECTIVE_ID.equals(perspectiveId);
    }
}
