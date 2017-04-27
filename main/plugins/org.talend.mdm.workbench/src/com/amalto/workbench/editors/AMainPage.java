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
package com.amalto.workbench.editors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.talend.mdm.webservice.TMDMService;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.widgets.WidgetFactory;

public abstract class AMainPage extends AFormPage implements ModifyListener {

    private static Log log = LogFactory.getLog(AMainPage.class);

    private SectionPart firstSectionPart = null;

    public AMainPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        try {

            FormToolkit toolkit = WidgetFactory.getWidgetFactory();// managedForm.getToolkit();

            final ScrolledForm form = managedForm.getForm();
            TableWrapLayout formLayout = new TableWrapLayout();
            form.getBody().setLayout(formLayout);

            formLayout.numColumns = 1;

            // create the FormPart
            firstSectionPart = new SectionPart(form.getBody(), toolkit, Section.DESCRIPTION | ExpandableComposite.TWISTIE
                    | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
            managedForm.addPart(firstSectionPart);

            // Layout the components
            Section firstSection = firstSectionPart.getSection();
            firstSection.setText(Messages.AMainPage_Characteristics);
            firstSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

            firstSection.addExpansionListener(new ExpansionAdapter() {

                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    form.reflow(true);
                }
            });
            firstSection.setDescription(Messages.AMainPage_MainCharacteristics);
            firstSection.setLayout(new GridLayout(1, false));

            // toolkit.createCompositeSeparator(firstSection);

            Composite charComposite = toolkit.createComposite(firstSection);
            charComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            GridLayout charLayout = new GridLayout(2, false);
            charComposite.setLayout(charLayout);

            firstSection.setClient(charComposite);

            createCharacteristicsContent(toolkit, charComposite);

            // adapt body add mouse/focus listener for child
            // WidgetFactory factory=WidgetFactory.getWidgetFactory();
            toolkit.adapt(form.getBody());
            initReadOnly(form);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createFormContent

    /**
     * The composite containing the actual characteristics form By default its layout is GridLayout of 2 columns
     * 
     * @param charSection
     */
    protected abstract void createCharacteristicsContent(FormToolkit toolkit, Composite charSection);

    protected Composite getNewSectionComposite(String title) {
        return getNewSectionComposite(title, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
                | ExpandableComposite.EXPANDED);
    }

    protected Composite getNewSectionComposite(String title, int style) {
        return (Composite) getNewSection(title, style).getClient();
    }

    protected Section getNewSection(String title) {
        return getNewSection(title, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
    }

    protected Section getNewSection(String title, int style) {

        // create the FormPart
        SectionPart newSectionPart = new SectionPart(this.getManagedForm().getForm().getBody(), this.getManagedForm()
                .getToolkit(), style);
        this.getManagedForm().addPart(newSectionPart);

        // Layout the components
        Section newSection = newSectionPart.getSection();
        if (title != null) {
            newSection.setText(title);
        }
        newSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        newSection.addExpansionListener(new ExpansionAdapter() {

            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                AMainPage.this.getManagedForm().getForm().reflow(true);
            }
        });
        newSection.setLayout(new GridLayout(1, false));

        // this.getManagedForm().getToolkit().createCompositeSeparator(newSection);
        newSection.setClient(getNewSectionComposite(newSection)); // in case someone calls getClient directly

        return newSection;
    }

    public Composite getNewSectionComposite(Section section) {

        Composite newComposite = this.getManagedForm().getToolkit().createComposite(section);
        newComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout charLayout = new GridLayout(2, false);
        newComposite.setLayout(charLayout);

        return newComposite;

    }

    /**
     * Modify Events on the page
     */
    public void modifyText(ModifyEvent e) {
        markDirty();
    }

    /**
     * Marks the page as dirty
     */
    public void markDirty() {
        firstSectionPart.markDirty();
        commitChanges();
    }

    public SectionPart getFirstSectionPart() {
        return firstSectionPart;
    }

    // This Part is meant to track dirty states changes
    class TopFormPart extends AbstractFormPart {

        public Composite getComposite() {
            ScrolledForm form = this.getManagedForm().getForm();
            Composite topComposite = this.getManagedForm().getToolkit().createComposite(form.getBody());
            topComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
            topComposite.setLayout(new GridLayout(2, false));
            return topComposite;
        }
    }

    protected TMDMService getMDMService() throws XtentisException {
        return Util.getMDMService(getXObject());
    }
}
