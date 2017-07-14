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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.xsd.XSDElementDeclaration;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.composites.CommitBarComposite.CommitBarListener;
import com.amalto.workbench.detailtabs.sections.composites.EntityKeyConfigComposite;
import com.amalto.workbench.detailtabs.sections.composites.NameConfigComposite;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.entity.EntityWrapper;
import com.amalto.workbench.detailtabs.sections.util.CommitBarListenerRegistry;
import com.amalto.workbench.i18n.Messages;

public class EntityMainSection extends CommitBarListenerSection<XSDElementDeclaration> {

    public static final int SPACE_BEGINING = 10;

    public static final int HSPACE_CONTROL = 5;

    public static final int VSPACE_CONTROL = 10;

    private EntityWrapper entityWrapper;

    private NameConfigComposite compNameConfig;

    private EntityKeyConfigComposite compKeyConfig;

    private XSDElementDeclaration xsdElementDeclaration;

    // private CommitBarComposite compCommitBar;

    @Override
    public boolean shouldUseExtraSpace() {
        return true;
    }

    @Override
    public void refresh() {

        // compKeyConfig.setXSDElement(null);
        // compNameConfig.setTarget(null);
        //
        // if (entityWrapper != null) {
        // compNameConfig.setTarget(entityWrapper);
        // compKeyConfig.setXSDElement(entityWrapper);
        // }
        updateSectionEnabled();
    }

    @Override
    protected boolean doSubmit() throws CommitException, CommitValidationException {
        boolean result = super.doSubmit();

        initUIContents(entityWrapper.getSourceEntity());
        CommitSection commitsec = CommitBarListenerRegistry.getInstance().getRegistCommitSection(getParentTabID());
        if (commitsec != null) {
            List<CommitBarListener> commitBarListeners = commitsec.getCommitBar().getCommitBarListeners();
            if (commitBarListeners != null) {
                for (CommitBarListener listener : commitBarListeners) {
                    if (listener != this) {
                        listener.onReset();
                    }
                }
            }
        }
        return result;
    }

    @Override
    public XSDElementDeclaration getEditedObj() {
        return xsdElementDeclaration;
    }

    @Override
    protected void initUIContents(XSDElementDeclaration editedObj) {

        entityWrapper = new EntityWrapper(editedObj);
        this.xsdElementDeclaration = editedObj;

        compNameConfig.setTarget(entityWrapper);
        compKeyConfig.setXSDElement(entityWrapper);
    }

    @Override
    protected void refreshUIByEditedObj() {
        entityWrapper = new EntityWrapper(getEditedObj());

        this.xsdElementDeclaration = getEditedObj();

        compNameConfig.setTarget(entityWrapper);
        compKeyConfig.setXSDElement(entityWrapper);
        refresh();
    }

    @Override
    protected ISubmittable getSubmittedObj() {

        return entityWrapper;
    }

    // @Override
    // protected void registToGolbalCommitBarListenerReg() {
    // }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {

        Composite compTop = getWidgetFactory().createFlatFormComposite(compSectionClient);
        compTop.setLayout(new GridLayout());

        // compCommitBar = new CommitBarComposite(compTop, SWT.NONE);
        // GridData gdCompCommitBar = new GridData(SWT.BEGINNING, SWT.TOP,
        // false, false);
        // compCommitBar.setLayoutData(gdCompCommitBar);
        // compCommitBar.addCommitListener(this);

        compNameConfig = new NameConfigComposite(compTop, SWT.NONE, this);
        GridData gdCompName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        compNameConfig.setLayoutData(gdCompName);

        Group gpKeyConfig = new Group(compTop, SWT.NONE);
        gpKeyConfig.setText(Messages.EntityMainSection_KeyDefinition);
        gpKeyConfig.setBackground(compSectionClient.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        GridData gdGroupKeyConfig = new GridData(SWT.FILL, SWT.FILL, true, true);
        gpKeyConfig.setLayoutData(gdGroupKeyConfig);
        gpKeyConfig.setLayout(new FillLayout());
        compKeyConfig = new EntityKeyConfigComposite(gpKeyConfig, SWT.NONE, this, xsdElementDeclaration);
    }

    @Override
    protected String getSectionTitle() {
        return "Main";//$NON-NLS-1$
    }

    // @Override
    // protected int getSectionStyle() {
    // return Section.NO_TITLE | Section.EXPANDED;
    // }

    @Override
    protected boolean hasTitleSeperator() {
        return false;
    }
}
