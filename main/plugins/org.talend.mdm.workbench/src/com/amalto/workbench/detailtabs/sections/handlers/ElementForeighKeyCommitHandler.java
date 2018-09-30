// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDTerm;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyAnnoInfo;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class ElementForeighKeyCommitHandler extends ListContentsCommitHandler<ForeignKeyAnnoInfo> {

    public ElementForeighKeyCommitHandler(ForeignKeyAnnoInfo foreignKeyInfo) {
        super(foreignKeyInfo);
    }

    @Override
    protected String getMsgHeader() {
        return Messages.ElementForeighKeyCommitHandler_MsgHeader;
    }

    @Override
    protected String[] getOriginalAnnoInfos() {
        return new String[] { getXSDAnnotationStruct().getForeignKey() };
    }

    @Override
    protected void validateCommit() throws CommitValidationException {

    }

    @Override
    protected boolean doSubmit() throws CommitException {
        XSDComponent sourceXSDComponent = this.submittedObj.getSourceXSDComponent();
        if (sourceXSDComponent != null && sourceXSDComponent instanceof XSDParticle) {
            XSDParticle particle = (XSDParticle) sourceXSDComponent;
            XSDParticleContent content = particle.getContent();
            XSDTerm term = particle.getTerm();
            if (content != null && term != null && content != term) {
                return false;
            }
        }
        XSDAnnotationsStructure xsdAnnoStruct = getXSDAnnotationStruct();
        String[] values = getCommitedObj().getValues();
        if (Util.getForeignKeys() != null && values != null) {
            if (xsdAnnoStruct.getForeignKey() != null) {
                Util.getForeignKeys().remove(Util.getConceptFromPath(xsdAnnoStruct.getForeignKey()));
            }
            Util.getForeignKeys().add(Util.getConceptFromPath(values[0]));
        }

        if (values.length > 1) {
            xsdAnnoStruct.setForeignKeyNotSep(Boolean.valueOf(values[1]));
        }
        boolean setForeignKey = xsdAnnoStruct.setForeignKey(values[0]);
        doUpdateFKAnnotationStructure(xsdAnnoStruct);

        return setForeignKey;
    }
}
