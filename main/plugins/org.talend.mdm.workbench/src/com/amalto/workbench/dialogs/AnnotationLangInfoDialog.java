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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.detailtabs.sections.composites.LanguageInfoComposite;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.langinfo.LanguageInfo;

public class AnnotationLangInfoDialog extends Dialog {

    private LanguageInfoComposite compLangInfo;

    private List<LanguageInfo> langInfos = new ArrayList<LanguageInfo>();

    public AnnotationLangInfoDialog(Shell parentShell, LanguageInfo[] initLangInfos) {
        super(parentShell);

        for (LanguageInfo eachLangeInfo : initLangInfos)
            langInfos.add(eachLangeInfo);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        compLangInfo = new LanguageInfoComposite(container, SWT.NONE,null);
        compLangInfo.setLanguageInfos(langInfos.toArray(new LanguageInfo[0]));

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(388, 272);
    }

    protected void buttonPressed(int buttonId) {

        if (buttonId == IDialogConstants.OK_ID) {

            langInfos.clear();
            for (LanguageInfo eachLangInfo : compLangInfo.getLanguageInfos())
                langInfos.add(eachLangInfo);
        }

        super.buttonPressed(buttonId);
    }

    public LanguageInfo[] getLangInfos() {
        return langInfos.toArray(new LanguageInfo[0]);
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.MAX | SWT.MIN | SWT.RESIZE;
    }
}
