// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.rcp;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * wchen class global comment. Detailled comment
 */
public class CloseIntroAction extends Action {

    private static final String ACTION_ID = "org.talend.rcp.intro.CloseIntroAction";//$NON-NLS-1$

    /**
     * wchen CloseIntroAction constructor comment.
     */
    public CloseIntroAction() {
        setId(ACTION_ID);
        setActionDefinitionId("org.talend.rcp.command.closeIntro");//$NON-NLS-1$

    }

    @Override
    public void run() {
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
    }

}
