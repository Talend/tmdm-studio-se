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

import java.awt.Panel;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.detailtabs.sections.util.MDMRepositoryViewExtensionService;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter.FilterResult;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XPathTreeContentProvider;
import com.amalto.workbench.providers.XSDTreeLabelProvider;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetDataModel;

public class XpathSelectDialog extends Dialog {

    private static Log log = LogFactory.getLog(XpathSelectDialog.class);

    protected Label schemaLabel = null;

    protected Combo languagesCombo;

    protected TreeViewer domViewer;

    private String title = Messages.XpathSelectDialog_SelectXpath;

    protected TreeParent parent;

    protected Combo dataModelCombo;

    // protected TreeObject xobject;
    protected DrillDownAdapter drillDownAdapter;

    protected IWorkbenchPartSite site;

    protected Panel panel;

    protected Text xpathText;

    private Text filterText;

    private boolean keepFilterContent;

    protected Button add;

    protected SelectionListener listener;

    // TODO:check This two static String and there related static methods may cause some problems.
    public String dataModelName;

    protected boolean keepOneDatamodel;

    private String xpath = "";//$NON-NLS-1$

    private boolean isMulti = true;

    protected String conceptName;

    protected static String context;

    private List<String> avaiList;

    private boolean isAbsolutePath = false;

    private boolean lock;

    private IXPathSelectionFilter selectionFilter;

    public XpathSelectDialog(Shell parentShell, TreeParent parent, String title, IWorkbenchPartSite site, boolean isMulti,
            String dataModelName) {
        this(parentShell, parent, title, site, isMulti, dataModelName, false);
    }

    public XpathSelectDialog(Shell parentShell, TreeParent parent, String title, IWorkbenchPartSite site, boolean isMulti,
            String dataModelName, boolean isAbsolutePath) {
        this(parentShell, parent, title, site, isMulti, dataModelName, isAbsolutePath, null);
    }

    public XpathSelectDialog(Shell parentShell, TreeParent parent, String title, IWorkbenchPartSite site, boolean isMulti,
            String dataModelName, boolean isAbsolutePath, IXPathSelectionFilter filter) {
        super(parentShell);
        this.title = title;
        this.parent = parent;
        this.site = site;
        this.isMulti = isMulti;
        this.isAbsolutePath = isAbsolutePath;
        this.selectionFilter = filter;
        if (dataModelName != null) {
            this.dataModelName = dataModelName;// default dataModel
        }
        if (site == null) {
            this.site = MDMRepositoryViewExtensionService.getMDMRepositoryViewSite();
        }

    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getEntityName() {

        if (xpath == null || "".equals(xpath)) {
            return "";//$NON-NLS-1$
        }

        String[] parts = xpath.split("/");//$NON-NLS-1$

        for (String eachPart : parts) {

            if ("".equals(eachPart)) {
                continue;
            }

            return eachPart;
        }

        return "";//$NON-NLS-1$
    }

    private String getXpath(StructuredSelection sel) {

        String path = "";//$NON-NLS-1$
        String totalXpath = "";//$NON-NLS-1$
        TreeItem item;
        TreeItem[] items = domViewer.getTree().getSelection();
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

    protected List<String> getAvailableDataModel() {
        return Util.getDataModel(this.parent, dataModelName, conceptName);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(this.title);
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.makeColumnsEqualWidth = false;
        layout.numColumns = 2;
        Label datamoelsLabel = new Label(composite, SWT.NONE);
        GridData dg = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        datamoelsLabel.setLayoutData(dg);
        datamoelsLabel.setText(Messages.XpathSelectDialog_DataModels);
        dg = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        dg.widthHint = 400;
        dataModelCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
        dataModelCombo.setLayoutData(dg);
        // Modified by hbhong,to fix bug 21784|the following can cause potential error,so comment it
        // if (this.parent == null) {
        // this.parent = (TreeParent) ServerView.show().getRoot().getChildren()[0];
        // }
        // The ending| bug:21784

        final TreeParent tree = this.parent == null ? null : this.parent.findServerFolder(TreeObject.DATA_MODEL);

        // filter the datamodel according to conceptName
        if (tree != null) {
            avaiList = getAvailableDataModel();
        } else {
            avaiList = MDMRepositoryViewExtensionService.findAllDataModelNames();
        }

        if(keepOneDatamodel && dataModelName != null) {
            avaiList.clear();
            avaiList.add(dataModelName);
        }
        dataModelCombo.setItems(avaiList.toArray(new String[avaiList.size()]));
        dataModelCombo.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
            }

            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                changeDomTree(tree, filterText.getText());
            }
        });
        schemaLabel = new Label(composite, SWT.NONE);
        schemaLabel.setText(Messages.XpathSelectDialog_Xpath);
        schemaLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        ((GridData) schemaLabel.getLayoutData()).widthHint = 10;
        xpathText = new Text(composite, SWT.BORDER);
        xpathText.setEditable(false);
        xpathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        // add the filter for the xpath,see bug 0016511: Entity filtering in select multiple xpath dialog:
        Label filterLabel = new Label(composite, SWT.NONE);
        filterLabel.setText(Messages.XpathSelectDialog_Filter);
        filterLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        filterText = new Text(composite, SWT.BORDER);
        filterText.setEditable(true);
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        if (conceptName != null) {
            filterText.setText(conceptName);
            if(keepFilterContent){
                filterText.setEditable(false);
            }
        } else {
            filterText.setText("");//$NON-NLS-1$
        }
        filterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String filter = ((Text) e.widget).getText();
                changeDomTree(tree, filter);
            }
        });
        if (isMulti) {
            domViewer = new TreeViewer(composite, SWT.H_SCROLL | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        } else {
            domViewer = new TreeViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        }
        int index = avaiList.indexOf(dataModelName);
        if (index < 0) {
            dataModelCombo.select(0);
        } else {
            dataModelCombo.select(index);
        }
        dataModelCombo.setEnabled(!lock);
        changeDomTree(tree, filterText.getText());

        domViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        ((GridData) domViewer.getControl().getLayoutData()).heightHint = 400;
        ((GridData) domViewer.getControl().getLayoutData()).widthHint = 400;
        return composite;
    }

    protected void changeDomTree(final TreeParent pObject, String filter) {
        String modelDisplay = dataModelCombo.getText();
        if (modelDisplay.length() == 0) {
            return;
        }
        this.dataModelName = modelDisplay;
        // this.selectedDataModelName = modelDisplay;
        // xobject = pObject.findObject(TreeObject.DATA_MODEL, modelDisplay);
        TMDMService service = null;
        try {
            service = Util.getMDMService(pObject);
        } catch (XtentisException e3) {
            log.error(e3.getMessage(), e3);
        } catch (Exception e3) {
            log.error(e3.getMessage(), e3);
        }
        WSDataModel wsDataModel = null;

        String schema = null;
        XSDSchema xsd = null;
        if (service == null) {
            xsd = MDMRepositoryViewExtensionService.getDataModelXsd(pObject, filter, dataModelName);
            provideViwerContent(xsd, filter);
        } else {

            try {
                wsDataModel = service.getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelName)));
                // XSDSchema xsdSchema = Util.getXSDSchema(wsDataModel.getXsdSchema());
                schema = wsDataModel.getXsdSchema();// Util.nodeToString(xsdSchema.getDocument());
                xsd = Util.createXsdSchema(schema, pObject);
                provideViwerContent(xsd, filter);
            } catch (Exception e1) {
                log.error(e1.getMessage(), e1);
            }
        }

    }// changeDomTree(

    protected void provideViwerContent(XSDSchema xsdSchema, String filter) {
        drillDownAdapter = new DrillDownAdapter(domViewer);
        domViewer.setLabelProvider(new XSDTreeLabelProvider(selectionFilter));
        XPathTreeContentProvider provider = new XPathTreeContentProvider(this.site, xsdSchema, parent, filter);
        // filter the entity with the filter text but not the concept name.
        // provider.setConceptName(this.conceptName);
        domViewer.setContentProvider(provider);

        domViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent e) {
                StructuredSelection sel = (StructuredSelection) e.getSelection();
                xpath = getXpath(sel);
                xpathText.setText(xpath);
                boolean enable = false;
                if (selectionFilter == null) {
                    enable = xpath.length() > 0;
                } else {
                    enable = xpath.length() > 0 && (selectionFilter.check(sel.getFirstElement()) == FilterResult.ENABLE);
                }
                getButton(IDialogConstants.OK_ID).setEnabled(enable);
            }
        });
        domViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        domViewer.setSorter(new ViewerSorter() {

            @Override
            public int category(Object element) {
                // we want facets before Base TypeDefinitions in
                // SimpleTypeDefinition
                if (element instanceof XSDFacet) {
                    return 100;
                }
                // unique keys after element declarations and before other keys
                if (element instanceof XSDIdentityConstraintDefinition) {
                    XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) element;
                    if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                        return 300;
                    } else if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.KEY_LITERAL)) {
                        return 301;
                    } else {
                        return 302;
                    }
                }
                return 200;
            }

            @Override
            public int compare(Viewer theViewer, Object e1, Object e2) {
                int cat1 = category(e1);
                int cat2 = category(e2);
                return cat1 - cat2;
            }
        });
        domViewer.setInput(site);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control btnBar = super.createButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(Messages._Add);
        return btnBar;
    }

    public String getXpath() {
        return xpath;
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public void setKeepFilterContent(boolean keepFilterContent) {
        this.keepFilterContent = keepFilterContent;
    }

    public void setOnlyOneDataModel(boolean onlyOneDataModel) {
        this.keepOneDatamodel = onlyOneDataModel;
    }

    public static String getContext() {
        return context;
    }

    public static void setContext(String c) {
        context = c;
    }

    public void setSelectionFilter(IXPathSelectionFilter selectionFilter) {
        this.selectionFilter = selectionFilter;
    }

}
