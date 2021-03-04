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

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.AnnotaionInfo;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public abstract class AnnotationInfoCommitHandler<T extends AnnotaionInfo> extends CommitHandler<T> {

    public AnnotationInfoCommitHandler(T submittedObj) {
        super(submittedObj);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

    }

    @Override
    protected boolean doSubmit() throws CommitException {

        XSDAnnotationsStructure xsdAnnoStruct = getXSDAnnotationStruct();

        return doSubmit_Removal(xsdAnnoStruct) | doSubmit_Update(xsdAnnoStruct) | doSubmit_Add(xsdAnnoStruct);
    }

    protected XSDAnnotationsStructure getXSDAnnotationStruct() {
        return new XSDAnnotationsStructure(getCommitedObj().getSourceXSDComponent());
    }

    protected boolean doSubmit_Removal(XSDAnnotationsStructure xsdAnnoStruct) {

        Object[] needRemovedObjs = getNeedRemovedObjs();

        for (Object eachNeedRemovedObj : needRemovedObjs) {
            doRemoveEachObj(xsdAnnoStruct, eachNeedRemovedObj);
        }

        return (needRemovedObjs.length > 0);
    }

    protected boolean doSubmit_Update(XSDAnnotationsStructure xsdAnnoStruct) {

        Object[] needUpdatedObjs = getNeedUpdatedObjs();

        for (Object eachNeedUpdateedObject : needUpdatedObjs) {
            doUpdateEachObj(xsdAnnoStruct, eachNeedUpdateedObject);
        }

        return needUpdatedObjs.length > 0;
    }

    protected boolean doSubmit_Add(XSDAnnotationsStructure xsdAnnoStruct) {

        Object[] needAddedObjects = getNeedAddedObjs();

        for (Object eachNeedAddedObject : needAddedObjects) {
            doAddEachObj(xsdAnnoStruct, eachNeedAddedObject);
        }

        return needAddedObjects.length > 0;
    }

    protected Object[] getNeedRemovedObjs() {
        return new Object[0];
    }

    protected Object[] getNeedUpdatedObjs() {
        return new Object[0];
    }

    protected Object[] getNeedAddedObjs() {
        return new Object[0];
    }

    protected void doRemoveEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedRemovedObj) {

    }

    protected void doUpdateEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedUpdatedObj) {

    }

    protected void doAddEachObj(XSDAnnotationsStructure xsdAnnoStruct, Object eachNeedAddedObj) {

    }
}
