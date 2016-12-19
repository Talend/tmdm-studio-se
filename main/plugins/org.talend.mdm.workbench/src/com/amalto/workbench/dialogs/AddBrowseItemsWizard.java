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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSBoolean;
import org.talend.mdm.webservice.WSDeleteView;
import org.talend.mdm.webservice.WSGetView;
import org.talend.mdm.webservice.WSPutView;
import org.talend.mdm.webservice.WSView;
import org.talend.mdm.webservice.WSViewPK;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.widgets.ComplexTableViewer;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;

public class AddBrowseItemsWizard extends Wizard {

    private static Log log = LogFactory.getLog(AddBrowseItemsWizard.class);

    protected DataModelMainPage page;

    private TMDMService service;

    protected List<XSDElementDeclaration> declList = null;

    private Map<String, List<Line>> browseItemToRoles = new HashMap<String, List<Line>>();

    protected static String INSTANCE_NAME = "Browse Item View";//$NON-NLS-1$

    public static String BROWSE_ITEMS = "Browse_items_";//$NON-NLS-1$

    private static ComplexTableViewerColumn[] roleConfigurationColumns = new ComplexTableViewerColumn[] {
            new ComplexTableViewerColumn("Role Name", false, "", "", "", ComplexTableViewerColumn.COMBO_STYLE, new String[] {}, 0),//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
            new ComplexTableViewerColumn("Access", false, "", "", "", ComplexTableViewerColumn.COMBO_STYLE, new String[] {}, 0) };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

    private ConfigureRolePage configureRolePage;

    private IAddBrowseItemsWizardExAdapter exAdapter;

    public AddBrowseItemsWizard(DataModelMainPage launchPage, List<XSDElementDeclaration> list) {
        this(launchPage);
        setDeclarations(list);
    }

    public AddBrowseItemsWizard(DataModelMainPage launchPage) {
        super();
        setWindowTitle(Messages.GenerateBrowseViews);
        page = launchPage;
        this.exAdapter = ExAdapterManager.getAdapter(this, IAddBrowseItemsWizardExAdapter.class);
    }

    public void setDeclarations(List<XSDElementDeclaration> list) {
        declList = list;
        for (XSDElementDeclaration dl : declList) {
            browseItemToRoles.put(BROWSE_ITEMS + dl.getName(), new ArrayList<Line>());
        }
    }

    @Override
    public void addPages() {
        configureRolePage = new ConfigureRolePage();
        addPage(configureRolePage);
    }

    @Override
    public boolean performFinish() {
        configureRolePage.applyChangeToRoles();
        if (saveConfiguration()) {
            // page.getXObject().fireEvent(IXObjectModelListener.NEED_REFRESH,
            // null, page.getXObject().getParent().getParent());
            return true;
        }

        return false;
    }

    protected void newBrowseItemView(String browseItem) {
        for (XSDElementDeclaration decl : declList) {
            String fullName = BROWSE_ITEMS + decl.getName();
            if (fullName.equals(browseItem)) {

                TreeParent serverRoot = page.getXObject().getServerRoot();
                TreeParent serverFolder = serverRoot.findServerFolder(TreeObject.VIEW);
                TreeObject obj = serverFolder.findObject(TreeObject.VIEW, browseItem);

                if (obj != null) {

                    IEditorInput xobjectEditorinput = new XObjectEditorInput(obj, obj.getDisplayName());
                    final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    IEditorPart currentEditor = activePage.findEditor(xobjectEditorinput);

                    if (currentEditor != null) {// editor is opened
                        if (MessageDialog.openConfirm(this.getShell(), Messages.Warning,
                                Messages.AddBrowseItemsWizard_DuplicatedView)) {
                            refreshEditorContent(obj);
                        } else {
                            break;
                        }

                    }
                }

                obj = createNewTreeObject(decl, browseItem);
                TreeParent folder = obj.findServerFolder(obj.getType());
                folder.addChild(obj);
            }
        }
    }

    private TreeObject createNewTreeObject(XSDElementDeclaration decl, String browseItem) {
        WSView view = new WSView();
        view.setIsTransformerActive(new WSBoolean(false));
        view.setTransformerPK("");//$NON-NLS-1$
        view.setName(browseItem);
        EList<XSDIdentityConstraintDefinition> idtylist = decl.getIdentityConstraintDefinitions();
        List<String> keys = new ArrayList<String>();
        for (XSDIdentityConstraintDefinition idty : idtylist) {
            EList<XSDXPathDefinition> xpathList = idty.getFields();
            for (XSDXPathDefinition path : xpathList) {
                String key = decl.getName();
                // remove
                key = key.replaceFirst("#.*", "");//$NON-NLS-1$//$NON-NLS-2$
                key += "/" + path.getValue();//$NON-NLS-1$
                keys.add(key);
            }

        }
        view.getSearchableBusinessElements().addAll(keys);
        view.getViewableBusinessElements().addAll(keys);

        StringBuffer desc = new StringBuffer();
        LinkedHashMap<String, String> labels = new LinkedHashMap<String, String>();
        if (decl.getAnnotation() != null) {
            labels = new XSDAnnotationsStructure(decl.getAnnotation()).getLabels();
        }
        if (labels.size() == 0) {
            labels.put("EN", decl.getName());//$NON-NLS-1$
        }
        for (String lan : labels.keySet()) {
            desc.append("[" + lan.toUpperCase() + ":" + labels.get(lan) + "]");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
        view.setDescription(desc.toString());

        WSPutView wrap = new WSPutView();
        wrap.setWsView(view);

        WSViewPK viewPk = new WSViewPK();
        viewPk.setPk(browseItem);

        WSDeleteView delView = new WSDeleteView();
        delView.setWsViewPK(viewPk);
        WSGetView getView = new WSGetView();
        getView.setWsViewPK(viewPk);
        service.putView(wrap);
        // add node in the root
        TreeParent root = page.getXObject().getServerRoot();
        TreeObject obj = new TreeObject(browseItem, root, TreeObject.VIEW, viewPk, null // no storage to save
        // space
        );

        return obj;
    }

    private boolean refreshEditorContent(TreeObject obj) {

        IEditorInput xobjectEditorinput = new XObjectEditorInput(obj, obj.getDisplayName());

        final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart currentEditor = activePage.findEditor(xobjectEditorinput);
        if (currentEditor != null) {
            List<IEditorPart> editors = Arrays.asList(activePage.getDirtyEditors());

            activePage.closeEditor(currentEditor, false);

            if (editors.contains(currentEditor)) {
                return true;
            }
        }

        return false;
    }

    protected void modifyRolesWithAttachedBrowseItem(String browseItem, List<Line> roles) {
        // for (Line line : roles) {
        // List<KeyValue> keyValues = line.keyValues;
        // String roleName = keyValues.get(0).value;
        // XtentisPort port = getXtentisPort();
        // WSGetRole getRole = new WSGetRole();
        // getRole.setWsRolePK(new WSRolePK(roleName));
        // WSRole role = port.getRole(getRole);
        // for (WSRole.Specification spec : role.getSpecification()) {
        //                if (spec.getObjectType().equals("View")) {//$NON-NLS-1$
        // Instance newInstance = new Instance();
        // newInstance.setInstanceName(browseItem);
        //                    newInstance.setWritable(keyValues.get(1).value.equals("Read Only") ? false : true);//$NON-NLS-1$
        // spec.getInstance().add(newInstance);
        // break;
        // }
        // }
        // WSPutRole wrap = new WSPutRole();
        // wrap.setWsRole(role);
        // port.putRole(wrap);
        // }
    }

    private boolean saveConfiguration() {
        Iterator<String> browseIterator = browseItemToRoles.keySet().iterator();
        while (browseIterator.hasNext()) {
            String browse = browseIterator.next();
            List<Line> roles = browseItemToRoles.get(browse);
            try {
                newBrowseItemView(browse);
                modifyRolesWithAttachedBrowseItem(browse, roles);
            } catch (WebServiceException e) {
                if (!Util.handleConnectionException(page, e, null)) {
                    MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                            Messages.bind(Messages.ErrorOccuredSaveView, e.getLocalizedMessage()));
                }
                return false;
            }
        }

        return true;
    }

    public class ConfigureRolePage extends WizardPage {

        private TableViewer browseViewer;

        private ComplexTableViewer complexTableViewer;

        public ConfigureRolePage() {
            super(Messages.ConfigureBrowseViews);
            setTitle(Messages.ConfigureBrowseViews);
            setDescription(Messages.ConfigureTheBrowseViews);

            // Page isn't complete until an e-mail address has been added
            setPageComplete(true);
        }

        public void createControl(Composite parent) {
            Composite composite = new Composite(parent, SWT.BORDER);
            composite.setLayout(new GridLayout(1, false));
            browseViewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            gd.widthHint = 600;
            browseViewer.getControl().setLayoutData(gd);
            ((GridData) browseViewer.getControl().getLayoutData()).heightHint = 100;
            Table table = browseViewer.getTable();
            TableColumn column = new TableColumn(table, SWT.CENTER);
            column.setText(INSTANCE_NAME);
            column.setWidth(615);

            table.setHeaderVisible(true);
            table.setLinesVisible(true);

            CellEditor[] editors = new CellEditor[1];
            editors[0] = new TextCellEditor(table);
            browseViewer.setCellEditors(editors);

            browseViewer.setContentProvider(new IStructuredContentProvider() {

                public void dispose() {
                }

                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                }

                public Object[] getElements(Object inputElement) {
                    ArrayList<XSDElementDeclaration> values = (ArrayList<XSDElementDeclaration>) inputElement;
                    return values.toArray(new XSDElementDeclaration[values.size()]);
                }
            });

            browseViewer.setLabelProvider(new ITableLabelProvider() {

                public boolean isLabelProperty(Object element, String property) {
                    return false;
                }

                public void dispose() {
                }

                public void addListener(ILabelProviderListener listener) {
                }

                public void removeListener(ILabelProviderListener listener) {
                }

                public String getColumnText(Object element, int columnIndex) {

                    return BROWSE_ITEMS + ((XSDElementDeclaration) element).getName();
                }

                public Image getColumnImage(Object element, int columnIndex) {
                    return null;
                }
            });

            browseViewer.setCellModifier(new ICellModifier() {

                public boolean canModify(Object element, String property) {
                    return true;
                }

                public void modify(Object element, String property, Object value) {
                    TableItem item = (TableItem) element;

                    String tValue = value.toString().trim();
                    if (Pattern.compile("^\\s+\\w+\\s*").matcher(value.toString()).matches()//$NON-NLS-1$
                            || tValue.replaceAll("\\s", "").length() != tValue.length()) {//$NON-NLS-1$//$NON-NLS-2$
                        MessageDialog.openInformation(null, Messages.Warning, Messages.NotContainEmpty);
                        return;
                    }

                    if (!value.toString().startsWith(BROWSE_ITEMS)) {
                        MessageDialog.openInformation(null, Messages.Warning, Messages.bind(Messages.NameStartWith, BROWSE_ITEMS));
                        return;
                    }

                    XSDElementDeclaration elem = (XSDElementDeclaration) item.getData();

                    String declName = elem.getName();
                    if (!(BROWSE_ITEMS + declName).equals(tValue)) {
                        for (XSDElementDeclaration theElem : declList) {
                            if (theElem == elem) {
                                continue;
                            }
                            if ((BROWSE_ITEMS + theElem.getName()).equals(tValue)) {
                                MessageDialog.openInformation(null, Messages.Warning, Messages.BrowseNameExists);
                                return;
                            }
                        }

                        // find the real key in browseItemToRoles
                        String key = BROWSE_ITEMS + declName;
                        for (String k : browseItemToRoles.keySet()) {
                            if (k.startsWith(key)) {
                                key = k;
                                break;
                            }
                        }

                        List<Line> lines = browseItemToRoles.get(key);
                        browseItemToRoles.remove(key);
                        int prex = tValue.indexOf(BROWSE_ITEMS);

                        if (prex != -1 && (prex + BROWSE_ITEMS.length()) <= tValue.length()) {
                            elem.setName(tValue.substring(prex + BROWSE_ITEMS.length()));
                        }
                        browseItemToRoles.put(tValue, lines);
                        refreshRoleView(BROWSE_ITEMS + elem.getName());
                        browseViewer.update(elem, null);

                        elem.setName(declName);
                    }
                }

                public Object getValue(Object element, String property) {
                    XSDElementDeclaration elem = (XSDElementDeclaration) element;
                    return BROWSE_ITEMS + elem.getName();
                }
            });

            // Listen for changes in the selection of the viewer to display additional parameters
            browseViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                public void selectionChanged(SelectionChangedEvent event) {
                    applyChangeToRoles();
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    if (selection.size() > 1) {
                        List selectObjs = selection.toList();
                        refreshRoleView(selectObjs);
                        UpdateComplexViewButton(true);
                    } else if (selection.size() == 1) {

                        XSDElementDeclaration decl = (XSDElementDeclaration) selection.getFirstElement();

                        refreshRoleView(BROWSE_ITEMS + decl.getName());

                        UpdateComplexViewButton(true);
                    }
                }

                private void UpdateComplexViewButton(final boolean b) {
                    if (exAdapter != null) {
                        exAdapter.UpdateComplexViewButton(b);
                    }

                }
            });
            browseViewer.setInput(declList);
            browseViewer.setColumnProperties(new String[] { INSTANCE_NAME });
            browseViewer.refresh();
            if (exAdapter != null) {
                exAdapter.createRoleControl(composite);
            }
            setControl(composite);
        }

        /**
         * DOC HHB Comment method "refreshRoleView".
         * 
         * @param string
         */
        protected void refreshRoleView(String browseItem) {
            if (exAdapter != null) {
                exAdapter.refreshRoleView(browseItem);
            }

        }

        private Map<XSDElementDeclaration, Map<String, List<Line>>> allItemToRoles = new HashMap<XSDElementDeclaration, Map<String, List<Line>>>();

        private boolean isCommitMultiChanges = false;

        private List selectedMultiViews = null;

        private List<Line> multiChanges = new LinkedList<Line>();

        private void refreshRoleView(List selectObjs) {
            isCommitMultiChanges = true;
            selectedMultiViews = selectObjs;
            multiChanges.clear();
            //
            if (exAdapter != null) {
                exAdapter.refreshRoleView(multiChanges);
            }
        }

        private void applyChangeToRoles() {
            if (isCommitMultiChanges && selectedMultiViews != null && multiChanges.size() > 0) {
                for (Object obj : selectedMultiViews) {
                    XSDElementDeclaration decl = (XSDElementDeclaration) obj;
                    String browseItem = AddBrowseItemsWizard.BROWSE_ITEMS + decl.getName();
                    for (Line line : multiChanges) {
                        List<Line> lines = browseItemToRoles.get(browseItem);
                        Line newLine = line.clone();
                        if (!lines.contains(newLine)) {
                            lines.add(line);
                        }
                    }
                }
                selectedMultiViews = null;
                isCommitMultiChanges = false;
                multiChanges.clear();
            }
        }
    }

    /**
     * DOC hbhong Comment method "getAllRoleNames".
     * 
     * @return
     */
    public List<String> getAllRoleNames() {
        return Util.getChildren(page.getXObject().getServerRoot(), TreeObject.ROLE);
    }

    public static ComplexTableViewerColumn[] getRoleConfigurationColumns() {
        return roleConfigurationColumns;
    }

    public Map<String, List<Line>> getBrowseItemToRoles() {
        return this.browseItemToRoles;
    }
}
