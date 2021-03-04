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
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;

public class TdEditorToolBar {

    private CoolBar coolBar = null;

    private CoolBarManager coolBarMgr;

    private ToolBarManager defaultToolBarMgr;

    private List<Action> actions = new ArrayList<Action>();

    public TdEditorToolBar(Composite parent) {

        // create coolbar

        coolBar = new CoolBar(parent, SWT.FLAT);
        coolBarMgr = new CoolBarManager(coolBar);

        GridData gid = new GridData();
        gid.horizontalAlignment = GridData.FILL;
        coolBar.setLayoutData(gid);

        // initialize default actions
        defaultToolBarMgr = new ToolBarManager(SWT.FLAT | SWT.RIGHT);

        for (Action action : actions) {
            defaultToolBarMgr.add(action);
        }

        // add all toolbars to parent coolbar
        coolBarMgr.add(new ToolBarContributionItem(defaultToolBarMgr));
        coolBarMgr.update(true);
    }

    public void addResizeListener(ControlListener listener) {
        coolBar.addControlListener(listener);
    }

    public CoolBar getToolbarControl() {
        return coolBar;
    }

    public ToolBarManager getToolBarManager() {
        return defaultToolBarMgr;
    }

    public void addActions(Action... actions) {
        assert actions != null;

        if (coolBarMgr != null) {
            for (Action action : actions) {
                defaultToolBarMgr.add(action);
            }

            // coolBarMgr.add(new ToolBarContributionItem(defaultToolBarMgr));
            defaultToolBarMgr.update(true);
            coolBarMgr.update(true);
        }
    }

    public void addActions(IContributionItem... contributionItems) {
        assert contributionItems != null;

        if (coolBarMgr != null) {
            for (IContributionItem item : contributionItems) {
                defaultToolBarMgr.add(item);
            }

            defaultToolBarMgr.update(true);
            coolBarMgr.update(true);
        }
    }
}
