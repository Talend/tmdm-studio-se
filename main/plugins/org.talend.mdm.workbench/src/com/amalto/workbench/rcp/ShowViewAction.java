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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.ShowViewDialog;
import org.eclipse.ui.internal.misc.StatusUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.IViewDescriptor;

/**
 * Displays a window for view selection. <br/>
 * 
 * $Id: ShowViewAction.java 26629 2009-07-17 08:05:40Z qwei $
 * 
 */
public class ShowViewAction extends Action {

    private static final String ACTION_ID = "org.talend.rcp.intro.ShowViewAction"; //$NON-NLS-1$

    /**
     * Constructs a new ShowViewAction.
     */
    public ShowViewAction() {
        super("Show view"); //$NON-NLS-1$
        setId(ACTION_ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return;
        }

        final ShowViewDialog dialog = new ShowViewDialog(window, WorkbenchPlugin.getDefault().getViewRegistry()) {

            protected Control createDialogArea(Composite parent) {
                Control control = super.createDialogArea(parent);
                // TODO
                // 1) get tree
                // 2) get keyUp/KeyDown listener.
                // 3) remove listener.
                Control[] com = ((Composite) control).getChildren();
                for (int i = 0; i < com.length; i++) {
                    Control control2 = com[i];
                    if (control2 instanceof Label) {
                        ((Label) control2).setText("");//$NON-NLS-1$
                    }
                    if (control2 instanceof FilteredTree) {
                        Tree tree = ((FilteredTree) control2).getViewer().getTree();
                        Listener[] listenerDown = tree.getListeners(SWT.KeyDown);
                        Listener[] listeberUp = tree.getListeners(SWT.KeyUp);
                        for (int j = 0; j < listenerDown.length; j++) {
                            if (listenerDown[j] instanceof TypedListener) {
                                if (((TypedListener) listenerDown[j]).getEventListener() instanceof KeyListener) {
                                    KeyListener keyLis = (KeyListener) ((TypedListener) listenerDown[j]).getEventListener();
                                    tree.removeKeyListener(keyLis);
                                }
                            }

                        }
                        for (int k = 0; k < listeberUp.length; k++) {
                            if (listeberUp[k] instanceof TypedListener) {
                                if (((TypedListener) listeberUp[k]).getEventListener() instanceof KeyListener) {
                                    KeyListener keyLis = (KeyListener) ((TypedListener) listeberUp[k]).getEventListener();
                                    tree.removeKeyListener(keyLis);
                                }
                            }
                        }
                    }

                }
                return control;
            }

        };

        dialog.open();
        if (dialog.getReturnCode() == Window.CANCEL) {
            return;
        }

        final IViewDescriptor[] descriptors = dialog.getSelection();
        for (int i = 0; i < descriptors.length; ++i) {
            try {
                page.showView(descriptors[i].getId());
            } catch (PartInitException e) {
                StatusUtil.handleStatus(e.getStatus(), WorkbenchMessages.ShowView_errorTitle + ": " + e.getMessage(), //$NON-NLS-1$
                        StatusManager.SHOW);
            }
        }
    }
}
