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
package com.amalto.workbench.detailtabs.sections.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.actions.IMatchRuleMapInfoOperationExAdapter;
import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.entity.EntityWrapper;
import com.amalto.workbench.detailtabs.sections.model.entity.FieldWrapper;
import com.amalto.workbench.detailtabs.sections.model.entity.KeyWrapper;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.providers.datamodel.SchemaTreeContentProvider;
import com.amalto.workbench.utils.Util;

public class EntityCommitHandler extends CommitHandler<EntityWrapper> {

    private static final String ERR_ENTITY_NULLENTITYNAME = Messages.EntityCommitHandler_EntityNameCannotbeEmpty;

    private static final String ERR_ENTITY_CONTAINEMPTY = Messages.EntityCommitHandler_EntityNameCannotContainEmpty;

    private static final String ERR_ENTITY_DULPLICATEENTITYNAME = Messages.EntityCommitHandler_EntityNameExist;

    private static final String ERR_KEY_NULLKEYNAME = Messages.EntityCommitHandler_KeyNameCannotbeEmpty;

    private static final String ERR_KEY_CONTAINEMPTY = Messages.EntityCommitHandler_KeyNameCannotContainEmpty;

    private static final String ERR_KEY_DUPLICATEKEYNAME = Messages.EntityCommitHandler_KeyNameExist;

    private static final String ERR_KEY_NOFIELDS = Messages.EntityCommitHandler_MustAtLeastOnField;

    private static final String ERR_KEY_MULTIUNIQUE = Messages.EntityCommitHandler_AtMostUniqueKey;

    private static final String ERR_SELECTOR_WRONGFORMAT = Messages.EntityCommitHandler_KeySelectorInWrongFormat;

    private static final String ERR_FIELD_WRONGFORMAT = Messages.EntityCommitHandler_KeyFieldInWrongFormat;

    private IMatchRuleMapInfoOperationExAdapter exAdapter;

    public EntityCommitHandler(EntityWrapper entityWrapper) {
        super(entityWrapper);
        this.exAdapter = ExAdapterManager.getAdapter(this, IMatchRuleMapInfoOperationExAdapter.class);
    }

    @Override
    protected boolean doSubmit() throws CommitException {

        try {
            return (commitEntityName() | commitEnitityKeys());
        } catch (Exception e) {
            throw new CommitException(e.getMessage(), e);
        }

    }

    @Override
    protected void validateCommit() throws CommitValidationException {

        validateEntityName();

        validateEntityKeys();
    }

    private boolean commitEntityName() {

        XSDElementDeclaration xsdElementDeclaration = getCommitedObj().getSourceEntity();
        String oldName = xsdElementDeclaration.getName();
        String newName = getCommitedObj().getName();
        if (oldName.equals(newName)) {
            return false;
        }

        xsdElementDeclaration.setName(newName);
        xsdElementDeclaration.updateElement();

        updateReferenceForNewName(xsdElementDeclaration, oldName);

        // to correct unique's name
        for (Object idConsObj : xsdElementDeclaration.getIdentityConstraintDefinitions()) {
            XSDIdentityConstraintDefinition idCons = (XSDIdentityConstraintDefinition) idConsObj;
            if (idCons.getName().equals(oldName)) {
                idCons.setName(newName);
                idCons.updateElement();
                break;
            }
        }
        if (exAdapter != null) {
            exAdapter.renameEntityMapinfo(oldName, newName);
        }
        return true;
    }

    private void updateReferenceForNewName(XSDElementDeclaration xsdElementDeclaration, String oldName) {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IWorkbenchPartSite site = activePage.getActivePart().getSite();

        SchemaTreeContentProvider contentProvider = new SchemaTreeContentProvider(site, getCommitedObj().getSchema());
        Object[] objs = Util.getAllObject(site, new ArrayList<Object>(), contentProvider);
        Object[] allForeignKeyAndInfos = Util.getAllForeignKeyRelatedInfos(site, new ArrayList<Object>(), contentProvider,
                new HashSet<Object>());

        Util.updateReference(xsdElementDeclaration, objs, allForeignKeyAndInfos, oldName, getCommitedObj().getName());
    }

    private boolean commitEnitityKeys() {

        return commitKeyRemoval() | commitKeyUpdate() | commitKeyAddition();
    }

    private boolean commitKeyRemoval() {

        boolean hasChanges = false;

        for (XSDIdentityConstraintDefinition eachNeedRemovedKey : getNeedRemovedKeys()) {
            getCommitedObj().getSourceEntity().getIdentityConstraintDefinitions().remove(eachNeedRemovedKey);
            getCommitedObj().getSourceEntity().updateElement();

            hasChanges = true;
        }

        return hasChanges;
    }

    private boolean commitKeyUpdate() {

        boolean hasChanges = false;

        for (KeyWrapper eachKeyWrapper : getCommitedObj().getKeys()) {

            if (eachKeyWrapper.isUserCreated()) {
                continue;
            }

            hasChanges = hasChanges | commitKeyUpdate_Selector(eachKeyWrapper) | commitKeyUpdate_Fields(eachKeyWrapper);
        }

        return hasChanges;
    }

    private boolean commitKeyAddition() {

        boolean hasChanges = false;

        for (KeyWrapper eachKeyWrapper : getCommitedObj().getKeys()) {

            if (!eachKeyWrapper.isUserCreated()) {
                continue;
            }

            hasChanges = true;

            XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();

            XSDIdentityConstraintDefinition newKey = factory.createXSDIdentityConstraintDefinition();
            newKey.setName(eachKeyWrapper.getName());
            newKey.setIdentityConstraintCategory(eachKeyWrapper.getType());

            XSDXPathDefinition selector = factory.createXSDXPathDefinition();
            selector.setValue(eachKeyWrapper.getSelector());
            selector.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
            newKey.setSelector(selector);

            for (FieldWrapper newFields : eachKeyWrapper.getFields()) {
                XSDXPathDefinition field = factory.createXSDXPathDefinition();
                field.setValue(newFields.getXPath());
                field.setVariety(XSDXPathVariety.FIELD_LITERAL);
                newKey.getFields().add(field);
            }

            getCommitedObj().getSourceEntity().getIdentityConstraintDefinitions().add(newKey);
            getCommitedObj().getSourceEntity().updateElement();
        }

        return hasChanges;
    }

    private boolean commitKeyUpdate_Selector(KeyWrapper keyWrapper) {

        if (keyWrapper.getSourceKey().getSelector().getValue().equals(keyWrapper.getSelector())) {
            return false;
        }

        XSDXPathDefinition newXpath = XSDSchemaBuildingTools.getXSDFactory().createXSDXPathDefinition();
        newXpath.setValue(keyWrapper.getSelector());
        newXpath.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
        keyWrapper.getSourceKey().setSelector(newXpath);
        keyWrapper.getSourceKey().updateElement();

        return true;
    }

    private boolean commiKeyUpdate_Name(KeyWrapper keyWrapper) {

        if (keyWrapper.getSourceKey().getName().equals(keyWrapper.getName())) {
            return false;
        }

        keyWrapper.getSourceKey().setName(keyWrapper.getName());
        keyWrapper.getSourceKey().updateElement();

        return true;
    }

    private boolean commitKeyUpdate_Fields(KeyWrapper keyWrapper) {

        return commitKeyUpdate_Fields_Removal(keyWrapper) | commitKeyUpdate_Fields_Update(keyWrapper)
                | commitKeyUpdate_Fields_Addition(keyWrapper);

    }

    private boolean commitKeyUpdate_Fields_Removal(KeyWrapper keyWrapper) {

        boolean hasChanges = false;

        for (XSDXPathDefinition eachNeedRemovedField : getNeedRemovedFields(keyWrapper)) {
            keyWrapper.getSourceKey().getFields().remove(eachNeedRemovedField);
            keyWrapper.getSourceKey().updateElement();

            hasChanges = true;
        }

        return hasChanges;
    }

    private boolean commitKeyUpdate_Fields_Update(KeyWrapper keyWrapper) {

        boolean hasChanges = false;

        for (FieldWrapper eachField : keyWrapper.getFields()) {

            if (eachField.isUserCreated()) {
                continue;
            }

            if (eachField.getSourceField().getValue().equals(eachField.getXPath())) {
                continue;
            }

            eachField.getSourceField().setValue(eachField.getXPath());
            eachField.getSourceField().updateElement();

            hasChanges = true;
        }

        return hasChanges;
    }

    private boolean commitKeyUpdate_Fields_Addition(KeyWrapper keyWrapper) {

        boolean hasChanges = false;

        for (FieldWrapper eachField : keyWrapper.getFields()) {
            if (!eachField.isUserCreated()) {
                continue;
            }

            XSDXPathDefinition newXpath = XSDSchemaBuildingTools.getXSDFactory().createXSDXPathDefinition();
            newXpath.setValue(eachField.getXPath());
            newXpath.setVariety(XSDXPathVariety.FIELD_LITERAL);

            keyWrapper.getSourceKey().getFields().add(newXpath);
            keyWrapper.getSourceKey().updateElement();

            hasChanges = true;
        }

        return hasChanges;
    }

    private XSDXPathDefinition[] getNeedRemovedFields(KeyWrapper keyWrapper) {

        List<XSDXPathDefinition> curLeftFields = new ArrayList<XSDXPathDefinition>();
        List<XSDXPathDefinition> needRemovedFields = new ArrayList<XSDXPathDefinition>();

        for (FieldWrapper eachField : keyWrapper.getFields()) {
            if (!eachField.isUserCreated()) {
                curLeftFields.add(eachField.getSourceField());
            }
        }

        for (XSDXPathDefinition eachSourceField : keyWrapper.getSourceKey().getFields()) {
            if (!curLeftFields.contains(eachSourceField)) {
                needRemovedFields.add(eachSourceField);
            }
        }

        return needRemovedFields.toArray(new XSDXPathDefinition[0]);
    }

    private XSDIdentityConstraintDefinition[] getNeedRemovedKeys() {

        List<XSDIdentityConstraintDefinition> curLeftSourceKeys = new ArrayList<XSDIdentityConstraintDefinition>();
        List<XSDIdentityConstraintDefinition> needRemovedKeys = new ArrayList<XSDIdentityConstraintDefinition>();

        for (KeyWrapper eachKeyWrapper : getCommitedObj().getKeys()) {
            if (!eachKeyWrapper.isUserCreated()) {
                curLeftSourceKeys.add(eachKeyWrapper.getSourceKey());
            }
        }

        for (XSDIdentityConstraintDefinition eachSourceKey : getCommitedObj().getSourceEntity()
                .getIdentityConstraintDefinitions()) {
            if (!curLeftSourceKeys.contains(eachSourceKey)) {
                needRemovedKeys.add(eachSourceKey);
            }
        }

        return needRemovedKeys.toArray(new XSDIdentityConstraintDefinition[0]);
    }

    private void validateEntityName() throws CommitValidationException {

        if (getCommitedObj().getName() == null || "".equals(getCommitedObj().getName().trim())) {//$NON-NLS-1$
            throw new CommitValidationException(ERR_ENTITY_NULLENTITYNAME);
        }

        if (getCommitedObj().getName().replaceAll("\\s", "").length() != getCommitedObj().getName().length()) {
            throw new CommitValidationException(ERR_ENTITY_CONTAINEMPTY);
        }

        for (XSDElementDeclaration eachElement : getCommitedObj().getSchema().getElementDeclarations()) {

            if (eachElement.equals(getCommitedObj().getSourceEntity())) {
                continue;
            }

            if (eachElement.getName().equalsIgnoreCase(getCommitedObj().getName())) {
                throw new CommitValidationException(ERR_ENTITY_DULPLICATEENTITYNAME + " : " + getCommitedObj().getName());//$NON-NLS-1$
            }
        }

    }

    private void validateEntityKeys() throws CommitValidationException {

        for (KeyWrapper eachKeyWrapper : getCommitedObj().getKeys()) {
            validateEachKey(eachKeyWrapper);
        }

        validateKeyUnique();

        validateDuplicateKeyNameInEntityWrapper();
    }

    private void validateKeyUnique() throws CommitValidationException {

        int uniqueKeyCount = 0;

        for (KeyWrapper eachKeyWrapper : getCommitedObj().getKeys()) {
            if (eachKeyWrapper.getType() == XSDIdentityConstraintCategory.UNIQUE_LITERAL) {
                uniqueKeyCount++;
            }
        }

        if (uniqueKeyCount > 1) {
            throw new CommitValidationException(ERR_KEY_MULTIUNIQUE);
        }

    }

    private void validateDuplicateKeyNameInEntityWrapper() throws CommitValidationException {

        Set<String> keyWrapperNames = new HashSet<String>();

        for (KeyWrapper eachKeyWrapper : getCommitedObj().getKeys()) {
            keyWrapperNames.add(eachKeyWrapper.getName());
        }

        if (keyWrapperNames.size() != getCommitedObj().getKeys().length) {
            throw new CommitValidationException(ERR_KEY_DUPLICATEKEYNAME);
        }
    }

    private void validateEachKey(KeyWrapper checkedKeyWrapper) throws CommitValidationException {

        validateEachKeyName(checkedKeyWrapper);

        validateEachKeySelector(checkedKeyWrapper);

        validateEachKeyFieldsCount(checkedKeyWrapper);

        for (FieldWrapper eachFieldWrapper : checkedKeyWrapper.getFields()) {
            validateEachField(checkedKeyWrapper, eachFieldWrapper);
        }
    }

    private void validateEachKeyName(KeyWrapper checkedKeyWrapper) throws CommitValidationException {

        if (checkedKeyWrapper.getName() == null || "".equals(checkedKeyWrapper.getName().trim())) {//$NON-NLS-1$
            throw new CommitValidationException(ERR_KEY_NULLKEYNAME);
        }

        if (checkedKeyWrapper.getName().replaceAll("\\s", "").length() != checkedKeyWrapper.getName().length()) {//$NON-NLS-1$//$NON-NLS-2$
            throw new CommitValidationException(ERR_KEY_CONTAINEMPTY);
        }

        for (XSDIdentityConstraintDefinition eachId : getCommitedObj().getSchema().getIdentityConstraintDefinitions()) {

            if (eachId.equals(checkedKeyWrapper.getSourceKey())
                    || eachId.getContainer().equals(getCommitedObj().getSourceEntity())) {
                continue;
            }

            if (eachId.getName().equals(checkedKeyWrapper.getName())) {
                throw new CommitValidationException(ERR_KEY_DUPLICATEKEYNAME + " : " + checkedKeyWrapper.getName());//$NON-NLS-1$
            }
        }
    }

    private void validateEachKeySelector(KeyWrapper checkedKeyWrapper) throws CommitValidationException {

        if (checkedKeyWrapper.getSelector() == null || "".equals(checkedKeyWrapper.getSelector().trim())) {//$NON-NLS-1$
            throw new CommitValidationException(ERR_SELECTOR_WRONGFORMAT + " : key " + checkedKeyWrapper.getName());//$NON-NLS-1$
        }

    }

    private void validateEachKeyFieldsCount(KeyWrapper checkedKeyWrapper) throws CommitValidationException {

        if (checkedKeyWrapper.getFields().length == 0) {
            throw new CommitValidationException(ERR_KEY_NOFIELDS + " : key " + checkedKeyWrapper.getName());//$NON-NLS-1$
        }
    }

    private void validateEachField(KeyWrapper checkedKeyWrapper, FieldWrapper eachFieldWrapper) throws CommitValidationException {

        if (eachFieldWrapper.getXPath() == null || "".equals(eachFieldWrapper.getXPath().trim())) {//$NON-NLS-1$
            throw new CommitValidationException(ERR_FIELD_WRONGFORMAT + " : key " + checkedKeyWrapper.getName());//$NON-NLS-1$
        }

    }
}
