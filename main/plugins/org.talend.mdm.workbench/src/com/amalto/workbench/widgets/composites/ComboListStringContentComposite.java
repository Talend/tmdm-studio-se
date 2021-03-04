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
package com.amalto.workbench.widgets.composites;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.providers.StringViewerSorter;
import com.amalto.workbench.providers.ListContentProvider;
import com.amalto.workbench.providers.ListStringLabelProvider;

public abstract class ComboListStringContentComposite extends ListStringContentsComposite {

    protected ComboViewer comboInfos;

    public ComboViewer getInfosComboViewer() {
        return this.comboInfos;
    }

    public ComboListStringContentComposite(Composite parent, int style, Object[] initParas,BasePropertySection section) {
        super(parent, style, initParas,section);
    }

    @Override
    protected void createCandidateInfoUIArea(Composite parent) {

        comboInfos = new ComboViewer(this, SWT.DROP_DOWN);
        comboInfos.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboInfos.setContentProvider(new ListContentProvider());
        comboInfos.setLabelProvider(new ListStringLabelProvider());
        comboInfos.setSorter(new StringViewerSorter());

    }

    @Override
    protected void initUIListeners() {
        initListenerForInfosCombo();
        super.initUIListeners();
    }

    protected void initListenerForInfosCombo() {

        comboInfos.getCombo().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {

                comboInfos.setInput(getAllInfosInCombo());
            }
        });
    }

    @Override
    protected boolean hasCandidateInfo() {
        return getSelectionFromInfoViewer(comboInfos) != null;
    }

    @Override
    protected String getCandidateInfo() {
        return getSelectionFromInfoViewer(comboInfos);
    }

    @Override
    protected void initCandidateInfoUIArea() {
        comboInfos.setSelection(null);
    }


    protected abstract String[] getAllInfosInCombo();
}
