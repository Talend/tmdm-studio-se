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
package com.amalto.workbench.detailtabs.sections;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.TabContents;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.talend.mdm.webservice.WSDataModel;

import com.amalto.workbench.detailtabs.sections.util.CommitBarListenerRegistry;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.editors.xsdeditor.XSDEditor;
import com.amalto.workbench.models.TreeObject;

public abstract class BasePropertySection extends AbstractPropertySection {

    protected TabbedPropertySheetPage tabbedPropertySheetPage;

    protected String parentTabID;

    protected Composite compSectionClient;

    protected Section section;

    @Override
    public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
        super.createControls(parent, aTabbedPropertySheetPage);

        tabbedPropertySheetPage = aTabbedPropertySheetPage;
        parentTabID = tabbedPropertySheetPage.getSelectedTab().getId();

        Composite compTop = getWidgetFactory().createComposite(parent);
        compTop.setLayout(new FillLayout());

        Section section = getWidgetFactory().createSection(compTop, getSectionStyle());
        section.setText(getSectionTitle());
        this.section = section;
        section.setLayout(new FillLayout());

        if (hasTitleSeperator()) {
            getWidgetFactory().createCompositeSeparator(section);
        }

        Composite compSectionClient = getWidgetFactory().createComposite(section);
        compSectionClient.setLayout(new FillLayout());

        createControlsInSection(compSectionClient);
        this.compSectionClient = compSectionClient;
        section.setClient(compSectionClient);
        // CommitSection commitsec=CommitBarListenerRegistry.getInstance().getRegistCommitSection(getParentTabID());
        // if(!(this instanceof CommitSection) && commitsec!=null){
        // commitsec.section.setVisible(false);
        // commitsec.section.setExpanded(false);
        // commitsec.section.layout(true);
        // }
    }

    public void commit() {
        CommitSection commitsec = CommitBarListenerRegistry.getInstance().getRegistCommitSection(getParentTabID());
        if (commitsec != null) {
            commitsec.getCommitBar().fireSubmit();
        }
    }

    public void autoCommit() {
        CommitSection commitsec = CommitBarListenerRegistry.getInstance().getRegistCommitSection(getParentTabID());
        if (!(this instanceof CommitSection) && commitsec != null) {
            commit();
            // commitsec.setTitle("Apply the changes to data-model");
            // commitsec.section.setVisible(true);
            // commitsec.section.setExpanded(true);
            // commitsec.section.layout(true);
            // commitsec.section.setForeground(section.getDisplay().getSystemColor(SWT.COLOR_RED));
        }
    }

    public void resetCommitSection() {
        // CommitSection commitsec=CommitBarListenerRegistry.getInstance().getRegistCommitSection(getParentTabID());
        // if((this instanceof CommitSection) && commitsec!=null){
        // commitsec.setTitle("Commit");
        // // commitsec.section.setVisible(false);
        // // commitsec.section.setExpanded(false);
        // commitsec.section.layout(true);
        // commitsec.section.setForeground(section.getTitleBarForeground());
        // }
    }

    @Override
    public void refresh() {
        super.refresh();
        resetCommitSection();
    }

    protected void updateSectionEnabled() {
        if (compSectionClient == null || compSectionClient.isDisposed()) {
            return;
        }
        compSectionClient.setEnabled(!isReadOnly());
    }

    protected boolean isReadOnly() {
        if (getPart() == null) {
            return false;
        }
        Object adapter = getPart().getAdapter(Boolean.class);
        if (adapter == null) {
            return false;
        }
        return ((Boolean) adapter).booleanValue();
    }

    public TabbedPropertySheetPage getTabbedPropertySheetPage() {
        return tabbedPropertySheetPage;
    }

    public TabContents getCurrentTabContents() {
        return tabbedPropertySheetPage.getCurrentTab();
    }

    public ISection[] getCurrentTabSections() {
        return getCurrentTabContents().getSections();
    }

    public ITabDescriptor getSelectedTabDescriptor() {
        return tabbedPropertySheetPage.getSelectedTab();
    }

    public String getSelectedTabID() {
        return getSelectedTabDescriptor().getId();
    }

    public String getParentTabID() {
        return parentTabID;
    }

    protected DataModelMainPage getCurDataModelMainPage() {
        return (DataModelMainPage) ((XSDEditor) getPart()).getAdapter(DataModelMainPage.class);
    }

    public TreeObject getTreeObject() {
        return getCurDataModelMainPage().getXObject();
    }

    protected String getDataModelName() {
        return getDataModel().getName();
    }

    protected WSDataModel getDataModel() {
        return getCurDataModelMainPage().getDataModel();
    }

    protected int getSectionStyle() {
        return Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED;
    }

    protected boolean hasTitleSeperator() {
        return false;
    }

    protected abstract String getSectionTitle();

    protected abstract void createControlsInSection(Composite compSectionClient);
}
