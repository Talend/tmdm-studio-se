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
package com.amalto.workbench.detailtabs.sections.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;

import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.ListContentsAnnotationInfo;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public abstract class ListContentsCommitHandler<T extends ListContentsAnnotationInfo> extends AnnotationInfoCommitHandler<T> {

    public ListContentsCommitHandler(T submittedSimpleAnnoInfo) {
        super(submittedSimpleAnnoInfo);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        for (String eachSubmittedSimpleAnnoInfoValue : getCommitedObj().getInfos()) {
            if (eachSubmittedSimpleAnnoInfoValue == null || "".equals(eachSubmittedSimpleAnnoInfoValue.trim())) {//$NON-NLS-1$
                throw new CommitValidationException(Messages.bind(Messages.ListContentsCommitHandler_ExceptionInfo,
                        getMsgHeader()));
            }
        }

    }

    @Override
    protected XSDAnnotationsStructure getXSDAnnotationStruct() {
        return new XSDAnnotationsStructure(getCommitedObj().getSourceXSDComponent());
    }

    @Override
    protected String[] getNeedRemovedObjs() {

        List<String> results = new ArrayList<String>();
        List<String> curInfoList = Arrays.asList(getCommitedObj().getInfos());

        for (String eachOriginalInfo : getOriginalAnnoInfos()) {

            if (curInfoList.contains(eachOriginalInfo)) {
                continue;
            }

            results.add(eachOriginalInfo);
        }

        return results.toArray(new String[0]);
    }

    @Override
    protected Object[] getNeedUpdatedObjs() {
        return new String[0];
    }

    @Override
    protected Object[] getNeedAddedObjs() {

        List<String> results = new ArrayList<String>();
        List<String> originalInfos = Arrays.asList(getOriginalAnnoInfos());

        for (String eachCurInfo : getCommitedObj().getInfos()) {

            if (originalInfos.contains(eachCurInfo)) {
                continue;
            }

            results.add(eachCurInfo);
        }

        return results.toArray(new String[0]);
    }

    @Override
    protected void doRemoveEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedRemovedObj) {
    }

    @Override
    protected void doUpdateEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedUpdatedObj) {
    }

    @Override
    protected void doAddEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedAddedObj) {
    }

    protected void doUpdateFKAnnotationStructure(XSDAnnotationsStructure xsdAnnoStruct) {
        if (xsdAnnoStruct == null) {
            throw new NullArgumentException(null);
        }

        String fk = xsdAnnoStruct.getForeignKey();
        if (fk == null || fk.trim().isEmpty()) {
            xsdAnnoStruct.setForeignKeyNotSep(null);
            xsdAnnoStruct.setFKFilter(null);
            xsdAnnoStruct.setForeignKeyInfos(new ArrayList());
            xsdAnnoStruct.setRetrieveFKinfos(null);
            xsdAnnoStruct.setFKIntegrity(null);
            xsdAnnoStruct.setFKIntegrityOverride(null);

            if (xsdAnnoStruct.getAnnotation().getApplicationInformation().isEmpty()
                    && xsdAnnoStruct.getAnnotation().getUserInformation().isEmpty()) {
                xsdAnnoStruct.getDeclaration().setAnnotation(null);
            }
        }
    }

    protected abstract String getMsgHeader();

    protected abstract String[] getOriginalAnnoInfos();

}
