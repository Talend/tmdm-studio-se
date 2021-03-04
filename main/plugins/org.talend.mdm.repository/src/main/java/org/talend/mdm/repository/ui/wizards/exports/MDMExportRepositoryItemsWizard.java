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
package org.talend.mdm.repository.ui.wizards.exports;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.ui.wizards.exports.viewers.ExportRepObjCheckTreeViewer;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.local.ExportItemUtil;

import com.amalto.workbench.export.ExportItemsWizard;

/**
 * DOC hywang class global comment. this wizard is used to export the selected items from MDMRepositoryView
 */
public class MDMExportRepositoryItemsWizard extends ExportItemsWizard {

    ExportRepObjCheckTreeViewer checkTreeViewer;

    private static Log log = LogFactory.getLog(MDMExportRepositoryItemsWizard.class);

    public MDMExportRepositoryItemsWizard(IStructuredSelection sel) {
        super(sel);
    }

    // create a check box viewer
    @Override
    protected void createViewer() {
        checkTreeViewer = new ExportRepObjCheckTreeViewer(sel);
    }

    // do export is need to override ,use the system of TOS to export EMF files
    @Override
    public void doexport(Object[] selectedNodes, IProgressMonitor monitor) {
        List<IRepositoryViewObject> objs = new LinkedList<IRepositoryViewObject>();
        for (Object obj : selectedNodes) {
            if (obj instanceof IRepositoryViewObject) {
                objs.add((IRepositoryViewObject) obj);
            }
        }
        if (exportFolder != null) {

            Collection<Item> selectedItems = getItemsToExport(objs);
            try {
                ExportItemUtil exportItemUtil = new ExportItemUtil();
                // MOD sgandon 31/03/2010 bug 12229: moved getAllVersion into ExportItemUtil.exportitems() method.
                exportItemUtil.exportItems(new File(exportFolder), selectedItems, true, monitor);

            } catch (Exception e) {
                MessageBoxExceptionHandler.process(e);
            }
        }
    }

    @Override
    protected Object[] getCheckedObjects() {
        return checkTreeViewer.getCheckNodes();
    }

    /**
     * DOC hywang Comment method "getItemsToExport".
     *
     */
    private Collection<Item> getItemsToExport(Collection<IRepositoryViewObject> objs) {
        List<Item> toReturn = null;
        if (objs != null && objs.size() > 0) {
            toReturn = new ArrayList<Item>();
            for (IRepositoryViewObject obj : objs) {
                Item item = obj.getProperty().getItem();
                if (item != null && !(item instanceof ContainerItem)) {
                    item = RepositoryResourceUtil.assertItem(item);
                    toReturn.add(item);
                }
            }
        }
        return toReturn;
    }

    @Override
    protected Composite initItemTreeViewer(Composite composite) {
        Composite returnComposite = checkTreeViewer.createItemList(composite);
        checkTreeViewer.setItemText(Messages.MDMExportRepositoryItemsWizard_exportItem);

        initCheckState();
        return returnComposite;
    }

    private void initCheckState() {
        CheckboxTreeViewer tv = (CheckboxTreeViewer) checkTreeViewer.getViewer();
        tv.expandAll();

        List<IRepositoryViewObject> leafItems = new ArrayList<IRepositoryViewObject>();
        getCheckedViewObjects(sel, leafItems, new ArrayList<IRepositoryViewObject>());

        recursiveExpand(tv.getTree().getItems(), leafItems);
        ((CheckboxTreeViewer) checkTreeViewer.getViewer()).setCheckedElements(leafItems.toArray());
        checkTreeViewer.updateCountStatus();
    }

    private boolean recursiveExpand(TreeItem[] items, List<IRepositoryViewObject> leafItems) {
        boolean expanda = false;
        for (TreeItem item : items) {
            IRepositoryViewObject viewObj = (IRepositoryViewObject) item.getData();

            if (leafItems.contains(viewObj)) {
                expanda = true;
                break;
            }

            if (viewObj instanceof FolderRepositoryObject) {
                boolean expand = recursiveExpand(item.getItems(), leafItems);
                item.setExpanded(expand);
                if (expand) {
                    expanda = true;
                }
            }
        }

        return expanda;
    }

    private void getCheckedViewObjects(IStructuredSelection sel, List<IRepositoryViewObject> leafItems,
            List<IRepositoryViewObject> noneLeafItems) {

        Map<String, List<IRepositoryViewObject>> viewObjTypeMap = new TreeMap<String, List<IRepositoryViewObject>>();
        Map<String, List<String>> pathMap = new HashMap<String, List<String>>();
        Map<String, ERepositoryObjectType> types = new HashMap<String, ERepositoryObjectType>();

        List<IRepositoryViewObject> seList = sel.toList();
        for (IRepositoryViewObject iRepositoryViewObject : seList) {
            IRepositoryViewObject viewObj = iRepositoryViewObject;
            ERepositoryObjectType repositoryObjectType = viewObj.getRepositoryObjectType();
            if (repositoryObjectType != null) {
                List<IRepositoryViewObject> list = viewObjTypeMap.get(repositoryObjectType.name());
                List<String> typePaths = pathMap.get(repositoryObjectType.name());
                if (list == null) {
                    list = new LinkedList<IRepositoryViewObject>();
                    viewObjTypeMap.put(repositoryObjectType.name(), list);

                    typePaths = new LinkedList<String>();
                    pathMap.put(repositoryObjectType.name(), typePaths);

                    types.put(repositoryObjectType.name(), repositoryObjectType);
                }
                list.add(viewObj);
                typePaths.add(viewObj.getPath());
            }
        }

        List<IRepositoryViewObject> childs = new LinkedList<IRepositoryViewObject>();

        for (String etype : types.keySet()) {
            List<IRepositoryViewObject> viewObjectsWithDeleted = getAllViewObjectByType(types.get(etype));
            for (IRepositoryViewObject vObject : viewObjectsWithDeleted) {
                List<String> pathList = pathMap.get(etype);
                for (int i = 0; i < pathList.size(); i++) {
                    if (vObject.getPath().equals(pathList.get(i))) {
                        IRepositoryViewObject vo = viewObjTypeMap.get(etype).get(i);
                        if (vo instanceof FolderRepositoryObject && !(vObject instanceof FolderRepositoryObject)) {
                            childs.add(vObject);
                            break;
                        }
                    } else if (vObject.getPath().contains(pathList.get(i))
                            && viewObjTypeMap.get(etype).get(i) instanceof FolderRepositoryObject) {
                        childs.add(vObject);
                        break;
                    }

                }
            }

        }

        childs.addAll(seList);

        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i) instanceof FolderRepositoryObject) {
                noneLeafItems.add(childs.get(i));
            } else {
                leafItems.add(childs.get(i));
            }
        }
    }

    private List<IRepositoryViewObject> getAllViewObjectByType(ERepositoryObjectType eType) {
        List<IRepositoryViewObject> viewObjectsWithDeleted = null;

        if (eType == IServerObjectRepositoryType.TYPE_EVENTMANAGER) {

            viewObjectsWithDeleted = new LinkedList<IRepositoryViewObject>();
            List<IRepositoryViewObject> aViewObjects = RepositoryResourceUtil
                    .findAllViewObjects(IServerObjectRepositoryType.TYPE_TRANSFORMERV2);
            List<IRepositoryViewObject> bViewObjects = RepositoryResourceUtil
                    .findAllViewObjects(IServerObjectRepositoryType.TYPE_ROUTINGRULE);

            viewObjectsWithDeleted.addAll(aViewObjects);
            viewObjectsWithDeleted.addAll(bViewObjects);

        } else {

            viewObjectsWithDeleted = RepositoryResourceUtil.findAllViewObjectsWithDeleted(eType);
        }

        return viewObjectsWithDeleted;
    }

}
