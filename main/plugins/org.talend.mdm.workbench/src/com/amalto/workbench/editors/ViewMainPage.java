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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.detailtabs.sections.IMDMRepositoryViewServiceExt;
import com.amalto.workbench.detailtabs.sections.util.MDMRepositoryViewExtensionService;
import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSBoolean;
import com.amalto.workbench.webservices.WSConceptKey;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetBusinessConceptKey;
import com.amalto.workbench.webservices.WSGetTransformerPKs;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSTransformerPK;
import com.amalto.workbench.webservices.WSTransformerPKArray;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.DescAnnotationComposite;
import com.amalto.workbench.widgets.TisTableViewer;

public class ViewMainPage extends AMainPageV2 implements ITextListener {

    private static Log log = LogFactory.getLog(ViewMainPage.class);

    protected DescAnnotationComposite desAntionComposite;

    protected DropTarget windowTarget;

    private boolean refreshing = false;

    private boolean comitting = false;

    private String lastDataModelName = null;

    private String viewName = null;

    protected String concept;

    private ComplexTableViewerColumn[] viewableElementColumns = new ComplexTableViewerColumn[] { new ComplexTableViewerColumn(
            "XPath", false, "newXPath", "newXPath", "", ComplexTableViewerColumn.XPATH_STYLE, new String[] {}, 0) };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

    private TisTableViewer viewableViewer;

    private ComplexTableViewerColumn[] searchableElementColumns = new ComplexTableViewerColumn[] { new ComplexTableViewerColumn(
            "XPath", false, "newXPath", "newXPath", "", ComplexTableViewerColumn.XPATH_STYLE, new String[] {}, 0) };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

    private TisTableViewer searchableViewer;

    private Button btnRunProcess;

    protected Combo cboProcessList;

    private Combo combox_policy;

    private Combo combox_sortdirection;

    private String lastSortField;

    private final String[] SORT_FIELD = { Messages.ViewMainPage_defaultField, Messages.ViewMainPage_noneField };

    private final String[] SORTING_DIRECTION = { Messages.ViewMainPage_ascOrder, Messages.ViewMainPage_descOrder };

    private IViewMainPageExAdapter adapter;
    /*
     * private ComplexTableViewerColumn[] conditionsColumns= new ComplexTableViewerColumn[]{ new
     * ComplexTableViewerColumn("XPath", false, "newXPath", "newXPath", "",ComplexTableViewerColumn.XPATH_STYLE,new
     * String[] {},0), new ComplexTableViewerColumn("Operator", false, "", "",
     * "",ComplexTableViewerColumn.COMBO_STYLE,IConstants.VIEW_CONDITION_OPERATORS,0), new
     * ComplexTableViewerColumn("Value", false, "", ""), new ComplexTableViewerColumn("Predicate", true, "", "",
     * "",ComplexTableViewerColumn.COMBO_STYLE,IConstants.PREDICATES,0), };
     */
    // private TisTableViewer conditionViewer;

    public ViewMainPage(FormEditor editor) {
        super(editor, ViewMainPage.class.getName(), Messages.ViewMainPage_View
                + ((XObjectEditorInput) editor.getEditorInput()).getName()
                + Util.getRevision((TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel()));
        // this.treeParent = this.getXObject().getParent();
        Object model = ((XObjectEditorInput) editor.getEditorInput()).getModel();
        this.viewName = ((TreeObject) model).getName();
        adapter = ExAdapterManager.getAdapter(this, IViewMainPageExAdapter.class);

    }

    protected void initProcessCombo() throws XtentisException {
        java.util.List<String> pList = new ArrayList<String>();
        WSTransformerPKArray array = Util.getMDMService(getXObject()).getTransformerPKs(new WSGetTransformerPKs("")); //$NON-NLS-1$
        if (array != null && array.getWsTransformerPK() != null) {
            for (WSTransformerPK pk : array.getWsTransformerPK()) {
                pList.add(pk.getPk());
            }
        }
        cboProcessList.setItems(pList.toArray(new String[pList.size()]));
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {
            Composite descriptionComposite = toolkit.createComposite(charComposite, SWT.NONE);
            descriptionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            descriptionComposite.setLayout(new GridLayout());

            desAntionComposite = new DescAnnotationComposite(Messages.ViewMainPage_Description,
                    " ...", toolkit, descriptionComposite, this, //$NON-NLS-1$
                    false);

            Composite comp = toolkit.createComposite(descriptionComposite);
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = 0;
            layout.marginLeft = 0;
            layout.marginTop = 0;
            layout.marginHeight = 0;
            layout.marginBottom = 0;
            comp.setLayout(layout);
            comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            btnRunProcess = toolkit.createButton(comp, Messages.ViewMainPage_RunResultThroughProcess, SWT.CHECK);
            btnRunProcess.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
            cboProcessList = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            cboProcessList.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
            initProcessCombo();

            createSortPart(toolkit, comp);

            if (adapter != null) {
                adapter.createComposite(this, toolkit, comp);
            }

            // add listener
            btnRunProcess.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    cboProcessList.setEnabled(btnRunProcess.getSelection());
                    markDirtyWithoutCommit();
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                    cboProcessList.setEnabled(btnRunProcess.getSelection());
                    markDirtyWithoutCommit();
                }
            });
            cboProcessList.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    markDirtyWithoutCommit();
                }
            });
            // make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
            windowTarget.addDropListener(new DCDropTargetListener());

            /****
             * /viewable Business Elements
             ****/
            TreeParent treeParent = (TreeParent) getAdapter(TreeParent.class);
            Composite viewablehGroup = this.getNewSectionComposite(Messages.ViewMainPage_ViewableBusinessElements);
            viewablehGroup.setLayout(new GridLayout(2, false));
            viewableElementColumns[0].setColumnWidth(220);
            viewableViewer = getNewTisTableViewer(viewablehGroup, toolkit, Arrays.asList(viewableElementColumns));
            viewableViewer.setTreeParent(treeParent);
            viewableViewer.setXpath(true);
            if (viewName.startsWith(Messages.ViewMainPage_BrowseItems)) {
                concept = viewName.replaceAll("Browse_items_", "").replaceAll("#.*", "");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            }
            viewableViewer.setConceptName(concept);
            viewableViewer.setMainPage(this);
            viewableViewer.setAddMulti(true);
            viewableViewer.create();
            viewableViewer.setHeight(110);
            // wrap.Wrap(this, viewableViewer);

            Composite searchGroup = this.getNewSectionComposite(Messages.ViewMainPage_SearchableBusinessElements);
            searchGroup.setLayout(new GridLayout(2, false));
            searchableElementColumns[0].setColumnWidth(220);
            searchableViewer = getNewTisTableViewer(searchGroup, toolkit, Arrays.asList(searchableElementColumns));
            searchableViewer.setTreeParent(treeParent);
            searchableViewer.setXpath(true);
            searchableViewer.setConceptName(concept);
            searchableViewer.setMainPage(this);
            searchableViewer.setAddMulti(true);
            searchableViewer.create();
            searchableViewer.setHeight(110);
            // wrap.Wrap(this, searchableViewer);

            // Where Conditions
            // if(viewName.startsWith("Browse_items_"))
            // setCompositeView(false);
            // else
            setCompositeView(true);
            initCoditionsColumns();
            Composite wcGroup = this.getNewSectionComposite(Messages.ViewMainPage_WhereConditions);
            wcGroup.setLayout(new GridLayout(2, false));
            conditionsColumns[0].setColumnWidth(250);
            conditionsColumns[1].setColumnWidth(150);
            conditionsColumns[2].setColumnWidth(250);
            conditionsColumns[3].setColumnWidth(120);
            conditionViewer = getNewTisTableViewer(wcGroup, toolkit, Arrays.asList(conditionsColumns));
            conditionViewer.setTreeParent(treeParent);
            conditionViewer.setXpath(true);
            conditionViewer.setConceptName(concept);
            conditionViewer.setMainPage(this);
            conditionViewer.setAddMulti(true);
            conditionViewer.create();
            conditionViewer.setHeight(110);

            // wrap.Wrap(this, conditionViewer);
            addToolBarItem();

            refreshData();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createCharacteristicsContent

    private final String selectXPath = Messages.ViewMainPage_selectXPath;
    protected void createSortPart(FormToolkit toolkit, Composite parent) {
        Composite comp = toolkit.createComposite(parent);
        GridLayout slayout = new GridLayout(3, false);
        slayout.marginWidth = 0;
        slayout.marginLeft = 0;
        slayout.marginTop = 0;
        slayout.marginHeight = 0;
        slayout.marginBottom = 0;
        comp.setLayout(slayout);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));

        Label label_sortpolicy = toolkit.createLabel(comp, "Order by ", SWT.NONE); //$NON-NLS-1$
        label_sortpolicy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        combox_policy = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER | SWT.SINGLE);
        GridData policyLayout = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
        policyLayout.widthHint = 120;
        combox_policy.setLayoutData(policyLayout);

        combox_sortdirection = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER | SWT.SINGLE);
        GridData sortDirectionLayout = new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1);
        combox_sortdirection.setLayoutData(sortDirectionLayout);

        addListener();
    }

    private void addListener() {
        combox_policy.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                boolean toSelectXPath = combox_policy.getText().equals(selectXPath);
                if (toSelectXPath) {
                    String modelName = getDatamodelName();
                    String entityName = concept;
                    XpathSelectDialog dlg = getXPathSelectionDialog(Messages.ViewMainPage_titleSelectField, modelName,
                            new SortFieldSelectionFilter());
                    dlg.setConceptName(entityName);
                    String xpath = null;
                    if (dlg.open() == IDialogConstants.OK_ID) {
                        xpath = dlg.getXpath();
                    }

                    if (xpath != null) {
                        combox_policy.setItems(
                                new String[] { SORT_FIELD[0], SORT_FIELD[1], xpath, selectXPath });
                        combox_policy.setText(xpath);
                        combox_sortdirection.setVisible(true);
                        combox_sortdirection.select(0);
                        markDirtyWithoutCommit();
                    } else {
                        lastSortField = lastSortField == null ? SORT_FIELD[0] : lastSortField;
                        combox_policy.setText(lastSortField);
                    }
                } else if (combox_policy.getSelectionIndex() != 0 && combox_policy.getSelectionIndex() != 1) {
                    combox_sortdirection.setVisible(true);
                    combox_sortdirection.select(0);
                    lastSortField = combox_policy.getText();
                    markDirtyWithoutCommit();
                } else {
                    lastSortField = combox_policy.getText();
                    combox_sortdirection.setVisible(false);
                    markDirtyWithoutCommit();
                }

            }

        });
        combox_policy.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
                combox_policy.setToolTipText(combox_policy.getText());
            }

        });
        combox_sortdirection.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                markDirtyWithoutCommit();
            }
        });
    }

    protected XpathSelectDialog getXPathSelectionDialog(String title, String modelName, IXPathSelectionFilter filter) {
        XpathSelectDialog dlg = new XpathSelectDialog(getEditorSite().getShell(), null, title, getSite(), false, modelName,
                false, filter);
        return dlg;
    }

    private void addToolBarItem() {
        XObjectEditor editor = (XObjectEditor) getEditor();
        editor.getToolBar().addActions(new TestViewAction());
    }

    protected TisTableViewer getNewTisTableViewer(Composite parent, FormToolkit toolkit,
            java.util.List<ComplexTableViewerColumn> columns) {
        return new TisTableViewer(columns, toolkit, parent);
    }

    @Override
    protected void refreshData() {
        try {

            if (this.comitting) {
                return;
            }

            this.refreshing = true;

            WSView wsObject = getWsViewObject();

            desAntionComposite.setText(wsObject.getDescription() == null ? "" : wsObject.getDescription());//$NON-NLS-1$

            btnRunProcess.setSelection(wsObject.getIsTransformerActive().isTrue());
            if (btnRunProcess.getSelection()) {
                cboProcessList.setEnabled(true);
                cboProcessList.setText(wsObject.getTransformerPK());
            } else {
                cboProcessList.setEnabled(false);
                cboProcessList.setText("");//$NON-NLS-1$
            }

            ///////////////////////
            combox_sortdirection.add(SORTING_DIRECTION[0]);
            combox_sortdirection.add(SORTING_DIRECTION[1]);
            String sortField = wsObject.getSortField();
            WSBoolean ascOrder = wsObject.getIsAsc();
            if (sortField == null) {
                sortField = SORT_FIELD[0];
            }
            lastSortField = sortField;

            String[] policys = { SORT_FIELD[0], SORT_FIELD[1], selectXPath };
            if (sortField.equals(SORT_FIELD[0]) || sortField.equals(SORT_FIELD[1])) {
                combox_sortdirection.setVisible(false);
            } else {
                policys = new String[] { SORT_FIELD[0], SORT_FIELD[1], sortField, selectXPath };
                combox_sortdirection.setVisible(true);
                if (ascOrder == null || ascOrder.isTrue()) {
                    combox_sortdirection.setText(SORTING_DIRECTION[0]);
                } else {
                    combox_sortdirection.setText(SORTING_DIRECTION[1]);
                }
            }
            combox_policy.setItems(policys);
            combox_policy.setText(sortField);

            if (adapter != null) {
                adapter.refreshData(wsObject, getDatamodelName());
            }

            ////////////////////
            java.util.List<Line> vlines = new ArrayList<Line>();
            java.util.List<String> vis = wsObject.getViewableBusinessElements();
            if (vis != null) {
                for (String vi : vis) {
                    String strings[] = new String[] { vi };
                    Line line = new Line(viewableElementColumns, strings);
                    vlines.add(line);
                }
            }
            viewableViewer.getViewer().setInput(vlines);

            java.util.List<Line> slines = new ArrayList<Line>();
            java.util.List<String> ses = wsObject.getSearchableBusinessElements();
            if (ses != null) {
                for (String se : ses) {
                    String strings[] = new String[] { se };
                    Line line = new Line(searchableElementColumns, strings);
                    slines.add(line);
                }
            }
            searchableViewer.getViewer().setInput(slines);

            java.util.List<Line> lines = new ArrayList<Line>();
            for (WSWhereCondition wc : wsObject.getWhereConditions()) {
                Line line = new Line(conditionsColumns, Util.convertWhereCondition(wc));
                lines.add(line);
            }
            conditionViewer.getViewer().setInput(lines);
            this.refreshing = false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.ViewMainPage_ErrorRefreshPage,
                    Messages.bind(Messages.ViewMainPage_ErrorRefreshPageXX, e.getLocalizedMessage()));
        }
    }

    protected WSView getWsViewObject() {
        WSView wsObject = null;
        try {
            if (getXObject().getWsObject() == null) { // then fetch from server

                TMDMService port = Util.getMDMService(getXObject());

                wsObject = port.getView(new WSGetView((WSViewPK) getXObject().getWsKey()));
                getXObject().setWsObject(wsObject);
            } else { // it has been opened by an editor - use the object there
                wsObject = (WSView) getXObject().getWsObject();
            }
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return wsObject;
    }

    private String dataModelName;

    private String getDatamodelName() {
        if (dataModelName == null || dataModelName.isEmpty()) {
            IMDMRepositoryViewServiceExt repositoryViewService = MDMRepositoryViewExtensionService.getRepositoryViewService();
            java.util.List<String> dataModelNames = repositoryViewService.getDataModel(dataModelName, concept);
            dataModelName = dataModelNames.get(0);
        }

        return dataModelName;
    }

    @Override
    protected void commit() {
        try {

            if (this.refreshing) {
                return;
            }

            this.comitting = true;
            WSView wsObject = getWsViewObject();
            wsObject.setDescription(desAntionComposite.getText());
            wsObject.setIsTransformerActive(new WSBoolean(btnRunProcess.getSelection()));
            wsObject.setTransformerPK(cboProcessList.getText());

            String policy = combox_policy.getText();
            if (policy.equals(SORT_FIELD[0])) {
                policy = null;
            }
            wsObject.setSortField(policy);

            boolean visible = combox_sortdirection.isVisible();
            if (visible) {
                boolean isAscOrder = combox_sortdirection.getText().equals(SORTING_DIRECTION[0]);
                wsObject.setIsAsc(new WSBoolean(isAscOrder));
            } else {
                wsObject.setIsAsc(null);
            }

            if (adapter != null) {
                adapter.saveData(wsObject);
            }

            java.util.List<Line> vlines = (java.util.List<Line>) viewableViewer.getViewer().getInput();
            wsObject.getViewableBusinessElements().clear();
            for (Line item : vlines) {
                wsObject.getViewableBusinessElements().add(item.keyValues.get(0).value);
            }

            java.util.List<Line> slines = (java.util.List<Line>) searchableViewer.getViewer().getInput();
            wsObject.getSearchableBusinessElements().clear();
            for (Line item : slines) {
                wsObject.getSearchableBusinessElements().add(item.keyValues.get(0).value);
            }

            java.util.List<Line> lines = (java.util.List<Line>) conditionViewer.getViewer().getInput();
            java.util.List<WSWhereCondition> wclist = new ArrayList<WSWhereCondition>();
            wsObject.getWhereConditions().clear();
            for (Line item : lines) {
                String[] values = new String[] { item.keyValues.get(0).value, item.keyValues.get(1).value,
                        item.keyValues.get(2).value, item.keyValues.get(3).value };
                WSWhereCondition wc = Util.convertLine(values);
                wsObject.getWhereConditions().add(wc);
            }

            this.comitting = false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.ViewMainPage_ErrorCommitPage,
                    Messages.bind(Messages.ViewMainPage_ErrorCommitPageXX, e.getLocalizedMessage()));
        }
    }

    protected java.util.List<String> getAvailableDataModel() {
        return Util.getDataModel(this.getXObject(), null, concept);
    }

    protected WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey businessConcepKey) throws XtentisException {
        return Util.getMDMService(getXObject()).getBusinessConceptKey(businessConcepKey);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        if (this.viewName != null && this.viewName.length() > 0) {
            if (viewName.matches("Browse_items.*")) {//$NON-NLS-1$
                // lastDataModelName=XpathSelectDialog.getDataModelName();
                String concept = viewName.replaceAll("Browse_items_", "").replaceAll("#.*", "");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                java.util.List<String> avaiList = getAvailableDataModel();
                if (avaiList.size() > 0) {
                    lastDataModelName = avaiList.get(0);
                }
                if (concept != null && concept.length() > 0 && lastDataModelName != null && lastDataModelName.length() > 0) {
                    // if(concept!=null&&concept.length()>0&&lastDataModelName!=null&&lastDataModelName.length()>0){

                    // keys validate
                    java.util.List<String> toAddViewableList = new ArrayList<String>();

                    WSGetBusinessConceptKey wsGetBusinessConceptKey = new WSGetBusinessConceptKey(concept, new WSDataModelPK(
                            lastDataModelName));
                    WSConceptKey wsConceptKey = null;
                    try {
                        wsConceptKey = getBusinessConceptKey(wsGetBusinessConceptKey);
                    } catch (XtentisException e) {
                        log.error(e.getMessage(), e);
                    }

                    if (wsConceptKey != null) {

                        java.util.List<String> viewableList = new ArrayList<String>();
                        java.util.List<Line> vlines = (java.util.List<Line>) viewableViewer.getViewer().getInput();
                        for (int j = 0; j < vlines.size(); j++) {
                            Line item = vlines.get(j);
                            viewableList.add(item.keyValues.get(0).value);
                        }

                        java.util.List<String> keys = wsConceptKey.getFields();
                        for (int i = 0; i < keys.size(); i++) {
                            if (".".equals(wsConceptKey.getSelector())) {
                                keys.set(i, "/" + concept + "/" + keys.get(i));//$NON-NLS-1$//$NON-NLS-2$
                            } else {
                                keys.set(i, wsConceptKey.getSelector() + keys.get(i));
                            }
                        }

                        java.util.List<String> ids = wsConceptKey.getFields();
                        for (String id : ids) {

                            // need to care about more case
                            if (id.startsWith("/")) {//$NON-NLS-1$
                                id = id.substring(1);
                            } else if (id.startsWith("//")) {//$NON-NLS-1$
                                id = id.substring(2);
                            }

                            if (!viewableList.contains(id)) {
                                toAddViewableList.add(0, id);
                            }
                        }
                    }
                    // show verify report
                    if (toAddViewableList.size() > 0) {

                        String msg = Messages.ViewMainPage_Msg;
                        for (Object element : toAddViewableList) {
                            String toAddItem = (String) element;
                            msg += (toAddItem + "\n"); //$NON-NLS-1$
                        }
                        msg += Messages.ViewMainPage_Addtions;

                        MessageDialog.openInformation(this.getSite().getShell(), Messages.ViewMainPage_VerifyReport, msg);
                    }

                    // auto fix
                    IRunnableWithProgress autoFixProcess = new AutoFixProgress(toAddViewableList, viewableViewer, this.getSite()
                            .getShell());

                    try {
                        new ProgressMonitorDialog(this.getSite().getShell()).run(false, true, autoFixProcess);
                    } catch (InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }

                }
            }
        }

    }

    @Override
    protected void createActions() {
    }

    public void textChanged(TextEvent event) {
        markDirtyWithoutCommit();
    }

    @Override
    public void dispose() {
        super.dispose();
        windowTarget.dispose();
    }

    // description text listener
    @Override
    public void modifyText(ModifyEvent e) {
        if (this.refreshing) {
            return;
        }
        super.modifyText(e);
    }

    class TestViewAction extends Action {

        public TestViewAction() {
            setImageDescriptor(ImageCache.getImage(EImage.RUN_EXC.getPath()));
            setText(Messages.ViewMainPage_test);
            setToolTipText(Messages.ViewMainPage_test);
        }

        @Override
        public void run() {
            runTest();
        }
    }

    /****************************************************************************
     * DND
     ****************************************************************************/

    class DCDragSourceListener implements DragSourceListener {

        private int selected;

        public void dragFinished(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
                ((List) control).remove(selected);
                markDirtyWithoutCommit();
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

    class DCDropTargetListener implements DropTargetListener {

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
                        ViewMainPage.this.markDirtyWithoutCommit();
                    }
                }
            }
        }

        public void dropAccept(DropTargetEvent event) {
        }

    }

    /**
     * @author stakey
     *
     */
    class AutoFixProgress implements IRunnableWithProgress {

        java.util.List<String> toAddViewableList;

        TisTableViewer viewableBEsList;

        Shell parentShell;

        public AutoFixProgress(java.util.List<String> toAddViewableList, TisTableViewer viewableBEsList, Shell shell) {
            super();
            this.toAddViewableList = toAddViewableList;
            this.viewableBEsList = viewableBEsList;
            this.parentShell = shell;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                monitor.beginTask(Messages.ViewMainPage_Addingkeypath, toAddViewableList.size());

                for (String string : toAddViewableList) {
                    String[] keyPath = new String[] { string };
                    Line line = new Line(viewableElementColumns, keyPath);
                    java.util.List<Line> vlines = (java.util.List<Line>) viewableViewer.getViewer().getInput();
                    vlines.add(line);
                    commit();
                    monitor.worked(1);
                }// for
                viewableViewer.getViewer().refresh();
                monitor.done();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(parentShell, Messages.ViewMainPage_ErrorAutoFix,
                        Messages.bind(Messages.ViewMainPage_ErrorMsg, e.getLocalizedMessage()));
            }// try
        }

    }

    public boolean equals(WSWhereCondition wcObj, WSWhereCondition obj) {
        if (wcObj.getLeftPath().equals(obj.getLeftPath()) && wcObj.getOperator().value().equals(obj.getOperator().value())
                && wcObj.getRightValueOrPath().equals(obj.getRightValueOrPath())
                && wcObj.getStringPredicate().value().equals(obj.getStringPredicate().value())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean beforeDoSave() {
        if (desAntionComposite.getText().trim().equals("")) {//$NON-NLS-1$en
            MessageDialog.openError(this.getSite().getShell(), Messages.ViewMainPage_ErrorSaving,
                    Messages.ViewMainPage_DescriptionCannotbeEmpty);
            return false;
        }
        java.util.List<Line> input = (java.util.List<Line>) viewableViewer.getViewer().getInput();
        if (input != null && input.size() == 0) {
            MessageDialog.openError(this.getSite().getShell(), Messages._Error, Messages.ViewMainPage_ErrorMsg1);
            return false;
        }

        input = (java.util.List<Line>) searchableViewer.getViewer().getInput();
        if (input != null && input.size() == 0) {
            MessageDialog.openError(this.getSite().getShell(), Messages._Error, Messages.ViewMainPage_ErrorMsg2);
            return false;
        }
        return true;
    }

    // Modified by hhb,to fix bug 21784
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == TreeParent.class) {
            return Util.getServerTreeParent(getXObject());
        }
        return super.getAdapter(adapter);
    }

    // The ending| bug:21784

    protected void runTest() {
        // empty
    }
}
