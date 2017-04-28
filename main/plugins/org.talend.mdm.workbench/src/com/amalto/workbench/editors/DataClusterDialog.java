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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.talend.core.GlobalServiceRegister;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.service.ILegendServerDefService;
import com.amalto.workbench.utils.LineItem;
import com.amalto.workbench.utils.MDMServerDef;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;

/**
 * created by liusongbo on 2013-1-24
 *
 */
public class DataClusterDialog extends Dialog {

    private static final Log log = LogFactory.getLog(DataClusterDialog.class);

    private TreeObject model;

    private MDMServerDef oldServerDef = null;

    private Text textViewer;

    private ComboViewer serverComboViewer;

    private ComboViewer containerComboViewer;

    private DataClusterComposite clusterComposite;

    private String recordContent;

    private LineItem selected;

    private IWorkbenchPartSite site;

    private Color defaultColor;

    private Color greyColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

    private List<TreeObject> dataContainers;

    private List<MDMServerDef> allServerDefs;

    private boolean hasdefaultServer = false;

    private String okLabel;

    private String cancelLabel;

    private SelectionListener additionSelectionListener;

    public DataClusterDialog(Shell parentShell, TreeObject treeObject, IWorkbenchPartSite site) {
        super(parentShell);
        this.model = treeObject;
        this.site = site;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setSize(600, 550);
        newShell.setText(Messages.DataClusterDialog_0);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        //
        Composite firstLine = createFirstLine(parent);
        firstLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite lastPortion = createLastPortion(parent);
        lastPortion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        hookService();

        hookContextMenu();

        init();

        return parent;
    }

    @Override
    protected void initializeBounds() {
        super.initializeBounds();
        Point location = getInitialLocation(getShell().getSize());
        getShell().setLocation(location.x, location.y);
    }

    private Composite createFirstLine(Composite parent) {
        Composite firstLine = new Composite(parent, SWT.NONE);
        firstLine.setLayout(new GridLayout(4, false));

        Label serverLabel = new Label(firstLine, SWT.NONE);
        serverLabel.setText(Messages.DataClusterDialog_1);
        serverLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        serverComboViewer = new ComboViewer(firstLine, SWT.DROP_DOWN | SWT.READ_ONLY);
        Combo combo = serverComboViewer.getCombo();
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        data.widthHint = 200;
        combo.setLayoutData(data);
        serverComboViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                MDMServerDef serverDef = (MDMServerDef) element;
                return serverDef.getName() + " (" + serverDef.getHost() + ")"; //$NON-NLS-1$ //$NON-NLS-2$;
            }
        });
        serverComboViewer.setContentProvider(new ArrayContentProvider());

        serverComboViewer.setInput(getAllServerDefs());

        Label containerLabel = new Label(firstLine, SWT.NONE);
        containerLabel.setText(Messages.DataClusterDialog_2);
        containerLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        containerComboViewer = new ComboViewer(firstLine, SWT.DROP_DOWN | SWT.READ_ONLY);
        Combo containerCombo = containerComboViewer.getCombo();
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        layoutData.widthHint = 200;
        containerCombo.setLayoutData(layoutData);
        containerComboViewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                TreeObject treeObj = (TreeObject) element;

                return treeObj.getName();
            }
        });

        containerComboViewer.setContentProvider(new ArrayContentProvider());
        containerComboViewer.setInput(new TreeObject[0]);
        containerComboViewer.getCombo().setEnabled(false);

        return firstLine;
    }

    private List<MDMServerDef> getAllServerDefs() {
        if (allServerDefs == null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ILegendServerDefService.class)) {
                ILegendServerDefService serverDefService = (ILegendServerDefService) GlobalServiceRegister.getDefault()
                        .getService(ILegendServerDefService.class);
                allServerDefs = serverDefService.getLegendServerDefs();
            }
        }

        return allServerDefs;
    }

    private Composite createLastPortion(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.DataClusterDialog_3);
        group.setLayout(new FillLayout());

        SashForm splitter = new SashForm(group, SWT.VERTICAL);
        splitter.setSashWidth(8);
        clusterComposite = new DataClusterComposite(splitter, SWT.NONE, model, true, site);
        clusterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        clusterComposite.setEnabled(false);

        Composite contentComp = new Composite(splitter, SWT.NONE);
        contentComp.setLayout(new GridLayout());
        Label recordContentLabel = new Label(contentComp, SWT.NONE);
        recordContentLabel.setText(Messages.DataClusterDialog_4);

        textViewer = new Text(contentComp, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
        textViewer.setEditable(false);
        textViewer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        textViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        splitter.setWeights(new int[] { 2, 1 });

        defaultColor = clusterComposite.getBackground();

        changeWidgetColor(greyColor);

        return group;
    }

    private void hookService() {
        serverComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                refreshDataContainerCombo();

                MDMServerDef serverDef = getSelectedMdmServerDef();
                if (serverDef != oldServerDef) {
                    oldServerDef = serverDef;
                    clusterComposite.changeToServer(serverDef);

                    changeWidgetColor(greyColor);
                }
                selectDefaultContainer();
            }
        });

        containerComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection ssel = (IStructuredSelection) containerComboViewer.getSelection();
                TreeObject dataContainer = (TreeObject) ssel.getFirstElement();
                boolean refreshed = clusterComposite.changeToDataContainer(dataContainer);

                if (refreshed) {
                    changeWidgetColor(defaultColor);
                } else {
                    changeWidgetColor(greyColor);
                    containerComboViewer.setInput(dataContainers);
                }

                model = clusterComposite.getDataContainer();
            }
        });

        TableViewer resultsViewer = clusterComposite.getResultsViewer();
        resultsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection structedSelection = (IStructuredSelection) event.getSelection();
                selected = (LineItem) structedSelection.getFirstElement();

                showInTextWidget(selected);
            }
        });
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                manager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new NewItemAction(site.getShell()));
            }
        });
        Menu menu = menuMgr.createContextMenu(clusterComposite.getResultsViewer().getControl());
        clusterComposite.getResultsViewer().getControl().setMenu(menu);
    }

    private void init() {
        if (hasdefaultServer) {
            selectDefaultServer();

            selectDefaultContainer();
        }
    }

    private void selectDefaultServer() {
        for (MDMServerDef serverDef : getAllServerDefs()) {
            if (serverDef.getName().equals(oldServerDef.getName())) {
                serverComboViewer.setSelection(new StructuredSelection(serverDef), true);
                break;
            }
        }
    }

    private void selectDefaultContainer() {
        for (TreeObject treeObj : dataContainers) {
            if (treeObj.getName().equals("UpdateReport")) { //$NON-NLS-1$
                containerComboViewer.setSelection(new StructuredSelection(treeObj));
                break;
            }
        }
    }

    private void changeWidgetColor(Color color) {
        WidgetUtils.changeWidgetColor(clusterComposite, color, new boolean[] { true, false });
        if (!textViewer.getEditable()) {
            textViewer.setBackground(color);
        }
    }

    private void showInTextWidget(LineItem lineItem) {
        if (lineItem == null) {
            textViewer.setText(""); //$NON-NLS-1$
            recordContent = ""; //$NON-NLS-1$
            return;
        }

        try {
            final TMDMService service = Util.getMDMService(model);
            final WSItem wsItem = service.getItem(new WSGetItem(new WSItemPK(lineItem.getConcept().trim(), Arrays.asList(lineItem
                    .getIds()), (WSDataClusterPK) model.getWsKey())));
            recordContent = Util.formatXsdSource(wsItem.getContent());
            textViewer.setText(recordContent);
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages._Error,
                    Messages.bind(Messages.DataClusterBrowserMainPage_36, e.getLocalizedMessage()));
        }
    }

    private boolean refreshDataContainerCombo() {
        dataContainers = new ArrayList<TreeObject>();
        boolean sucessed = getAllDataContainers(dataContainers);
        containerComboViewer.setInput(dataContainers);
        containerComboViewer.getCombo().setEnabled(sucessed);

        return sucessed;
    }

    private boolean getAllDataContainers(List<TreeObject> dataContainers) {
        MDMServerDef serverDef = getSelectedMdmServerDef();

        String username = serverDef.getUser();
        String password = serverDef.getPasswd();
        String serverName = serverDef.getName();
        String endpointaddress = serverDef.getUrl();

        boolean canConnect = checkConnection(endpointaddress, username, password);
        if (!canConnect) {
            MessageDialog.openError(site.getShell(), Messages.DataClusterDialog_7, Messages.DataClusterDialog_8);
            return false;
        }

        List<WSDataClusterPK> xdcPKs = null;
        try {
            TMDMService service = Util.getMDMService(new URL(endpointaddress), username, password);
            TreeParent serverRoot = new TreeParent(serverName, null, TreeObject._SERVER_, endpointaddress, username
                    + ":" + (password == null ? "" : password));//$NON-NLS-1$//$NON-NLS-2$

            xdcPKs = service.getDataClusterPKs(new WSRegexDataClusterPKs("*")).getWsDataClusterPKs();//$NON-NLS-1$
            for (WSDataClusterPK pk : xdcPKs) {
                String name = pk.getPk();
                if (!("CACHE".equals(name))) { //$NON-NLS-1$
                    WSDataCluster wsObject = null;
                    boolean retriveWSObject = false;
                    try {
                        if (retriveWSObject) {
                            wsObject = service.getDataCluster(new WSGetDataCluster(pk));
                        }
                        TreeObject obj = new TreeObject(name, serverRoot, TreeObject.DATA_CLUSTER, pk, wsObject);
                        dataContainers.add(obj);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    private boolean checkConnection(String endpointaddress, String username, String password) {
        ILegendServerDefService serverDefService = (ILegendServerDefService) GlobalServiceRegister.getDefault().getService(
                ILegendServerDefService.class);
        return serverDefService.checkServerDefConnection(endpointaddress, username, password);
    }

    private MDMServerDef getSelectedMdmServerDef() {
        IStructuredSelection structuredSelection = (IStructuredSelection) serverComboViewer.getSelection();
        return (MDMServerDef) structuredSelection.getFirstElement();
    }

    public void setDefaultServerDef(MDMServerDef defaultServerDef) {
        oldServerDef = defaultServerDef;
        hasdefaultServer = true;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button okBtn = createButton(parent, Dialog.OK, okLabel != null ? okLabel : IDialogConstants.OK_LABEL, false);
        createButton(parent, Dialog.CANCEL, cancelLabel != null ? cancelLabel : IDialogConstants.CANCEL_LABEL, false);
        if (additionSelectionListener != null) {
            okBtn.addSelectionListener(additionSelectionListener);
        }
    }

    @Override
    protected void okPressed() {
        if (additionSelectionListener == null) {
            recordContent = textViewer.getText().trim();
            super.okPressed();
        }
    }

    public void setOkLabel(String okLabel) {
        this.okLabel = okLabel;
        if (getButton(Dialog.OK) != null) {
            getButton(Dialog.OK).setText(okLabel);
        }
    }

    public void setCancelLabel(String cancelLabel) {
        this.cancelLabel = cancelLabel;
        if (getButton(Dialog.CANCEL) != null) {
            getButton(Dialog.CANCEL).setText(cancelLabel);
        }
    }

    public void setSelectionListener(SelectionListener listener) {
        this.additionSelectionListener = listener;
    }

    public Text getText() {
        return textViewer;
    }

    public MDMServerDef getServerDef() {
        return oldServerDef;
    }

    public String getDataContainer() {
        return model.getName();
    }

    public String getConcept() {
        if (selected != null) {
            return selected.getConcept();
        }

        return null;
    }

    public String getRecordContent() {
        return recordContent;
    }

    public String[] getRecordIds() {
        if (selected != null) {
            return selected.getIds();
        }
        return null;
    }

    class NewItemAction extends Action {

        private Shell shell;

        public NewItemAction(Shell shell) {
            this.shell = shell;
            setImageDescriptor(ImageCache.getImage("icons/add_obj.gif")); //$NON-NLS-1$
            setText(Messages.DataClusterBrowserMainPage_98);
            setToolTipText(Messages.DataClusterBrowserMainPage_99);
        }

        @Override
        public void run() {
            try {
                final TMDMService service = Util.getMDMService(new URL(oldServerDef.getUrl()), oldServerDef.getUser(),
                        oldServerDef.getPasswd());
                boolean created = NewItemHandler.getNewInstance().createItemRecord(service, shell,
                        new WSDataClusterPK(getDataContainer()), true);
                if (created) {
                    clusterComposite.doSearch();
                }
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
            } catch (XtentisException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
