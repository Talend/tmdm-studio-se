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

import org.eclipse.xsd.XSDCompositor;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.complextype.ComplexTypeWrapper;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;

public class ComplexTypeWrapperCommitHandler extends CompositeCommitHandler<ComplexTypeWrapper> {

    public ComplexTypeWrapperCommitHandler(ComplexTypeWrapper submittable) {
        super(submittable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CommitHandler<ComplexTypeWrapper>[] createChildHandlers() {
        return new CommitHandler[] { new ComplexTypeNameCommitHandler(getCommitedObj()),
                new ComplexTypeGroupTypeCommitHandler(getCommitedObj()), new ComplexTypeExtendsCommitHandler(getCommitedObj()),
                new ComplexTypeAbstractCommitHandler(getCommitedObj()) };
    }

}

class ComplexTypeNameCommitHandler extends CommitHandler<ComplexTypeWrapper> {

    public ComplexTypeNameCommitHandler(ComplexTypeWrapper submittedObj) {
        super(submittedObj);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        if (getCommitedObj().getNewTypeName() == null || "".equals(getCommitedObj().getNewTypeName().trim()))//$NON-NLS-1$
            throw new CommitValidationException(Messages.ComplexTypeXXHandler_CannotbeEmpty);

        if (getCommitedObj().getNewTypeName().replaceAll("\\s", "").length() != getCommitedObj().getNewTypeName().length())//$NON-NLS-1$//$NON-NLS-2$
            throw new CommitValidationException(Messages.ComplexTypeXXHandler_CannotContainEmpty);

    }

    @Override
    protected boolean doSubmit() throws CommitException {
        return getCommitedObj().changeTypeName();
    }

}

class ComplexTypeAbstractCommitHandler extends CommitHandler<ComplexTypeWrapper> {

    public ComplexTypeAbstractCommitHandler(ComplexTypeWrapper submittedObj) {
        super(submittedObj);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {
    }

    @Override
    protected boolean doSubmit() throws CommitException {
        return getCommitedObj().changeAbstract();
    }
}

class ComplexTypeExtendsCommitHandler extends CommitHandler<ComplexTypeWrapper> {

    public ComplexTypeExtendsCommitHandler(ComplexTypeWrapper submittedObj) {
        super(submittedObj);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        if (getCommitedObj().getNewExtends() != null
                && getCommitedObj().getNewExtends().equals(getCommitedObj().getCurComplexType()))
            throw new CommitValidationException(Messages.ComplexTypeXXHandler_CBaseTypeCannotbeItself);

        if (Util.getParentTypes(getCommitedObj().getNewExtends()).contains(getCommitedObj().getCurComplexType()))
            throw new CommitValidationException(Messages.bind(Messages.ComplexTypeXXHandler_ValidExceptionInfo, getCommitedObj()
                    .getNewExtends().getName(), getCommitedObj().getCurComplexType().getName()));

        if (!getCommitedObj().isDefaultExtends()) {
            XSDCompositor newBaseGroupType = Util.getComplexTypeGroupType(getCommitedObj().getNewExtends());
            XSDCompositor newGroupType = getCommitedObj().getNewGroupType();

            if (newBaseGroupType != null && newBaseGroupType.equals(XSDCompositor.ALL_LITERAL))
                throw new CommitValidationException(Messages.bind(Messages.ComplexTypeXXHandler_ValidExceptionInfo1,
                        getCommitedObj().getNewExtends().getName()));

            if (newGroupType != null && newGroupType.equals(XSDCompositor.ALL_LITERAL))
                throw new CommitValidationException(Messages.ComplexTypeXXHandler_ValidExceptionInfo2);

            if (newBaseGroupType != null && !newBaseGroupType.equals(newGroupType))
                throw new CommitValidationException(Messages.bind(Messages.ComplexTypeXXHandler_ValidExceptionInfo3,
                        getCommitedObj().getNewExtends().getName()));
        }
    }

    @Override
    protected boolean doSubmit() throws CommitException {
        return getCommitedObj().changeExtends();
    }

}

class ComplexTypeGroupTypeCommitHandler extends CommitHandler<ComplexTypeWrapper> {

    public ComplexTypeGroupTypeCommitHandler(ComplexTypeWrapper submittedObj) {
        super(submittedObj);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        if (getCommitedObj().getNewGroupType() == null)
            throw new CommitValidationException(Messages.ComplexTypeXXHandler_ValidExceptionInfo4);

    }

    @Override
    protected boolean doSubmit() throws CommitException {
        return getCommitedObj().changeGroupType();
    }

}
