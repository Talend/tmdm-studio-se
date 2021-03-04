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
package com.amalto.workbench.detailtabs.sections.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.EntityCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.INameEditable;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.utils.inputvalidator.EditXSDEleDecNameValidator;

public class EntityWrapper implements ISubmittable, INameEditable {

    private XSDElementDeclaration sourceEntity;

    private EditXSDEleDecNameValidator nameValidator;

    private String name = "";//$NON-NLS-1$

    private List<KeyWrapper> keys = new ArrayList<KeyWrapper>();

    public EntityWrapper(XSDElementDeclaration sourceEntity) {
        this.sourceEntity = sourceEntity;
        init();
    }

    private void init() {

        if (sourceEntity != null) {
            name = sourceEntity.getName();

            for (XSDIdentityConstraintDefinition eachId : sourceEntity.getIdentityConstraintDefinitions()) {
                keys.add(new KeyWrapper(eachId));
            }

            nameValidator = new EditXSDEleDecNameValidator(sourceEntity.getSchema());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if (getName().equals(name))
            return;

        this.name = name;
    }

    public String validNewName(String newName) {

        if (getSourceEntityName().equals(newName))
            return null;

        return nameValidator.isValid(newName);
    }

    public KeyWrapper[] getKeys() {
        return keys.toArray(new KeyWrapper[0]);
    }

    public void addKey(KeyWrapper keyWrapper) {
        if (keyWrapper != null)
            keys.add(keyWrapper);
    }

    public void removeKey(KeyWrapper keyWrapper) {
        keys.remove(keyWrapper);
    }

    public boolean hasKey() {
        return !keys.isEmpty();
    }

    public XSDElementDeclaration getSourceEntity() {
        return sourceEntity;
    }

    public XSDSchema getSchema() {
        if (sourceEntity != null)
            return sourceEntity.getSchema();

        return null;
    }

    public XSDIdentityConstraintDefinition[] getIdentityConstraintDefinitions() {

        if (sourceEntity != null)
            return sourceEntity.getIdentityConstraintDefinitions().toArray(new XSDIdentityConstraintDefinition[0]);

        return new XSDIdentityConstraintDefinition[0];
    }

    public String getSourceEntityName() {
        if (sourceEntity != null)
            return sourceEntity.getName();

        return ""; //$NON-NLS-1$
    }

    public CommitHandler<EntityWrapper> createCommitHandler() {
        return new EntityCommitHandler(this);
    }
}
