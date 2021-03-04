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
package org.talend.mdm.repository.ui.widgets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.progress.UIJob;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.service.ConsistencyService;
import org.talend.mdm.repository.core.service.ConsistencyService.CompareResultEnum;
import org.talend.mdm.repository.core.service.ConsistencyService.ConsistencyData;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.InteractiveService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryLabelProvider;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSDigest;
import com.amalto.workbench.webservices.WSDigestKey;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryViewObjectCheckedWidget extends Composite {

    protected static Log log = LogFactory.getLog(RepositoryViewObjectCheckedWidget.class);

    IRepositoryViewObjectCheckedWidgetExAdapter adapter = null;

    class ContentProvider implements ITreeContentProvider {

        public void dispose() {
        }

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IRepositoryViewObject[]) {
                return (IRepositoryViewObject[]) parentElement;
            } else if (parentElement instanceof FolderRepositoryObject) {
                FolderRepositoryObject folderRepositoryObject = (FolderRepositoryObject) parentElement;
                Item item = folderRepositoryObject.getProperty().getItem();
                return RepositoryResourceUtil.findViewObjects(folderRepositoryObject.getRepositoryObjectType(), item, true, true)
                        .toArray();
            }
            return new Object[0];
        }

        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private HashMap<String, AbstractDeployCommand> cmdMap;

    private boolean hasSameServerDef = true;

    private IRepositoryViewObject[] input;

    private MDMServerDef lastServerDef = null;

    private CheckboxTreeViewer treeViewer;

    private TreeViewerColumn localTimeColumn;

    private TreeViewerColumn serverTimeColumn;

    private boolean reconciliation;

    private MDMServerDef curServerDef;

    private boolean isServerOk;

    /**
     * if type==null, return all type
     *
     * @param parent
     * @param type
     * @param commands
     */
    public RepositoryViewObjectCheckedWidget(Composite parent, ERepositoryObjectType type, List<AbstractDeployCommand> commands) {
        super(parent, SWT.NONE);
        adapter = ExAdapterManager.getAdapter(this, IRepositoryViewObjectCheckedWidgetExAdapter.class);
        initChangedViewObjects(commands);
        input = initInput(type);
        initWidget();

    }

    public void addCheckStateListener(ICheckStateListener listener) {
        treeViewer.addCheckStateListener(listener);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public MDMServerDef getSameServerDef() {
        return this.lastServerDef;
    }

    public void updateCurrentServerDef(Shell shell, final MDMServerDef curServerDef) {
        doCheckServerConnection(shell, curServerDef);

        new UIJob("Switch Server") { //$NON-NLS-1$

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                RepositoryViewObjectCheckedWidget.this.curServerDef = curServerDef;
                consistencyMap.clear();
                treeViewer.refresh();
                selectObjects(new InitDeployCheckHandler());
                return Status.OK_STATUS;
            }
        }.schedule();

    }

    private void doCheckServerConnection(Shell shell, MDMServerDef serverDef) {
        isServerOk = RepositoryResourceUtil.checkServerConnection(shell, serverDef);
    }

    public boolean isServerOk() {
        return isServerOk;
    }

    public List<AbstractDeployCommand> getSelectedCommands() {
        List<AbstractDeployCommand> commands = new LinkedList<AbstractDeployCommand>();
        CommandManager commandManager = CommandManager.getInstance();
        for (Object obj : treeViewer.getCheckedElements()) {
            if (obj instanceof FolderRepositoryObject) {
                continue;
            }
            IRepositoryViewObject viewObject = (IRepositoryViewObject) obj;
            String id = viewObject.getId();
            AbstractDeployCommand e = cmdMap.get(id);
            if (e == null) {
                List<IRepositoryViewObject> objs = new ArrayList<IRepositoryViewObject>();
                objs.add(viewObject);
                List<AbstractDeployCommand> newCommands = commandManager.getDeployCommands(objs, ICommand.CMD_MODIFY);
                if (!newCommands.isEmpty()) {
                    e = newCommands.get(0);
                }
            }
            commands.add(e);
            List<AbstractDeployCommand> associatedCommands = getAssociatedObjectCommand(viewObject, ICommand.CMD_MODIFY);
            if (associatedCommands != null) {
                commands.addAll(associatedCommands);
            }
        }
        return commands;
    }

    private List<AbstractDeployCommand> getAssociatedObjectCommand(IRepositoryViewObject viewObj, int cmdType) {
        ERepositoryObjectType type = viewObj.getRepositoryObjectType();

        CommandManager commandManager = CommandManager.getInstance();
        if (type != null) {
            IInteractiveHandler handler = InteractiveService.findHandler(type);
            if (handler != null) {
                List<IRepositoryViewObject> associatedObjects = handler.getAssociatedObjects(viewObj);
                if (associatedObjects != null) {
                    return commandManager.getDeployCommands(associatedObjects, cmdType);
                }
            }
        }
        return null;
    }

    public List<IRepositoryViewObject> getSelectededViewObjs() {
        List<IRepositoryViewObject> selectededViewObjs = new LinkedList<IRepositoryViewObject>();
        for (Object obj : treeViewer.getCheckedElements()) {
            if (obj instanceof FolderRepositoryObject) {
                continue;
            }
            selectededViewObjs.add((IRepositoryViewObject) obj);
        }
        return selectededViewObjs;
    }

    public Tree getTree() {
        return treeViewer.getTree();
    }

    private void initChangedViewObjects(List<AbstractDeployCommand> commands) {
        if (commands != null) {
            cmdMap = new HashMap<String, AbstractDeployCommand>();
            for (AbstractDeployCommand cmd : commands) {
                if (cmd.getViewObject() != null) {
                    cmdMap.put(cmd.getViewObject().getId(), cmd);
                }
            }

        }
    }

    /**
     * DOC hbhong Comment method "initInput".
     *
     * @param type
     * @return
     */
    private IRepositoryViewObject[] initInput(ERepositoryObjectType type) {
        if (type != null) {
            IRepositoryViewObject[] elements = RepositoryResourceUtil.getCategoryViewObjects(type);
            List<IRepositoryViewObject> objs = new ArrayList<IRepositoryViewObject>();
            objs.addAll(Arrays.asList(elements));
            // missing transformer2, routingrule
            if (type.getKey().equals(IServerObjectRepositoryType.TYPE_TRANSFORMERV2.getKey())
                    || type.getKey().equals(IServerObjectRepositoryType.TYPE_ROUTINGRULE.getKey())) {
                IRepositoryNodeConfiguration typeConf = RepositoryNodeConfigurationManager.getConfiguration(type);
                addCategoryViewObject(objs, typeConf);
            }
            if (type.getKey().equals(IServerObjectRepositoryType.TYPE_EVENTMANAGER.getKey())) {
                IRepositoryNodeConfiguration processConf = RepositoryNodeConfigurationManager
                        .getConfiguration(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
                IRepositoryNodeConfiguration triggerConf = RepositoryNodeConfigurationManager
                        .getConfiguration(IServerObjectRepositoryType.TYPE_ROUTINGRULE);
                addCategoryViewObject(objs, processConf);
                addCategoryViewObject(objs, triggerConf);
            }
            return objs.toArray(new IRepositoryViewObject[0]);
        } else {
            IRepositoryViewObject[] elements = RepositoryResourceUtil.getCategoryViewObjects();
            List<IRepositoryViewObject> objs = new ArrayList<IRepositoryViewObject>();

            for (IRepositoryViewObject folderObj : elements) {
                ERepositoryObjectType objType = folderObj.getRepositoryObjectType();
                if (objType == IServerObjectRepositoryType.TYPE_EVENTMANAGER) {
                    IRepositoryNodeConfiguration processConf = RepositoryNodeConfigurationManager
                            .getConfiguration(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
                    IRepositoryNodeConfiguration triggerConf = RepositoryNodeConfigurationManager
                            .getConfiguration(IServerObjectRepositoryType.TYPE_ROUTINGRULE);

                    addCategoryViewObject(objs, processConf);
                    addCategoryViewObject(objs, triggerConf);
                } else if (objType != IServerObjectRepositoryType.TYPE_MATCH_RULE) {
                    objs.add(folderObj);
                }
            }
            Collections.sort(objs, new Comparator() {

                public int compare(Object o1, Object o2) {
                    String name1 = ((IRepositoryViewObject) o1).getRepositoryObjectType().getLabel();
                    String name2 = ((IRepositoryViewObject) o2).getRepositoryObjectType().getLabel();
                    return name1.compareTo(name2);
                }

            });
            return objs.toArray(new IRepositoryViewObject[0]);
        }
    }

    public void enableReconciliation(boolean enable) {
        this.reconciliation = enable;
        treeViewer.refresh();
        treeViewer.expandAll();
        selectObjects(new InitDeployCheckHandler());

    }

    private void addCategoryViewObject(List<IRepositoryViewObject> result, IRepositoryNodeConfiguration conf) {
        if (conf != null) {
            IRepositoryViewObject categoryViewObject = RepositoryResourceUtil.getCategoryViewObject(conf);
            if (categoryViewObject != null) {
                result.add(categoryViewObject);
            }
        }
    }

    private void initWidget() {
        //
        setLayout(new GridLayout());

        treeViewer = new ContainerCheckedTreeViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
        Tree tree = getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        installColumns();
        treeViewer.setContentProvider(new ContentProvider());

        treeViewer.getTree().addListener(SWT.PaintItem, new Listener() {

            public void handleEvent(Event event) {
                TreeItem item = (TreeItem) event.item;
                Object data = item.getData();
                if (data != null && lockedViewObjs.contains(data)) {
                    String text = item.getText(event.index);
                    Point point = event.gc.textExtent(text);
                    int y = event.y + point.y / 2 + 2;
                    int x = event.x + 20;
                    event.gc.drawLine(x, y, x + point.x, y);
                    event.gc.drawText(Messages.RepositoryViewObjectCheckedWidget_lock, x + point.x + 2, event.y + 2);
                }
            }

        });

        treeViewer.addCheckStateListener(new ICheckStateListener() {

            private void checkSubElement(IRepositoryViewObject viewObj) {
                if (viewObj instanceof FolderRepositoryObject) {
                    Item item = viewObj.getProperty().getItem();
                    List<IRepositoryViewObject> children = RepositoryResourceUtil.findViewObjects(
                            viewObj.getRepositoryObjectType(), item, true, true);
                    if (children != null) {
                        for (IRepositoryViewObject child : children) {
                            checkSubElement(child);
                        }
                    }
                } else {
                    if (lockedViewObjs.contains(viewObj)) {
                        treeViewer.setChecked(viewObj, false);
                    }
                }
            }

            public void checkStateChanged(CheckStateChangedEvent event) {
                Object element = event.getElement();
                if (element instanceof IRepositoryViewObject) {
                    checkSubElement((IRepositoryViewObject) element);
                }
            }
        });
        if (adapter != null) {
            treeViewer.addCheckStateListener(new ICheckStateListener() {

                public void checkStateChanged(CheckStateChangedEvent event) {
                    updateSeverityCount();
                }
            });
        }

        treeViewer.addFilter(new ViewerFilter() {

            private boolean containVisibleElement(FolderRepositoryObject parent) {
                Item item = parent.getProperty().getItem();
                List<IRepositoryViewObject> children = RepositoryResourceUtil.findViewObjects(parent.getRepositoryObjectType(),
                        item, true, true);
                boolean result = false;
                for (IRepositoryViewObject viewObj : children) {

                    if (viewObj instanceof FolderRepositoryObject) {
                        boolean r = containVisibleElement((FolderRepositoryObject) viewObj);
                        if (r) {
                            result = true;
                        }
                    } else if (viewObj instanceof IRepositoryViewObject) {
                        if (isVisibleViewObj(viewObj)) {
                            updateServerDef(viewObj);
                            // updateLockedObject(viewObj);
                            result = true;
                        }
                    }
                }
                return result;
            }

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (reconciliation) {
                    return true;
                } else {
                    if (element instanceof FolderRepositoryObject) {
                        return containVisibleElement((FolderRepositoryObject) element);
                    } else {
                        IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
                        return isVisibleViewObj(viewObj);
                    }
                }
            }
        });
        // show time
        final Button showTimestampBun = new Button(this, SWT.CHECK);
        showTimestampBun.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showTimeStampColumns(showTimestampBun.getSelection());
            }
        });
        showTimestampBun.setText(Messages.ConsistencyConflict_showTimeStampColumn);
        //
        treeViewer.setInput(input);
        selectAll(true);
        treeViewer.expandAll();
        if (adapter != null) {
            adapter.initSeverityControl(this);
        }
        updateSeverityCount();
    }

    private void updateSeverityCount() {
        if (adapter != null) {
            adapter.updateSeverityCount(getSelectededViewObjs());
        }
    }

    private boolean isVisibleViewObj(IRepositoryViewObject viewObj) {
        AbstractDeployCommand cmd = cmdMap.get(viewObj.getId());
        if (cmd != null) {
            ERepositoryObjectType type = viewObj.getRepositoryObjectType();
            if (type == ERepositoryObjectType.PROCESS) {
                int commandType = cmd.getCommandType();
                return commandType == ICommand.CMD_MODIFY || commandType == ICommand.CMD_DELETE;
            } else {
                return true;
            }
        }

        return false;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss"); //$NON-NLS-1$

    public void skipDeployedObjects() {
        selectObjects(new SkipDeployedCheckHandler());
    }

    public void selectObjects(CheckHandler checkHandler) {
        for (IRepositoryViewObject viewObj : consistencyMap.keySet()) {
            ConsistencyData consistencyData = consistencyMap.get(viewObj);
            if (consistencyData != null) {
                CompareResultEnum compareResult = consistencyData.getCompareResult();
                checkHandler.handle(compareResult, viewObj);
            }
        }
    }

    interface CheckHandler {

        void handle(CompareResultEnum compareResult, IRepositoryViewObject viewObj);
    }

    class InitDeployCheckHandler implements CheckHandler {

        public void handle(CompareResultEnum compareResult, IRepositoryViewObject viewObj) {

            if (compareResult == CompareResultEnum.NOT_EXIST_IN_SERVER) {
                treeViewer.setChecked(viewObj, true);
            } else {
                treeViewer.setChecked(viewObj, isVisibleViewObj(viewObj));
            }
        }

    }

    class SkipDeployedCheckHandler implements CheckHandler {

        public void handle(CompareResultEnum compareResult, IRepositoryViewObject viewObj) {

            if (compareResult == CompareResultEnum.SAME || compareResult == CompareResultEnum.CONFLICT
                    || compareResult == CompareResultEnum.MODIFIED_LOCALLY
                    || compareResult == CompareResultEnum.POTENTIAL_CONFLICT) {
                treeViewer.setChecked(viewObj, false);
            }

        }

    }

    class TimeStampColumnProvider extends ColumnLabelProvider {

        private final boolean isLocal;

        public TimeStampColumnProvider(boolean isLocal) {
            this.isLocal = isLocal;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IRepositoryViewObject && curServerDef != null && !(element instanceof FolderRepositoryObject)) {
                ConsistencyData consistencyData = getConsistencyData(curServerDef, (IRepositoryViewObject) element);
                if (consistencyData != null) {
                    WSDigest dt = isLocal ? consistencyData.getLocalDigestTime() : consistencyData.getServerDigestTime();
                    if (dt != null && dt.getTimeStamp() > 0) {
                        return DATE_FORMAT.format(new Date(dt.getTimeStamp()));
                    }
                }
            }
            return ""; //$NON-NLS-1$
        }

    }

    class CompareResultColumnProvider extends ColumnLabelProvider {

        @Override
        public String getText(Object element) {
            if (element instanceof IRepositoryViewObject && curServerDef != null && !(element instanceof FolderRepositoryObject)) {
                ConsistencyData consistencyData = getConsistencyData(curServerDef, (IRepositoryViewObject) element);
                if (consistencyData != null) {
                    CompareResultEnum compareResult = consistencyData.getCompareResult();
                    if (compareResult != null) {
                        switch (compareResult) {
                        case NOT_EXIST_IN_SERVER:
                            return Messages.ConsistencyConflictt_NotDeployed;
                        case SAME:
                        case CONFLICT:
                        case POTENTIAL_CONFLICT:
                        case MODIFIED_LOCALLY:
                            return Messages.ConsistencyConflictt_Deployed;
                        case NOT_SUPPORT:
                            return Messages.ConsistencyConflict_undefined;
                        }
                    }
                }
            }
            return ""; //$NON-NLS-1$
        }

    }

    private void installColumns() {
        treeViewer.getTree().setHeaderVisible(true);
        //
        TreeViewerColumn nameColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        nameColumn.getColumn().setWidth(320);
        nameColumn.getColumn().setText(Messages.ConsistencyConflict_name);
        ILabelDecorator labelDecorator = RepositoryPlugin.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator();
        DecoratingStyledCellLabelProvider consistencyLabelProvider = new DecoratingStyledCellLabelProvider(
                new MDMRepositoryLabelProvider(), labelDecorator, null);
        nameColumn.setLabelProvider(consistencyLabelProvider);
        // compare result
        TreeViewerColumn resultColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        resultColumn.getColumn().setWidth(120);
        resultColumn.getColumn().setText(Messages.ConsistencyConflict_compareResult);
        resultColumn.setLabelProvider(new CompareResultColumnProvider());
        //
        localTimeColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        localTimeColumn.getColumn().setText(Messages.ConsistencyConflict_retrievalTimestamp);
        localTimeColumn.setLabelProvider(new TimeStampColumnProvider(true));
        //
        serverTimeColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        serverTimeColumn.getColumn().setText(Messages.ConsistencyConflict_serverTimestamp);
        serverTimeColumn.setLabelProvider(new TimeStampColumnProvider(false));
        //
        showTimeStampColumns(false);
    }

    protected void showTimeStampColumns(boolean selection) {
        int width = selection ? 120 : 0;
        localTimeColumn.getColumn().setWidth(width);
        serverTimeColumn.getColumn().setWidth(width);
        localTimeColumn.getColumn().setResizable(selection);
        serverTimeColumn.getColumn().setResizable(selection);
        treeViewer.refresh();
    }

    Map<IRepositoryViewObject, ConsistencyData> consistencyMap = new HashMap<IRepositoryViewObject, ConsistencyData>();

    private synchronized ConsistencyData getConsistencyData(MDMServerDef serverDef, IRepositoryViewObject viewObj) {
        if (!isServerOk) {
            return null;
        }
        ConsistencyData consistencyData = consistencyMap.get(viewObj);
        if (consistencyData != null) {
            return consistencyData;
        }
        try {
            ConsistencyService consistencyService = ConsistencyService.getInstance();

            consistencyData = new ConsistencyData();

            ERepositoryObjectType viewType = viewObj.getRepositoryObjectType();
            if (viewType == null) {
                return null;
            }
            if (viewType == IServerObjectRepositoryType.TYPE_RESOURCE || viewType == IServerObjectRepositoryType.TYPE_JOB
                    || viewType == IServerObjectRepositoryType.TYPE_WORKFLOW) {

                consistencyData.setCompareResult(CompareResultEnum.NOT_SUPPORT);
            } else {
                List<IRepositoryViewObject> viewObjs = new ArrayList<IRepositoryViewObject>();
                viewObjs.add(viewObj);
                Map<IRepositoryViewObject, WSDigest> digestValueResult = consistencyService.queryServerDigestValue(serverDef,
                        viewObjs);
                WSDigest serverDigestTime = digestValueResult.get(viewObj);
                consistencyData.setServerDigestTime(serverDigestTime);
                consistencyService.updateCurrentDigestValue(viewObj);
                Item item = viewObj.getProperty().getItem();
                String ld = consistencyService.getLocalDigestValue(item);
                String cd = consistencyService.getCurrentDigestValue(item);
                long localTimestamp = consistencyService.getLocalTimestamp(item);
                // key
                String type = viewObj.getRepositoryObjectType().getKey();
                String objectName = viewObj.getLabel();
                WSDigestKey key = new WSDigestKey(objectName, type);
                consistencyData.setLocalDigestTime(new WSDigest(ld, localTimestamp, key));

                // init compare result;
                CompareResultEnum result;
                if (serverDigestTime == null || serverDigestTime.getDigestValue() == null) {
                    result = CompareResultEnum.NOT_EXIST_IN_SERVER;
                } else {
                    String rd = serverDigestTime.getDigestValue();
                    result = consistencyService.getCompareResult(cd, ld, rd);
                }
                consistencyData.setCompareResult(result);

            }
            consistencyMap.put(viewObj, consistencyData);
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return consistencyData;
    }

    public void selectAll(boolean isAll) {

        for (IRepositoryViewObject viewObj : input) {
            treeViewer.setSubtreeChecked(viewObj, isAll);
        }
        for (IRepositoryViewObject lockObj : lockedViewObjs) {
            treeViewer.setChecked(lockObj, false);
        }
        updateSeverityCount();
    }

    private void updateServerDef(IRepositoryViewObject viewObj) {
        if (hasSameServerDef) {
            Item item = viewObj.getProperty().getItem();
            if (item == null || item instanceof ContainerItem) {
                return;
            }
            MDMServerDef serverDef = RepositoryResourceUtil.getLastServerDef(item);
            if (lastServerDef == null) {
                lastServerDef = serverDef;
            } else {
                if (!lastServerDef.equals(serverDef)) {
                    lastServerDef = null;
                    hasSameServerDef = false;
                }
            }
        }
    }

    private Set<IRepositoryViewObject> lockedViewObjs = new HashSet<IRepositoryViewObject>();

    //
    // private TreeColumn tc3;
    //
    // private TreeColumn tc4;

    private void updateLockedObject(IRepositoryViewObject viewObject) {
        if (viewObject instanceof FolderRepositoryObject) {
            return;
        }
        if (RepositoryResourceUtil.isLockedViewObject(viewObject)) {
            lockedViewObjs.add(viewObject);
        }
    }
}
