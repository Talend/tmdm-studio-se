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
package org.talend.mdm.repository.core.validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.marker.ValidateMarkerUtil;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.core.validate.impl.AfterSavingValidationPreference;
import org.talend.mdm.repository.core.validate.impl.BeforeDeployingValidationPreference;
import org.talend.mdm.repository.core.validate.impl.ImmediateValidationPreference;

/**
 * created by HHB on 2013-1-24 Detailled comment
 * 
 */
public class MDMValidationService implements IModelValidationService {

    static Logger log = Logger.getLogger(MDMValidationService.class);

    private IValidationPreference immediatePref = new ImmediateValidationPreference();

    private IValidationPreference afterSavingPref = new AfterSavingValidationPreference();

    private IValidationPreference beforeDeployingPref = new BeforeDeployingValidationPreference();

    public static class ModelValidateResult implements IModelValidateResult {

        private static final int OK = -1;

        private static final int WARNING = IMarker.SEVERITY_WARNING;

        private static final int ERROR = IMarker.SEVERITY_ERROR;

        private int selectedButton;

        private Map<Integer, List<IRepositoryViewObject>> severityMap = new HashMap<Integer, List<IRepositoryViewObject>>();

        public ModelValidateResult(Map<IRepositoryViewObject, IFile> viewObjMap) {
            initSeverityMap();
            collectChectResult(viewObjMap);
            filterCheckResult();
            this.selectedButton = IDialogConstants.CANCEL_ID;
        }

        /**
         * Sets the selectedButton.
         * 
         * @param selectedButton the selectedButton to set
         */
        @Override
        public void setSelectedButton(int selectedButton) {
            this.selectedButton = selectedButton;
        }

        public ModelValidateResult() {
            this.selectedButton = IDialogConstants.CANCEL_ID;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.mdm.repository.core.service.IModelValidationService.IModelValidateResult#hasErrOrWarning()
         */
        @Override
        public boolean hasErrOrWarning() {
            return severityMap.get(ERROR).size() > 0 || severityMap.get(WARNING).size() > 0;
        }

        @Override
        public int getSelectedButton() {
            return selectedButton;
        }

        private void initSeverityMap() {
            severityMap.put(OK, new LinkedList<IRepositoryViewObject>());
            severityMap.put(ERROR, new LinkedList<IRepositoryViewObject>());
            severityMap.put(WARNING, new LinkedList<IRepositoryViewObject>());
        }

        private void collectChectResult(Map<IRepositoryViewObject, IFile> viewObjMap) {
            for (IRepositoryViewObject viewObj : viewObjMap.keySet()) {
                IFile file = viewObjMap.get(viewObj);
                if (file == null) {
                    severityMap.get(OK).add(viewObj);
                } else {
                    String[] markerTypes = ValidateMarkerUtil.getMarkerTypeByViewType(viewObj.getRepositoryObjectType());
                    if (markerTypes != null) {
                        try {
                            for (String markerType : markerTypes) {
                                int severity = file.findMaxProblemSeverity(markerType, false, IResource.DEPTH_ZERO);

                                List<IRepositoryViewObject> objs = severityMap.get(severity);
                                if (objs != null && !objs.contains(viewObj)) {
                                    objs.add(viewObj);

                                }
                            }
                        } catch (CoreException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }

        private void filterCheckResult() {
            List<IRepositoryViewObject> okObjs = severityMap.get(OK);
            List<IRepositoryViewObject> errorObjs = severityMap.get(ERROR);
            List<IRepositoryViewObject> warningObjs = severityMap.get(WARNING);
            for (IRepositoryViewObject obj : errorObjs) {
                if (warningObjs.contains(obj)) {
                    warningObjs.remove(obj);
                }
                if (okObjs.contains(obj)) {
                    okObjs.remove(obj);
                }
            }

            for (IRepositoryViewObject obj : warningObjs) {
                if (okObjs.contains(obj)) {
                    okObjs.remove(obj);
                }
            }

        }

        @Override
        public List<IRepositoryViewObject> getValidObjects(int selectedButtons) {

            switch (selectedButtons) {
            case BUTTON_OK:
            case BUTTON_SKIP_ERROR_WARNING:
                return severityMap.get(OK);
            case BUTTON_SKIP_ERROR:
                List<IRepositoryViewObject> objs = new LinkedList<IRepositoryViewObject>();
                objs.addAll(severityMap.get(OK));
                objs.addAll(severityMap.get(WARNING));
                return objs;
            case BUTTON_CANCEL:
                return Collections.emptyList();
            }
            ;

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.mdm.repository.core.service.IModelValidationService.IModelValidateResult#getInValidObjects(int)
         */
        @Override
        public List<IRepositoryViewObject> getInvalidObjects(int selectedButtons) {
            switch (selectedButtons) {
            case BUTTON_OK:
            case BUTTON_SKIP_ERROR_WARNING:
                List<IRepositoryViewObject> objs = new LinkedList<IRepositoryViewObject>();
                objs.addAll(severityMap.get(ERROR));
                objs.addAll(severityMap.get(WARNING));
                return objs;
            case BUTTON_SKIP_ERROR:
                return severityMap.get(ERROR);
            case BUTTON_CANCEL:
                List<IRepositoryViewObject> all = new LinkedList<IRepositoryViewObject>();
                all.addAll(severityMap.get(OK));
                all.addAll(severityMap.get(ERROR));
                all.addAll(severityMap.get(WARNING));
                return all;
            }
            return null;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.repository.core.service.IValidationService#validate(java.util.List, int)
     */
    @Override
    public IModelValidateResult validate(List<IRepositoryViewObject> viewObjs, int condition, boolean forbidShowResultDialog) {
        if (viewObjs != null && viewObjs.size() > 0) {
            switch (condition) {
            case VALIDATE_IMMEDIATE:
                return MDMValidationRunner.validate(viewObjs, immediatePref, forbidShowResultDialog);
            case VALIDATE_AFTER_SAVE:
                return MDMValidationRunner.validate(viewObjs, afterSavingPref, forbidShowResultDialog);
            case VALIDATE_BEFORE_DEPLOY:
                return MDMValidationRunner.validate(viewObjs, beforeDeployingPref, forbidShowResultDialog);
            default:
                break;
            }
        }
        return null;
    }

}
