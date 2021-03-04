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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.detailtabs.sections.XpathSection;
import com.amalto.workbench.i18n.Messages;

/**
 * created by liusongbo on Aug 31, 2015
 */
public class ElementFKInfoComposite extends XpathComposite {

    private int caretPosition = 0;

    private String formatFKInfo = ""; //$NON-NLS-1$

    private ModifyListener modifyListener;

    private ElementFKInfoFormatViewer formatEditor;

    private StyledText styledText;

    public ElementFKInfoComposite(Composite parent) {
        super(parent);
    }

    public ElementFKInfoComposite(Composite parent, int style, XpathSection section) {
        super(parent, style, section);
        createFormatFkUIArea(this);
        addDoubleClickListener();
    }

    private void addDoubleClickListener() {
        tvInfos.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) tvInfos.getSelection();
                String label = selection.getFirstElement().toString();
                formatEditor.getTextWidget().insert(label);
            }
        });
    }

    protected void createFormatFkUIArea(Composite parent) {
        initializeDefaultPreferences();

        Group formatGroup = new Group(parent, SWT.NONE);
        GridData glayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        formatGroup.setLayoutData(glayoutData);
        GridLayout layout = new GridLayout();
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        formatGroup.setLayout(layout);
        formatGroup.setText(Messages.FormatFKInfoComp_format);

        IVerticalRuler verticalRuler = ElementFKInfoFormatHelper.createVerticalRuler();
        IOverviewRuler overviewRuler = ElementFKInfoFormatHelper.createOverviewRuler();

        formatEditor = new ElementFKInfoFormatViewer(formatGroup, verticalRuler, overviewRuler, true, SWT.V_SCROLL | SWT.H_SCROLL);
        formatEditor.configure(new ElementFKInfoConfiguration());
        formatEditor.initilize();

        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.heightHint = 150;
        formatEditor.getControl().setLayoutData(layoutData);
        formatEditor.setFkinfos(infos);
        formatEditor.setFormatFKInfo(formatFKInfo);
        styledText = formatEditor.getTextWidget();

        modifyListener = new ModifyListener() {


            public void modifyText(ModifyEvent e) {
                formatFKInfo = styledText.getText();
                caretPosition = styledText.getCaretOffset();

                if (section != null) {
                    section.autoCommit();
                }

            }
        };
    }

    private void addModifyListener() {
        styledText.addModifyListener(modifyListener);
    }

    private void removeModifyListener() {
        styledText.removeModifyListener(modifyListener);
    }

    public static final String PREF_COLOR_DEFAULT = "colorDefault"; //$NON-NLS-1$

    public static final String PREF_COLOR_STRING = "colorString"; //$NON-NLS-1$

    public static final String PREF_COLOR_KEYWORD = "colorKeyword"; //$NON-NLS-1$

    public void initializeDefaultPreferences() {
        IPreferenceStore store = MDMWorbenchPlugin.getDefault().getPreferenceStore();

        store.setDefault(PREF_COLOR_DEFAULT, StringConverter.asString(new RGB(0, 128, 0)));
        store.setDefault(PREF_COLOR_STRING, StringConverter.asString(new RGB(0, 0, 255)));
        store.setDefault(PREF_COLOR_KEYWORD, StringConverter.asString(new RGB(0, 0, 128)));
    }

    @Override
    public void setContentChanged(boolean changed) {
        super.setContentChanged(changed);
        fireXPathsChanges();
    }

    @Override
    public void setInfos(String[] infos) {
        super.setInfos(infos);
        fireXPathsChanges();
    }

    private void fireXPathsChanges() {
        formatEditor.setFkinfos(infos);
    }

    public String getFormatFKInfo() {
        formatFKInfo = formatEditor.getFormatFKInfo();
        return formatFKInfo;
    }

    public void setFormatFKInfo(String formatedFkInfo) {
        this.formatFKInfo = formatedFkInfo;

        removeModifyListener();

        styledText.setText(formatedFkInfo);
        styledText.setSelection(caretPosition);

        addModifyListener();
    }
}
