// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;

import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter.FilterResult;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.providers.XSDTreeLabelProvider;
import com.amalto.workbench.providers.datamodel.SchemaNameFilter;
import com.amalto.workbench.providers.datamodel.XPathContentProvider;
import com.amalto.workbench.utils.SchemaElementNameFilterDes;

public class SelectXPathComposite extends Composite {

    private TreeViewer tvXPath;

    private Text txtFilter;

    private Text txtXPath;

    private Combo comboDataModels;

    private IAllDataModelHolder allDataModelHolder;

    private String defaultSelectedDataModel;

    private String conceptName;

    private static String context;

    private SchemaNameFilter xpathTopElementNameFilter;

    private boolean isAbsolutePath = false;

    private IXPathSelectionFilter xpathSelectionFilter;

    public SelectXPathComposite(Composite parent, int style, IAllDataModelHolder allDataModelHolder,
            String defaultSelectedDataModel) {
        this(parent, style, allDataModelHolder, defaultSelectedDataModel, null, false);
    }

    public SelectXPathComposite(Composite parent, int style, IAllDataModelHolder allDataModelHolder,
            String defaultSelectedDataModel, String conceptName) {
        this(parent, style, allDataModelHolder, defaultSelectedDataModel, conceptName, false);
    }

    public SelectXPathComposite(Composite parent, int style, IAllDataModelHolder allDataModelHolder,
            String defaultSelectedDataModel, String conceptName, boolean isAbsolutePath) {
        this(parent, style, allDataModelHolder, defaultSelectedDataModel, conceptName, false, null);
    }

    public SelectXPathComposite(Composite parent, int style, IAllDataModelHolder allDataModelHolder,
            String defaultSelectedDataModel, String conceptName, boolean isAbsolutePath, IXPathSelectionFilter filter) {
        super(parent, style);

        this.allDataModelHolder = allDataModelHolder;
        this.defaultSelectedDataModel = defaultSelectedDataModel;
        this.conceptName = conceptName;
        this.isAbsolutePath = isAbsolutePath;
        this.xpathSelectionFilter = filter;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        setLayout(gridLayout);

        final Label lblDataModels = new Label(this, SWT.NONE);
        lblDataModels.setText(Messages.SelectXPathComposite_DataModels);

        comboDataModels = new Combo(this, SWT.READ_ONLY);
        comboDataModels.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label lblXPath = new Label(this, SWT.NONE);
        lblXPath.setText(Messages.SelectXPathComposite_XPath);

        txtXPath = new Text(this, SWT.READ_ONLY | SWT.BORDER);
        final GridData gd_txtXPath = new GridData(SWT.FILL, SWT.CENTER, true, false);
        txtXPath.setLayoutData(gd_txtXPath);

        final Label lblFilter = new Label(this, SWT.NONE);
        lblFilter.setText(Messages.SelectXPathComposite_Filters);

        txtFilter = new Text(this, SWT.BORDER);
        final GridData gd_txtFilter = new GridData(SWT.FILL, SWT.CENTER, true, false);
        txtFilter.setLayoutData(gd_txtFilter);

        tvXPath = new TreeViewer(this, SWT.BORDER);
        tvXPath.setContentProvider(new XPathContentProvider());
        tvXPath.setLabelProvider(new XSDTreeLabelProvider(xpathSelectionFilter));
        xpathTopElementNameFilter = new SchemaNameFilter(new SchemaElementNameFilterDes(true, "*")); //$NON-NLS-1$
        tvXPath.setFilters(new ViewerFilter[] { xpathTopElementNameFilter });

        Tree tree = tvXPath.getTree();
        final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        tree.setLayoutData(gd_tree);
        //

        initUIListeners();

        initUIContents();
    }

    private void initUIListeners() {

        initListenerForDataModelCombo();

        initListenerForFilterTextBox();

        initListenerForXPathTree();
    }

    private void initUIContents() {

        initDataModelComboContents();

        initXPathTreeContents();

        initFilterText();
    }

    private void initFilterText() {
        if (conceptName != null) {
            txtFilter.setText(conceptName);
        }
    }

    private void initDataModelComboContents() {

        List<String> allCurDataModels = Arrays.asList(allDataModelHolder.getAllDataModelNames());
        comboDataModels.setItems(allCurDataModels.toArray(new String[0]));

        if (allCurDataModels.size() > 0) {
            comboDataModels.select(0);
        }

        if (allCurDataModels.contains(defaultSelectedDataModel)) {
            comboDataModels.select(allCurDataModels.indexOf(defaultSelectedDataModel));
        }
    }

    private void initXPathTreeContents() {

        tvXPath.setInput(allDataModelHolder.getDataModel(comboDataModels.getText().trim()));
    }

    private void initListenerForDataModelCombo() {

        comboDataModels.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {

                String old = comboDataModels.getText().trim();
                String[] allModels = allDataModelHolder.getAllDataModelNames();

                comboDataModels.setItems(allModels);

                if (Arrays.asList(allModels).contains(old)) {
                    comboDataModels.setText(old);
                }
            }

        });

        comboDataModels.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                txtXPath.setText("");//$NON-NLS-1$
                tvXPath.setInput(allDataModelHolder.getDataModel(comboDataModels.getText().trim()));
            }
        });

    }

    private void initListenerForFilterTextBox() {

        txtFilter.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                xpathTopElementNameFilter.setNameFilterDes(new SchemaElementNameFilterDes(true, getFilterExpression()));
                tvXPath.refresh();
            }
        });

    }

    private void initListenerForXPathTree() {

        ISelectionChangedListener listener = new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                txtXPath.setText(getXpath());
                IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                boolean enable = false;
                if(xpathSelectionFilter != null) {
                    enable = xpathSelectionFilter.check(sel.getFirstElement()) == FilterResult.ENABLE;
                }
                if(propertyListener != null) {
                    propertyListener.propertyChange(new PropertyChangeEvent(event.getSource(), "selection", null, enable)); //$NON-NLS-1$
                }
            }
        };
        addSelectionChangedListener(listener);

    }

    List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

    private PropertyChangeListener propertyListener;

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
        tvXPath.addSelectionChangedListener(listener);
    }

    @Override
    public void dispose() {
        for (ISelectionChangedListener listner : selectionChangedListeners) {
            tvXPath.removeSelectionChangedListener(listner);
        }
        selectionChangedListeners.clear();
        super.dispose();
    }

    public String getSelectedXPath() {
        return txtXPath.getText().trim();
    }

    private String getFilterExpression() {
        String starFlag = "*"; //$NON-NLS-1$

        String filterText = txtFilter.getText().trim();
        if (filterText.isEmpty()) {
            return starFlag;
        }

        return filterText + starFlag;
    }

    private String getXpath() {

        String path = "";//$NON-NLS-1$
        String totalXpath = "";//$NON-NLS-1$
        TreeItem item;
        TreeItem[] items = tvXPath.getTree().getSelection();
        for (int i = 0; i < items.length; i++) {
            item = items[i];
            XSDConcreteComponent component = (XSDConcreteComponent) item.getData();
            if (!(component instanceof XSDParticle) && !(component instanceof XSDElementDeclaration)) {
                continue;
            }
            do {
                component = (XSDConcreteComponent) item.getData();
                if (component instanceof XSDParticle) {
                    if (((XSDParticle) component).getTerm() instanceof XSDElementDeclaration) {
                        path = "/" + ((XSDElementDeclaration) ((XSDParticle) component).getTerm()).getName() + path;//$NON-NLS-1$
                    }
                } else if (component instanceof XSDElementDeclaration) {
                    path = (isAbsolutePath ? "/" : "") + ((XSDElementDeclaration) component).getName() + path;//$NON-NLS-1$//$NON-NLS-2$
                }
                item = item.getParentItem();

            } while (item != null);
            if (i == 0) {
                totalXpath = path;
            } else {
                totalXpath += "&" + path;//$NON-NLS-1$
            }
            path = "";//$NON-NLS-1$
        }// for(i=0
        if (context != null && conceptName != null) {

            if (totalXpath.equals(conceptName)) {
                totalXpath = totalXpath.replaceAll(conceptName, "/");//$NON-NLS-1$
            } else {
                totalXpath = totalXpath.replaceAll(conceptName + "/", "");//$NON-NLS-1$//$NON-NLS-2$
            }
            if (totalXpath.equals(context) || totalXpath.equals(context.replaceAll(conceptName + "/", ""))) {//$NON-NLS-1$//$NON-NLS-2$
                totalXpath = ".";//$NON-NLS-1$
            }
            if (totalXpath.indexOf('/') == -1 && !totalXpath.equals(".") && !"/".equals(totalXpath) && !"/".equals(context)//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    && !context.equals(conceptName)) {
                totalXpath = "../" + totalXpath;//$NON-NLS-1$
            }
        }
        return totalXpath;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void lockCombo(boolean lock) {
        comboDataModels.setEnabled(!lock);
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public static String getContext() {
        return context;
    }

    public static void setContext(String c) {
        context = c;
    }

    public void setPropertyChangeListener(PropertyChangeListener propertyListener) {
        this.propertyListener = propertyListener;        
    }

}
