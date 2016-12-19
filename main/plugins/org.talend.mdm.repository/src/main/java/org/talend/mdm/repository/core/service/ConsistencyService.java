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
package org.talend.mdm.repository.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.ReferenceFileItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesPackage;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.dialogs.consistency.ConfirmConflictMessageDialog;
import org.talend.mdm.repository.ui.dialogs.consistency.ConsistencyConflictDialog;
import org.talend.mdm.repository.ui.preferences.PreferenceConstants;
import org.talend.mdm.repository.ui.wizards.imports.IConsistencyServiceExAdapter;
import org.talend.mdm.repository.utils.DigestUtil;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.UIUtil;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSDigest;
import org.talend.mdm.webservice.WSDigestKey;
import org.talend.mdm.webservice.WSLong;

import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.XtentisException;

/**
 * created by HHB on 2013-7-18 Detailled comment
 * 
 */
public class ConsistencyService {

    public static final int CONFLICT_STRATEGY_DEFAULT = 0;

    public static final int CONFLICT_STRATEGY_OVERWRITE = 1;

    public static final int CONFLICT_STRATEGY_SKIP_DIFFERENCE = 2;

    public enum CompareResultEnum {
        NOT_EXIST_IN_SERVER,
        NOT_EXIST_IN_LOCAL,
        CONFLICT,
        POTENTIAL_CONFLICT,
        SAME,
        MODIFIED_LOCALLY,
        NOT_SUPPORT
    }

    public static class ConsistencyData {

        private WSDigest localDigestTime;

        public WSDigest getLocalDigestTime() {
            return this.localDigestTime;
        }

        public void setLocalDigestTime(WSDigest localDigestTime) {
            this.localDigestTime = localDigestTime;
        }

        public WSDigest getServerDigestTime() {
            return this.serverDigestTime;
        }

        public void setServerDigestTime(WSDigest serverDigestTime) {
            this.serverDigestTime = serverDigestTime;
        }

        public CompareResultEnum getCompareResult() {
            return this.compareResult;
        }

        public void setCompareResult(CompareResultEnum compareResult) {
            this.compareResult = compareResult;
        }

        private WSDigest serverDigestTime;

        private CompareResultEnum compareResult;

    }

    public static class ConsistencyCheckResult {

        private boolean isCanceled;

        private List<IRepositoryViewObject> toDeployObjects;

        private List<IRepositoryViewObject> toSkipObjects;

        /**
         * user cancel the operation
         */
        public ConsistencyCheckResult() {
            isCanceled = true;
        }

        public ConsistencyCheckResult(Collection<IRepositoryViewObject> viewObjs) {
            this(new LinkedList<IRepositoryViewObject>(viewObjs), Collections.EMPTY_LIST);
        }

        public ConsistencyCheckResult(List<IRepositoryViewObject> deployedObjects, List<IRepositoryViewObject> skippededObjects) {
            this.toDeployObjects = deployedObjects;
            this.toSkipObjects = skippededObjects;
            isCanceled = false;
        }

        public List<IRepositoryViewObject> getToDeployObjects() {
            return this.toDeployObjects;
        }

        public List<IRepositoryViewObject> getToSkipObjects() {
            return this.toSkipObjects;
        }

        public boolean isCanceled() {
            return this.isCanceled;
        }
    }

    enum DigestCalStrategyEnum {
        // compare the object 's resource file
        OBJ_RESOURCE,
        // only compare the reference file
        REF_FILE,
        // same with OBJ_RESOURCE except the property is sorted
        SORTED_OBJ_RESOURCE

    }

    public class SortEMFCopier extends EcoreUtil.Copier {

        @Override
        protected void copyAttribute(EAttribute eAttribute, EObject eObject, EObject copyEObject) {

            if (eObject.eIsSet(eAttribute)) {
                if (FeatureMapUtil.isFeatureMap(eAttribute)) {
                    FeatureMap featureMap = (FeatureMap) eObject.eGet(eAttribute);
                    for (int i = 0, size = featureMap.size(); i < size; ++i) {
                        EStructuralFeature feature = featureMap.getEStructuralFeature(i);
                        if (feature instanceof EReference && ((EReference) feature).isContainment()) {
                            Object value = featureMap.getValue(i);
                            if (value != null) {
                                copy((EObject) value);
                            }
                        }
                    }
                } else if (eAttribute.isMany()) {
                    List<?> source = (List<?>) eObject.eGet(eAttribute);

                    @SuppressWarnings("unchecked")
                    List<Object> target = (List<Object>) copyEObject.eGet(getTarget(eAttribute));
                    if (source.isEmpty()) {
                        target.clear();
                    } else {
                        Object[] array = source.toArray();
                        Arrays.sort(array);
                        for (Object object : array) {
                            target.add(object);
                        }

                    }
                } else {
                    copyEObject.eSet(getTarget(eAttribute), eObject.eGet(eAttribute));
                }
            }

        }

        public <T extends EObject> T duplicate(T eObject) {
            SortEMFCopier copier = new SortEMFCopier();
            EObject result = copier.copy(eObject);
            copier.copyReferences();

            @SuppressWarnings("unchecked")
            T t = (T) result;
            return t;
        }
    }

    private static final String DIGEST_VALUE = "digestValue"; //$NON-NLS-1$

    private static final String CURRENT_DIGEST_VALUE = "currentDigestValue"; //$NON-NLS-1$

    private static ConsistencyService instance = new ConsistencyService();

    static Logger log = Logger.getLogger(ConsistencyService.class);

    private static final String TIMESTAMP = "timeStamp"; //$NON-NLS-1$

    private IConsistencyServiceExAdapter exAdapter;

    public static ConsistencyService getInstance() {
        return instance;
    }

    private ConsistencyService() {
        exAdapter = ExAdapterManager.getAdapter(this, IConsistencyServiceExAdapter.class);
    }

    private String calculateDigestValueByEMFResource(Resource resource) {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            resource.save(os, Collections.EMPTY_MAP);
            String digestValue = DigestUtil.encodeByMD5(os.toByteArray());
            return digestValue;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    private String calculateDigestValueByObjectResource(Item item) {
        EObject copy = null;
        if (item instanceof ProcessItem) {
            copy = EcoreUtil.copy(((ProcessItem) item).getProcess());
        } else if (item instanceof MDMServerObjectItem) {
            MDMServerObject mdmServerObject = ((MDMServerObjectItem) item).getMDMServerObject();
            copy = EcoreUtil.copy(mdmServerObject);
            // remove server def property
            MDMServerObject copiedMdmObj = (MDMServerObject) copy;
            copiedMdmObj.setLastServerDef(null);
            copiedMdmObj.setLastServerName(null);
            copiedMdmObj.setCurrentDigestValue(null);
            copiedMdmObj.setDigestValue(null);
            // restore the timestamp to default
            copiedMdmObj.setTimestamp(0L);

        } else {
            // unsupport to caculate object md5 which not belong to MDM
            throw new UnsupportedOperationException();
        }
        if (copy != null) {
            //
            Resource resource = new SortResourceImpl();
            resource.getContents().add(copy);
            return calculateDigestValueByEMFResource(resource);
        }
        return null;
    }

    class SortResourceImpl extends XMIResourceImpl {

        @Override
        protected XMLSave createXMLSave() {

            return new SortXMLSave(createXMLHelper());
        }

    }

    class SortXMLSave extends XMLSaveImpl {

        public SortXMLSave(XMLHelper helper) {
            super(helper);
        }

        @Override
        protected void saveContainedMany(EObject o, EStructuralFeature f) {
            List<InternalEObject> values = (List<InternalEObject>) ((InternalEList<? extends InternalEObject>) helper.getValue(o,
                    f)).basicList();
            List<InternalEObject> copyList = new ArrayList<InternalEObject>();
            copyList.addAll(values);
            int size = copyList.size();
            Collections.sort(copyList, new Comparator<InternalEObject>() {

                public int compare(InternalEObject o1, InternalEObject o2) {
                    String[] values1 = getValues(o1);
                    String[] values2 = getValues(o2);
                    for (int i = 0; i < values1.length; i++) {
                        String v1 = values1[i];
                        String v2 = values2[i];
                        if (v1 != null && v2 != null) {
                            int result = v1.compareTo(v2);
                            if (result == 0) {
                                continue;
                            }
                            return result;
                        } else if (v1 != null && v2 == null) {
                            return 1;
                        } else if (v1 == null && v2 != null) {
                            return -1;
                        }
                    }
                    return 0;

                }

            });
            for (int i = 0; i < size; i++) {
                InternalEObject value = copyList.get(i);
                if (value != null) {
                    saveElement(value, f);
                }
            }
        }

        private String[] getValues(InternalEObject o) {
            EList<EAttribute> attributes = o.eClass().getEAllAttributes();
            String[] values = new String[attributes.size()];
            int i = 0;
            for (EAttribute attribute : attributes) {
                Object valueO = o.eGet(attribute);
                if (valueO != null) {
                    values[i] = valueO.toString();
                }
                i++;
            }
            return values;
        }

    }

    private String calculateDigestValueByRefFile(Item item, ERepositoryObjectType type) {

        IFile file = getReferenceFile(item, type);
        if (file != null && file.exists()) {
            InputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = file.getContents();
                out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                //
                String digestValue = DigestUtil.encodeByMD5(out.toByteArray());
                return digestValue;
            } catch (CoreException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }

        }
        return null;
    }

    private String calculateDigestValueBySortedObject(Item item, ERepositoryObjectType type) {
        if (item.eClass().getClassifierID() == MdmpropertiesPackage.WS_WORKFLOW_ITEM) {

            IFile file = getReferenceFile(item, type);
            if (file != null && file.exists()) {
                try {
                    URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
                    Resource rawResource = new XMIResourceImpl(uri);
                    rawResource.load(Collections.EMPTY_MAP);
                    Resource resource = new XMIResourceImpl();
                    SortEMFCopier copier = new SortEMFCopier();

                    for (EObject next : rawResource.getContents()) {
                        EObject copy = copier.duplicate(next);
                        resource.getContents().add(copy);
                    }
                    return calculateDigestValueByEMFResource(resource);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }

            }
        }
        return null;
    }

    public ConsistencyCheckResult checkConsistency(MDMServerDef serverDef, Collection<IRepositoryViewObject> viewObjs)
            throws XtentisException {
        Map<IRepositoryViewObject, Integer> viewObCmdOpjMap = new HashMap<IRepositoryViewObject, Integer>();
        for (IRepositoryViewObject viewObj : viewObjs) {
            viewObCmdOpjMap.put(viewObj, ICommand.CMD_MODIFY);
        }
        return checkConsistency(serverDef, viewObCmdOpjMap);
    }

    public ConsistencyCheckResult checkConsistency(MDMServerDef serverDef, Map<IRepositoryViewObject, Integer> viewObCmdOpjMap)
            throws XtentisException {
        Collection<IRepositoryViewObject> viewObjs = viewObCmdOpjMap.keySet();
        if (UIUtil.isWorkInUI()) {
            updateCurrentlDigestValue(viewObjs);
            Map<IRepositoryViewObject, WSDigest> viewObjMap = queryServerDigestValue(serverDef, viewObjs);
            int conflictCount = getConflictCount(viewObjMap);
            if (conflictCount > 0) {
                ConsistencyCheckResult result = null;
                if (isWarnUserWhenConflict()) {
                    ConfirmConflictMessageDialog confirmDialog = new ConfirmConflictMessageDialog(getShell(), conflictCount);
                    int returnValue = confirmDialog.open();
                    if (returnValue == IDialogConstants.OK_ID) {
                        int strategy = confirmDialog.getStrategy();
                        result = getCheckResultByStrategy(strategy, viewObjMap, viewObCmdOpjMap);
                    } else if (returnValue == IDialogConstants.DETAILS_ID) {
                        ConsistencyConflictDialog dialog = new ConsistencyConflictDialog(getShell(), conflictCount, viewObjMap,
                                viewObCmdOpjMap);
                        dialog.open();
                        result = dialog.getResult();
                    } else {
                        result = new ConsistencyCheckResult();
                    }
                } else {
                    int strategy = getConflictStrategy();
                    result = getCheckResultByStrategy(strategy, viewObjMap, viewObCmdOpjMap);
                }
                correctCheckResult(result);
                return result;

            }
        }
        return new ConsistencyCheckResult(viewObjs);

    }

    private void correctCheckResult(ConsistencyCheckResult result) {
        correctCheckResultForAssociatedObj(result.getToDeployObjects(), result.getToSkipObjects());
        correctCheckResultForAssociatedObj(result.getToSkipObjects(), result.getToDeployObjects());
    }

    private List<IRepositoryViewObject> getAssociatedObjects(IRepositoryViewObject viewObj) {
        ERepositoryObjectType type = viewObj.getRepositoryObjectType();
        if (type != null) {
            IInteractiveHandler handler = InteractiveService.findHandler(type);
            if (handler != null) {
                return handler.getAssociatedObjects(viewObj);
            }
        }
        return null;

    }

    private void correctCheckResultForAssociatedObj(List<IRepositoryViewObject> sourceObjs, List<IRepositoryViewObject> targetObjs) {
        if (sourceObjs != null && targetObjs != null) {
            for (IRepositoryViewObject viewObj : sourceObjs.toArray(new IRepositoryViewObject[0])) {
                List<IRepositoryViewObject> associatedObjects = getAssociatedObjects(viewObj);
                List correctedAssociatedObjs = getAssociatedObjects(associatedObjects, targetObjs);
                if (correctedAssociatedObjs != null && !correctedAssociatedObjs.isEmpty()) {
                    targetObjs.removeAll(correctedAssociatedObjs);
                    sourceObjs.addAll(correctedAssociatedObjs);
                }
            }
        }
    }

    private List<IRepositoryViewObject> getAssociatedObjects(List<IRepositoryViewObject> associatedObjects,
            List<IRepositoryViewObject> targetObjs) {
        if (associatedObjects == null) {
            return null;
        }
        List<IRepositoryViewObject> returnObjs = new LinkedList<IRepositoryViewObject>();
        for (IRepositoryViewObject viewObj : targetObjs) {
            for (IRepositoryViewObject associateObj : associatedObjects) {
                if (viewObj.getRepositoryObjectType() == associateObj.getRepositoryObjectType()
                        && viewObj.getLabel().equals(associateObj.getLabel())) {
                    returnObjs.add(viewObj);
                }
            }
        }
        return returnObjs;
    }

    private ConsistencyCheckResult getCheckResultByStrategy(int strategy, Map<IRepositoryViewObject, WSDigest> viewObjMap,
            Map<IRepositoryViewObject, Integer> viewObCmdOpjMap) {
        List<IRepositoryViewObject> toDeployObjs = new LinkedList<IRepositoryViewObject>();
        List<IRepositoryViewObject> toSkipObjs = new LinkedList<IRepositoryViewObject>();
        for (IRepositoryViewObject viewObj : viewObjMap.keySet()) {
            WSDigest dt = viewObjMap.get(viewObj);
            if (dt == null) {
                toDeployObjs.add(viewObj);
            } else {
                Item item = viewObj.getProperty().getItem();
                String ld = getLocalDigestValue(item);
                String rd = dt.getDigestValue();
                String cd = getCurrentDigestValue(item);
                CompareResultEnum result = getCompareResult(cd, ld, rd);
                // update isDeleteOp
                boolean isDeletedOp = false;
                Integer cmdOp = viewObCmdOpjMap.get(viewObj);
                if (cmdOp != null) {
                    isDeletedOp = cmdOp == ICommand.CMD_DELETE;
                }
                //
                if (result == CompareResultEnum.SAME) {
                    if (strategy == CONFLICT_STRATEGY_DEFAULT || strategy == CONFLICT_STRATEGY_SKIP_DIFFERENCE) {
                        if (isDeletedOp) {
                            toDeployObjs.add(viewObj);
                        } else {
                            toSkipObjs.add(viewObj);
                        }

                    } else if (strategy == CONFLICT_STRATEGY_OVERWRITE) {
                        toDeployObjs.add(viewObj);
                    }
                } else if (result == CompareResultEnum.CONFLICT || result == CompareResultEnum.MODIFIED_LOCALLY
                        || result == CompareResultEnum.POTENTIAL_CONFLICT) {
                    if (strategy == CONFLICT_STRATEGY_SKIP_DIFFERENCE) {
                        toSkipObjs.add(viewObj);
                    } else if (strategy == CONFLICT_STRATEGY_DEFAULT || strategy == CONFLICT_STRATEGY_OVERWRITE) {
                        toDeployObjs.add(viewObj);
                    }
                }
            }
        }
        return new ConsistencyCheckResult(toDeployObjs, toSkipObjs);
    }

    /**
     * DOC HHB Comment method "getCompareResult".
     * 
     * @param cd current digest value
     * @param ld local digest value
     * @param rd remote digest value
     * @return
     */
    public CompareResultEnum getCompareResult(String cd, String ld, String rd) {
        if (rd == null) {
            return CompareResultEnum.NOT_EXIST_IN_SERVER;
        }
        if (ld != null) {
            if (!ld.equals(rd)) {
                return CompareResultEnum.CONFLICT;
            } else {
                if (cd != null && cd.equals(ld)) {
                    return CompareResultEnum.SAME;
                } else {
                    return CompareResultEnum.MODIFIED_LOCALLY;
                }
            }
        } else {
            if (cd != null && !cd.equals(rd)) {
                return CompareResultEnum.POTENTIAL_CONFLICT;
            } else {
                return CompareResultEnum.SAME;
            }
        }
    }

    public String calculateDigestValue(Item item, ERepositoryObjectType type) {
        DigestCalStrategyEnum strategy = getDigetValueCaculateStrategy(item);
        switch (strategy) {
        case OBJ_RESOURCE:
            return calculateDigestValueByObjectResource(item);
        case REF_FILE:
            return calculateDigestValueByRefFile(item, type);
        case SORTED_OBJ_RESOURCE:
            return calculateDigestValueBySortedObject(item, type);

        }
        return null;
    }

    private DigestCalStrategyEnum getDigetValueCaculateStrategy(Item item) {
        if (item instanceof ProcessItem) {
            return DigestCalStrategyEnum.OBJ_RESOURCE;
        }
        if (item instanceof MDMServerObjectItem) {
            int id = item.eClass().getClassifierID();
            switch (id) {
            case MdmpropertiesPackage.WS_CUSTOM_FORM_ITEM:
            case MdmpropertiesPackage.WS_DATA_MODEL_ITEM:
            case MdmpropertiesPackage.WS_RESOURCE_ITEM:

                return DigestCalStrategyEnum.REF_FILE;
            case MdmpropertiesPackage.WS_WORKFLOW_ITEM:
                return DigestCalStrategyEnum.SORTED_OBJ_RESOURCE;
            default:
                return DigestCalStrategyEnum.OBJ_RESOURCE;
            }
        }
        return DigestCalStrategyEnum.OBJ_RESOURCE;
    }

    public String getLocalDigestValue(Item item) {
        if (item == null) {
            return null;
        }
        if (item instanceof MDMServerObjectItem) {
            return ((MDMServerObjectItem) item).getMDMServerObject().getDigestValue();
        } else if (item instanceof ProcessItem) {
            return (String) item.getProperty().getAdditionalProperties().get(DIGEST_VALUE);
        }
        throw new UnsupportedOperationException();
    }

    public String getCurrentDigestValue(Item item) {
        if (item == null) {
            return null;
        }
        if (item instanceof MDMServerObjectItem) {
            return ((MDMServerObjectItem) item).getMDMServerObject().getCurrentDigestValue();
        } else if (item instanceof ProcessItem) {
            return (String) item.getProperty().getAdditionalProperties().get(CURRENT_DIGEST_VALUE);
        }
        throw new UnsupportedOperationException();
    }

    public long getLocalTimestamp(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (item instanceof MDMServerObjectItem) {
            return ((MDMServerObjectItem) item).getMDMServerObject().getTimestamp();
        } else if (item instanceof ProcessItem) {
            Long time = (Long) item.getProperty().getAdditionalProperties().get(TIMESTAMP);
            if (time != null) {
                return time;
            }
            return 0L;
        }
        throw new UnsupportedOperationException();
    }

    private IFile getReferenceFile(Item item, ERepositoryObjectType type) {
        item = RepositoryResourceUtil.assertItem(item);
        List refResources = item.getReferenceResources();
        if (refResources != null && !refResources.isEmpty()) {
            ReferenceFileItem fileItem = (ReferenceFileItem) refResources.get(0);
            ByteArray content = fileItem.getContent();
            if (content.eResource() != null) {
                URI uri = content.eResource().getURI();
                if (uri.isPlatformResource()) {
                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                    IPath path = new Path(uri.toPlatformString(true));
                    IFile file = root.getFile(path);
                    return file;
                }
            } else {
                return RepositoryResourceUtil.findReferenceFile(type, item, fileItem.getExtension());
            }
        }
        return null;
    }

    private Shell getShell() {
        return Display.getDefault().getActiveShell();
    }

    private int getConflictCount(Map<IRepositoryViewObject, WSDigest> map) {
        int total = 0;
        for (IRepositoryViewObject viewObj : map.keySet()) {
            WSDigest digestTime = map.get(viewObj);
            if (digestTime == null) {
                continue;
            }
            String rd = digestTime.getDigestValue();
            if (digestTime != null && rd != null) {
                Item item = viewObj.getProperty().getItem();
                String ld = getLocalDigestValue(item);
                String cd = getCurrentDigestValue(item);
                CompareResultEnum result = getCompareResult(cd, ld, rd);
                if (result == CompareResultEnum.CONFLICT || result == CompareResultEnum.POTENTIAL_CONFLICT) {
                    total++;
                }
            }
        }
        return total;
    }

    public void updateDigestValue(MDMServerDef serverDef, IRepositoryViewObject viewObj) throws XtentisException {
        TMDMService service = RepositoryWebServiceAdapter.getMDMService(serverDef);
        updateLocalDigestValue(viewObj);
        Item item = viewObj.getProperty().getItem();

        // key
        String type = viewObj.getRepositoryObjectType().getKey();
        String objectName = getObjectName(viewObj);
        WSDigestKey key = new WSDigestKey(objectName, type);
        // value
        WSDigest value = new WSDigest(getLocalDigestValue(item), 0L, key);
        WSLong timeValue = service.updateDigest(value);
        //
        if (timeValue != null) {
            updateLocalTimestamp(item, timeValue.getValue());
        }
        if (!viewObj.getRepositoryObjectType().equals(IServerObjectRepositoryType.TYPE_MATCH_RULE_MAPINFO)) {
            item = RepositoryResourceUtil.assertItem(item);
            Property property = item.getProperty();
            boolean eDeliver = property.eDeliver();
            property.eSetDeliver(false);
            RepositoryResourceUtil.saveItem(item);
            property.eSetDeliver(eDeliver);
        }
    }

    private String getObjectName(IRepositoryViewObject viewObj) {
        String objectName = viewObj.getLabel();

        if (exAdapter != null) {
            objectName = exAdapter.getObjectNameForDigest(viewObj);
        }

        return objectName;
    }

    private String getObjectName(TreeObject treeObj) {
        String objectName = treeObj.getDisplayName();

        if (exAdapter != null) {
            objectName = exAdapter.getObjectNameForDigest(treeObj);
        }

        return objectName;
    }

    public <T> Map<T, WSDigest> queryServerDigestValue(MDMServerDef serverDef, Collection<T> objs) throws XtentisException {
        Map<T, WSDigest> result = new LinkedHashMap<T, WSDigest>();
        TMDMService service = RepositoryWebServiceAdapter.getMDMService(serverDef);
        if (isSupportConsistency(service)) {
            for (T obj : objs) {
                String type = null;
                String objectName = null;
                if (obj instanceof IRepositoryViewObject) {
                    IRepositoryViewObject viewObj = (IRepositoryViewObject) obj;

                    type = viewObj.getRepositoryObjectType().getKey();
                    objectName = viewObj.getLabel();
                } else if (obj instanceof TreeObject) {
                    TreeObject treeObj = (TreeObject) obj;

                    ERepositoryObjectType repositoryObjectType = RepositoryQueryService
                            .getRepositoryObjectType(treeObj.getType());
                    if (repositoryObjectType != null) {
                        type = repositoryObjectType.getKey();
                        objectName = getObjectName(treeObj);
                    }
                }

                if (type != null && objectName != null) {
                    WSDigest digest = service.getDigest(new WSDigestKey(objectName, type));
                    result.put(obj, digest);
                }

            }
        }
        return result;
    }

    private void updateLocalDigestValue(Item item, String digestValue) {
        if (item instanceof MDMServerObjectItem) {
            ((MDMServerObjectItem) item).getMDMServerObject().setDigestValue(digestValue);
        } else if (item instanceof ProcessItem) {
            Property property = item.getProperty();
            boolean eDeliver = property.eDeliver();
            property.eSetDeliver(false);
            EMap additionalProperties = item.getProperty().getAdditionalProperties();
            additionalProperties.removeKey(CURRENT_DIGEST_VALUE);
            additionalProperties.put(DIGEST_VALUE, digestValue);
            property.eSetDeliver(eDeliver);
        }
    }

    private void updateCurrentDigestValue(Item item, String digestValue) {
        if (item instanceof MDMServerObjectItem) {
            ((MDMServerObjectItem) item).getMDMServerObject().setCurrentDigestValue(digestValue);
        } else if (item instanceof ProcessItem) {
            Property property = item.getProperty();
            boolean eDeliver = property.eDeliver();
            property.eSetDeliver(false);
            property.getAdditionalProperties().put(CURRENT_DIGEST_VALUE, digestValue);
            property.eSetDeliver(eDeliver);
        }
    }

    public void updateCurrentlDigestValue(Iterable<IRepositoryViewObject> it) {
        for (IRepositoryViewObject viewObj : it) {
            updateCurrentDigestValue(viewObj);
        }
    }

    /**
     * caculate and update local digest value
     * 
     * @param viewObj
     */
    public void updateLocalDigestValue(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        String digestValue = calculateDigestValue(item, viewObj.getRepositoryObjectType());
        updateLocalDigestValue(item, digestValue);
    }

    public void updateCurrentDigestValue(IRepositoryViewObject viewObj) {
        Item item = viewObj.getProperty().getItem();
        String digestValue = calculateDigestValue(item, viewObj.getRepositoryObjectType());
        updateCurrentDigestValue(item, digestValue);
    }

    private void updateLocalTimestamp(Item item, long timestamp) {
        if (item instanceof MDMServerObjectItem) {
            ((MDMServerObjectItem) item).getMDMServerObject().setTimestamp(timestamp);
        } else if (item instanceof ProcessItem) {
            Property property = item.getProperty();
            boolean eDeliver = property.eDeliver();
            property.eSetDeliver(false);
            property.getAdditionalProperties().put(TIMESTAMP, timestamp);
            property.eSetDeliver(eDeliver);
        }
    }

    private IPreferenceStore getPreferenceStore() {
        return RepositoryPlugin.getDefault().getPreferenceStore();
    }

    public void setWarnUserWhenConflict(boolean isWarn) {
        getPreferenceStore().setValue(PreferenceConstants.P_WARN_USER_HAS_CONFLICT, isWarn);
    }

    public boolean isWarnUserWhenConflict() {
        return getPreferenceStore().getBoolean(PreferenceConstants.P_WARN_USER_HAS_CONFLICT);
    }

    public void setConflictStrategy(int strategy) {
        getPreferenceStore().setValue(PreferenceConstants.P_CONFLICT_STRATEGY, strategy);
    }

    public int getConflictStrategy() {
        return getPreferenceStore().getInt(PreferenceConstants.P_CONFLICT_STRATEGY);
    }

    private boolean isSupportConsistency(TMDMService service) {
        // *** TMDM-8080, temp substituted start ***//
        // WSBoolean isXmlDB = service.isXmlDB();
        // return !isXmlDB.isTrue();
        return true;
        // *** TMDM-8080, temp substituted end ***//

    }
}
