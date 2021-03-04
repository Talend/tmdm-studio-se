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
package org.talend.mdm.repository.ui.wizards.imports.viewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.service.ConsistencyService;
import org.talend.mdm.repository.core.service.ConsistencyService.CompareResultEnum;
import org.talend.mdm.repository.core.service.ConsistencyService.ConsistencyData;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.ui.widgets.AbstractNodeCheckTreeViewer;
import org.talend.mdm.repository.utils.EclipseResourceManager;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.ServerTreeContentProvider;
import com.amalto.workbench.providers.ServerTreeLabelProvider;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSDigest;
import com.amalto.workbench.webservices.WSDigestKey;
import com.amalto.workbench.widgets.FilteredCheckboxTree;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class TreeObjectCheckTreeViewer extends AbstractNodeCheckTreeViewer {

    class TreeObjectContentProvider extends ServerTreeContentProvider {

        /**
         * DOC hbhong TreeObjectContentProvider constructor comment.
         *
         * @param site
         * @param invisibleRoot
         */
        public TreeObjectContentProvider(TreeParent invisibleRoot) {
            super(null, invisibleRoot);
        }

        @Override
        public Object[] getElements(Object parent) {

            return getChildren(parent);
        }

    }

    private ServerTreeContentProvider contentProvider;

    Collection<TreeObject> repositoryNodes = new ArrayList<TreeObject>();

    private boolean isOverWrite = true;

    private Map<TreeObject, ConsistencyData> consistencyMap;

    private ITreeObjectCheckTreeViewerExAdapter exAdapter;

    /**
     * DOC hbhong TreeObjectCheckTreeViewer constructor comment.
     *
     * @param serverDef
     *
     * @param serverRoot
     */
    public TreeObjectCheckTreeViewer(TreeParent serverRoot) {
        super(serverRoot);
        exAdapter = ExAdapterManager.getAdapter(this, ITreeObjectCheckTreeViewerExAdapter.class);
    }

    public void initInput(MDMServerDef serverDef) {
        List<TreeObject> treeObjs = initTreeObjs(serverRoot);
        this.consistencyMap = initConsistencyData(serverDef, treeObjs);
        getViewer().setInput(serverRoot);
    }

    private List<TreeObject> initTreeObjs(TreeObject serverRoot) {
        List<TreeObject> treeObjs = new ArrayList<TreeObject>();
        iterateAllTreeObjs(serverRoot, treeObjs);
        return treeObjs;
    }

    private void iterateAllTreeObjs(TreeObject parent, List<TreeObject> treeObjs) {
        if (parent != null) {
            if (parent instanceof TreeParent) {
                for (TreeObject child : ((TreeParent) parent).getChildren()) {
                    iterateAllTreeObjs(child, treeObjs);
                }
            } else {
                treeObjs.add(parent);
            }
        }
    }

    private static final Color COLOR_LIGHT_RED = EclipseResourceManager.getColor(255, 210, 210);

    private static final Color COLOR_LIGHT_GREEN = EclipseResourceManager.getColor(223, 255, 186);

    class CompareResultColumnProvider extends ColumnLabelProvider {

        @Override
        public Color getBackground(Object element) {
            if (!(element instanceof TreeParent)) {
                ConsistencyData consistencyData = consistencyMap.get(element);
                if (consistencyData != null) {
                    CompareResultEnum compareResult = consistencyData.getCompareResult();
                    if (compareResult == CompareResultEnum.CONFLICT || compareResult == CompareResultEnum.POTENTIAL_CONFLICT) {
                        return COLOR_LIGHT_RED;
                    } else if (compareResult == CompareResultEnum.MODIFIED_LOCALLY) {
                        return COLOR_LIGHT_GREEN;
                    }

                }
            }
            return null;
        }

        @Override
        public String getText(Object element) {
            if (!(element instanceof TreeParent)) {
                ConsistencyData consistencyData = consistencyMap.get(element);
                if (consistencyData != null) {
                    CompareResultEnum compareResult = consistencyData.getCompareResult();
                    switch (compareResult) {
                    case NOT_EXIST_IN_SERVER:
                        return Messages.ConsistencyConflict_noDataInServer;
                    case NOT_EXIST_IN_LOCAL:
                        return Messages.ConsistencyConflict_notExistInLocal;
                    case SAME:
                        return Messages.ConsistencyConflict_Same;
                    case CONFLICT:
                        return Messages.ConsistencyConflict_Conflict;
                    case POTENTIAL_CONFLICT:
                        return Messages.ConsistencyConflict_potentialConflict;
                    case MODIFIED_LOCALLY:
                        return Messages.ConsistencyConflict_modifiedLocally;
                    case NOT_SUPPORT:
                        return Messages.ConsistencyConflict_undefined;
                    }
                }
            }
            return ""; //$NON-NLS-1$
        }

    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss"); //$NON-NLS-1$

    class TimeStampColumnProvider extends ColumnLabelProvider {

        private final boolean isLocal;

        public TimeStampColumnProvider(boolean isLocal) {
            this.isLocal = isLocal;
        }

        @Override
        public String getText(Object element) {
            if (!(element instanceof TreeParent)) {
                ConsistencyData consistencyData = consistencyMap.get(element);
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

    private String getTreeObjectName(TreeObject treeObject) {
        if (treeObject != null) {
            int type = treeObject.getType();
            if (type == TreeObject.WORKFLOW_PROCESS) {
                if (exAdapter != null) {
                    return exAdapter.getWorkflowgTreeObjectName(treeObject);
                }

            }
            return treeObject.getDisplayName();
        }
        return null;
    }

    private Map<TreeObject, ConsistencyData> initConsistencyData(MDMServerDef serverDef, List<TreeObject> treeObjs) {
        Map<TreeObject, ConsistencyData> map = new HashMap<TreeObject, ConsistencyData>();
        try {
            ConsistencyService consistencyService = ConsistencyService.getInstance();
            Map<TreeObject, WSDigest> serverDigestValues = consistencyService.queryServerDigestValue(serverDef, treeObjs);
            if (treeObjs.size() > 0 && serverDigestValues.isEmpty()) {
                return map;
            }
            for (TreeObject treeObject : treeObjs) {

                ConsistencyData consistencyData = new ConsistencyData();
                WSDigest serverDigestTime = serverDigestValues.get(treeObject);
                consistencyData.setServerDigestTime(serverDigestTime);
                String objName = getTreeObjectName(treeObject);
                ERepositoryObjectType viewType = RepositoryQueryService.getRepositoryObjectType(treeObject.getType());
                if (viewType == null) {
                    continue;
                }
                if (viewType == IServerObjectRepositoryType.TYPE_RESOURCE || viewType == IServerObjectRepositoryType.TYPE_JOB
                        || viewType == IServerObjectRepositoryType.TYPE_WORKFLOW) {

                    consistencyData.setCompareResult(CompareResultEnum.NOT_SUPPORT);
                } else {
                    IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectByName(viewType, objName);
                    if (viewObj == null) {
                        consistencyData.setCompareResult(CompareResultEnum.NOT_EXIST_IN_LOCAL);
                    } else {
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
                }
                map.put(treeObject, consistencyData);
            }
        } catch (WebServiceException e) {
            log.error(e.getMessage(), e);
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }
        return map;
    }

    private TreeViewerColumn localTimeColumn;

    private TreeViewerColumn serverTimeColumn;

    @Override
    protected void createTreeViewer(Composite itemComposite) {
        filteredCheckboxTree = new FilteredCheckboxTree(itemComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION) {

            ContainerCheckedTreeViewer treeViewer;

            @Override
            protected CheckboxTreeViewer doCreateTreeViewer(Composite parent, int style) {
                treeViewer = new ContainerCheckedTreeViewer(parent, style);
                contentProvider = new TreeObjectContentProvider(serverRoot);
                treeViewer.setContentProvider(contentProvider);

                installColumns();
                treeViewer.setInput(serverRoot);

                return treeViewer;
            }

            private void installColumns() {
                treeViewer.getTree().setHeaderVisible(true);
                //
                TreeViewerColumn nameColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
                nameColumn.getColumn().setWidth(320);
                nameColumn.getColumn().setText(Messages.ConsistencyConflict_name);
                nameColumn.setLabelProvider(new ServerTreeLabelProvider());
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

            @Override
            protected boolean isNodeCollectable(TreeItem item) {
                return false;
            }

            @Override
            protected void refreshCompleted() {
                treeViewer.expandToLevel(3);
                restoreCheckedElements();
            }
        };
        filteredCheckboxTree.getViewer().addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                TreeObject node = (TreeObject) element;
                return filterRepositoryNode(node, isOverWrite);
            }
        });
    }

    @Override
    protected Composite createSelectionButton(Composite itemComposite) {
        Composite container = super.createSelectionButton(itemComposite);
        Button collapseBtn = new Button(container, SWT.PUSH);
        collapseBtn.setText(Messages.ConsistencyConflict_skipSame);
        collapseBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                skipSameDigestValueObjects();
            }
        });
        return container;
    }

    protected void skipSameDigestValueObjects() {
        for (TreeObject treeObj : consistencyMap.keySet()) {
            ConsistencyData consistencyData = consistencyMap.get(treeObj);
            if (consistencyData != null) {
                if (consistencyData.getCompareResult() == CompareResultEnum.SAME) {
                    filteredCheckboxTree.getViewer().setChecked(treeObj, false);
                }
            }
        }
    }

    public void showTimeStampColumns(boolean selection) {
        int width = selection ? 120 : 0;
        localTimeColumn.getColumn().setWidth(width);
        serverTimeColumn.getColumn().setWidth(width);
        localTimeColumn.getColumn().setResizable(selection);
        serverTimeColumn.getColumn().setResizable(selection);
    }

    @Override
    protected void filterCheckedObjects(Object[] selected, List<Object> ret) {

        for (Object element : selected) {
            ret.add(element);
        }
    }

    @Override
    public void refresh() {
        if (serverRoot != null) {
            repositoryNodes.addAll(Util.getChildrenObj(serverRoot));

            ((CheckboxTreeViewer) viewer).setCheckedElements(repositoryNodes.toArray());
        }
    }

    public void setRoot(TreeParent root) {
        setServerRoot(root);
        contentProvider.setRoot(root);
    }

    public boolean isOverWrite() {
        return this.isOverWrite;
    }

    public void setOverWrite(boolean isOverWrite) {
        this.isOverWrite = isOverWrite;
    }
}
