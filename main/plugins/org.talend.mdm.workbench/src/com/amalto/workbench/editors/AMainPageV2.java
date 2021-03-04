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
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
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

import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.TableViewWrapper;
import com.amalto.workbench.widgets.TisTableViewer;
import com.amalto.workbench.widgets.WidgetFactory;

public abstract class AMainPageV2 extends AFormPage implements ModifyListener, Observer {

    private static Log log = LogFactory.getLog(AMainPageV2.class);

    protected boolean comitting;

    protected boolean refreshing;

    protected boolean isCompositeView = true;

    private TMDMService port;

    public void setCompositeView(boolean isCompositeView) {
        this.isCompositeView = isCompositeView;
    }

    protected ComplexTableViewerColumn[] conditionsColumns;

    protected TableViewWrapper wrap = new TableViewWrapper();;

    protected TisTableViewer conditionViewer;

    public boolean isComitting() {
        return comitting;
    }

    public void setComitting(boolean comitting) {
        this.comitting = comitting;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    private TopFormPart topFormPart = null;

    public AMainPageV2(FormEditor editor, String id, String title) {
        super(editor, id, title);

    }

    public void update(Observable o, Object arg) {
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        try {
            /*
             * FormToolkit toolkit = managedForm.getToolkit();
             *
             * final ScrolledForm form = managedForm.getForm(); TableWrapLayout formLayout = new TableWrapLayout();
             * form.getBody().setLayout(formLayout);
             *
             * formLayout.numColumns = 1;
             *
             * //create the FormPart firstSectionPart = new SectionPart( form.getBody(), toolkit,
             * Section.DESCRIPTION|ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED ) ;
             * managedForm.addPart(firstSectionPart);
             *
             * //Layout the components Section firstSection = firstSectionPart.getSection();
             * firstSection.setText("Characteristics"); firstSection.setLayoutData( new
             * TableWrapData(TableWrapData.FILL_GRAB) );
             *
             * firstSection.addExpansionListener(new ExpansionAdapter() { public void
             * expansionStateChanged(ExpansionEvent e) { form.reflow(true); } });
             * firstSection.setDescription("The main characteristics"); firstSection.setLayout(new GridLayout(1,false));
             *
             * toolkit.createCompositeSeparator(firstSection);
             *
             *
             * Composite charComposite = toolkit.createComposite(firstSection); charComposite.setLayoutData( new
             * GridData(SWT.FILL,SWT.FILL,true,true,1,1) ); GridLayout charLayout = new GridLayout(2,false);
             * charComposite.setLayout(charLayout);
             *
             * firstSection.setClient(charComposite);
             */
            ScrolledForm form = managedForm.getForm();
            TableWrapLayout formLayout = new TableWrapLayout();
            form.getBody().setLayout(formLayout);

            // sets the title
            form.setText(this.getTitle());

            // get the toolkit
            FormToolkit toolkit = WidgetFactory.getWidgetFactory();// managedForm.getToolkit();

            // This part is meant to track dirty states
            topFormPart = new TopFormPart();
            getManagedForm().addPart(topFormPart);
            // initCoditionsColumns();
            createCharacteristicsContent(toolkit, topFormPart.getComposite());
            // adapt body add mouse/focus listener for child
            // WidgetFactory factory=WidgetFactory.getWidgetFactory();
            toolkit.adapt(form.getBody());
            initReadOnly(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createFormContent

    protected void initCoditionsColumns() {
        ComplexTableViewerColumn operatorColumn;
        if (isCompositeView) {
            operatorColumn = new ComplexTableViewerColumn("Operator", false, "", "", "", ComplexTableViewerColumn.COMBO_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                    IConstants.COMPOSITE_VIEW_CONDITION_OPERATORS, 0);
        } else {
            operatorColumn = new ComplexTableViewerColumn("Operator", false, "", "", "", ComplexTableViewerColumn.COMBO_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                    IConstants.VIEW_CONDITION_OPERATORS, 0);
        }
        conditionsColumns = new ComplexTableViewerColumn[] {
                new ComplexTableViewerColumn("XPath", false, "newXPath", "newXPath", "", ComplexTableViewerColumn.XPATH_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                        new String[] {}, 0),
                operatorColumn,
                new ComplexTableViewerColumn("Value", false, "", "", "", ComplexTableViewerColumn.XPATH_STYLE, new String[] {}, 0),//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                new ComplexTableViewerColumn("Predicate", true, "", "", "", ComplexTableViewerColumn.COMBO_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                        IConstants.PREDICATES, 0), };
    }

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
                AMainPageV2.this.getManagedForm().getForm().reflow(true);
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
        markDirtyWithoutCommit();
    }

    /**
     * Marks the page as dirty
     */
    public void markDirty() {
        topFormPart.markDirty();
        // TODO:check this method if there is any other problem.
        commitChanges();
    }

    public void markDirtyWithoutCommit() {
        topFormPart.markDirty();
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

    /****************************************************************************
     * DND
     ****************************************************************************/

    protected class DCDragSourceListener implements DragSourceListener {

        private int selected;

        public void dragFinished(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
                ((List) control).remove(selected);
                markDirty();
            }
        }

        public void dragSetData(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List)) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    this.selected = ((List) control).getSelectionIndex();
                    event.data = ((List) control).getSelection()[0];
                }
            }
        }

        public void dragStart(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List)) {
                event.doit = (((List) control).getItemCount() > 0);
            }
        }
    }

    public class DCDropTargetListener implements DropTargetListener {

        public void dragEnter(DropTargetEvent event) {
            // priority to copy
            if ((event.operations & DND.DROP_COPY) == DND.DROP_COPY) {
                event.detail = DND.DROP_COPY;
            } else if ((event.operations & DND.DROP_MOVE) == DND.DROP_MOVE) {
                event.detail = DND.DROP_MOVE;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }

        public void dragLeave(DropTargetEvent event) {
        }

        public void dragOperationChanged(DropTargetEvent event) {
        }

        public void dragOver(DropTargetEvent event) {
        }

        public void drop(DropTargetEvent event) {
            Control control = ((DropTarget) event.widget).getControl();
            if ((control instanceof List) && ((event.operations & DND.DROP_COPY) == DND.DROP_COPY)) {
                if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    if (!Arrays.asList(((List) control).getItems()).contains(event.data)) {
                        ((List) control).add((String) event.data);
                        markDirty();
                    }
                }
            }
        }

        public void dropAccept(DropTargetEvent event) {
        }

    }

    protected TMDMService getService() {
        if (port == null) {
            try {
                port = Util.getMDMService(getXObject());
            } catch (XtentisException e) {
                log.error(e.getMessage(), e);
            }
        }
        return port;
    }

    protected java.util.List<String> getRegex() {
        java.util.List<String> regex = new ArrayList<String>();
        regex.add(".*"); //$NON-NLS-1$
        return regex;
    }
}
