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

import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;
import com.amalto.workbench.i18n.Messages;

public class SimpleTypeBaseTypeCommitHandler extends CommitHandler<SimpleTypeWrapper> {

    public SimpleTypeBaseTypeCommitHandler(SimpleTypeWrapper submittedObj) {
        super(submittedObj);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        XSDSimpleTypeDefinition newBaseType = getCommitedObj().getSchema().resolveSimpleTypeDefinition(
                getCommitedObj().getSchema().getSchemaForSchemaNamespace(), getCommitedObj().getNewBaseTypeName());

        if (newBaseType == null)
            throw new CommitValidationException(Messages.bind(Messages.SimpleTypeBaseTypeCommitHandler_ExceptionInfo1, getCommitedObj().getNewBaseTypeName()));

        if (newBaseType.equals(getCommitedObj()))
            throw new CommitValidationException(Messages.SimpleTypeBaseTypeCommitHandler_ExceptionInfo2);
    }

    @Override
    protected boolean doSubmit() throws CommitException {

        if (getCommitedObj().getXSDSimpleType().getBaseType().getName().equals(getCommitedObj().getNewBaseTypeName()))
            return false;

        XSDSimpleTypeDefinition newBaseType = getCommitedObj().getNewBaseType();
        if (!getCommitedObj().isBaseTypeExists()) {
            newBaseType = getCommitedObj().getSchema().resolveSimpleTypeDefinition(getCommitedObj().getNewBaseTypeName());
            newBaseType.setBaseTypeDefinition(getCommitedObj().getNewBaseType());
            getCommitedObj().getSchema().getContents().add(newBaseType);
        }

        getCommitedObj().getXSDSimpleType().setBaseTypeDefinition(newBaseType);
        getCommitedObj().getXSDSimpleType().getFacetContents().removeAll(getCommitedObj().getXSDSimpleType().getFacetContents());
        getCommitedObj().getXSDSimpleType().updateElement();

        return true;
    }

}
