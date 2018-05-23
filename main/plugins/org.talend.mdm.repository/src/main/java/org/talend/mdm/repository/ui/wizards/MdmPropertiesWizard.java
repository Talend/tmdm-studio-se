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
package org.talend.mdm.repository.ui.wizards;

import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.command.CommandManager;
import org.talend.mdm.repository.core.command.ICommand;
import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmproperties.WSResourceItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.ValidateUtil;
import org.talend.metadata.managment.ui.wizard.PropertiesWizard;
import org.talend.metadata.managment.ui.wizard.PropertiesWizardPage;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC achen class global comment. Detailled comment
 */
public class MdmPropertiesWizard extends PropertiesWizard {

    private String oldVersion;

    /**
     * DOC achen MdmPropertiesWizard constructor comment.
     * 
     * @param repositoryViewObject
     * @param path
     * @param useLastVersion
     */
    public MdmPropertiesWizard(IRepositoryViewObject repositoryViewObject, IPath path, boolean useLastVersion) {
        super(repositoryViewObject, path, useLastVersion);

        // reset the originaleObjectLabel
        Property property = object.getProperty();
        MDMServerObjectItem item = (MDMServerObjectItem) property.getItem();
        String oldName = item.getMDMServerObject().getName();
        // reset property's label
        property.setLabel(oldName);
        this.oldVersion = property.getVersion();

    }

    @Override
    public boolean performFinish() {
        if (alreadyEditedByUser) {
            return false;
        }
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        Item item = object.getProperty().getItem();
        String newName = object.getLabel();
        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        if (serverObject != null) {
            String oldName = serverObject.getName();
            try {

                if (newName != null && factory.isEditableAndLockIfPossible(item)) {

                    serverObject.setName(newName);
                    item = RepositoryResourceUtil.assertItem(item);
                    factory.save(item, false);
                    MDMServerDef lastServerDef = RepositoryResourceUtil.getLastServerDef(item);
                    if (!oldName.equals(newName) && lastServerDef != null) {
                        CommandManager.getInstance().pushCommand(ICommand.CMD_RENAME, object.getId(),
                                new String[] { oldName, newName });
                    }
                }

            } catch (PersistenceException e) {
                MessageBoxExceptionHandler.process(e);
                return false;
            } finally {
                try {
                    factory.unlock(item);
                } catch (PersistenceException e) {
                    MessageBoxExceptionHandler.process(e);
                    return false;
                } catch (LoginException e) {
                    MessageBoxExceptionHandler.process(e);
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public void addPages() {
        mainPage = new PropertiesWizardPage("WizardPage", object.getProperty(), path, isReadOnly(), false, lastVersionFound) { //$NON-NLS-1$

            @Override
            public void createControl(Composite parent) {
                Composite container = new Composite(parent, SWT.NONE);
                GridLayout layout = new GridLayout(2, false);
                container.setLayout(layout);

                if (alreadyEditedByUser) {
                    Label label = new Label(container, SWT.NONE);
                    label.setForeground(ColorConstants.red);
                    label.setText(Messages.MdmPropertiesWizard_lockedByOther);
                    GridData gridData = new GridData();
                    gridData.horizontalSpan = 2;
                    label.setLayoutData(gridData);
                }

                super.createControl(container);

                setControl(container);
                updateContent();
                nameText.setEnabled(canEditObjectName());
                addListeners();
                setPageComplete(false);

                // add catalog field for resource node
                if (object.getProperty().getItem() instanceof WSResourceItem) {
                    final WSResourceItem item = (WSResourceItem) object.getProperty().getItem();
                    final Text imageCatalog = addImageCatalogField(container, item);
                    // add listener
                    if (imageCatalog != null) {
                        imageCatalog.addModifyListener(new ModifyListener() {

                            @Override
                            public void modifyText(ModifyEvent e) {
                                item.getResource().setImageCatalog(imageCatalog.getText());
                                updatePageStatus();
                            }
                        });
                    }
                }
            }

            @Override
            protected void evaluateTextField() {
                if (isReadOnly()) {
                    return;
                }
                if (nameText == null || nameText.isDisposed()) {
                    return;
                }

                //
                String errorMsg = null;

                String newText = nameText.getText();
                if (newText.length() == 0) {
                    nameStatus = createStatus(IStatus.ERROR, Messages.Common_nameCanNotBeEmpty);
                } else {
                    if (!isValid(newText)) {
                        errorMsg = Messages.Common_nameIsUsed;
                    } else {
                        ERepositoryObjectType objectType = object.getRepositoryObjectType();
                        if (objectType.equals(IServerObjectRepositoryType.TYPE_VIEW)) {
                            if (!ValidateUtil.matchViewProcessRegex(newText)) {
                                errorMsg = Messages.Common_nameInvalid;
                            }
                        } else if (objectType.equals(IServerObjectRepositoryType.TYPE_TRANSFORMERV2)) {
                            if (newText.startsWith(ITransformerV2NodeConsDef.PREFIX_SMARTVIEW_UPPER)) {
                                if (!ValidateUtil.matchSmartViewRegex(newText)) {
                                    errorMsg = Messages.Common_nameInvalid;
                                }
                            }
                            if (errorMsg == null && !ValidateUtil.matchViewProcessRegex(newText)) {
                                errorMsg = Messages.Common_nameInvalid;
                            }
                        } else if (objectType.equals(IServerObjectRepositoryType.TYPE_CUSTOM_FORM)) {
                            if (!ValidateUtil.matchCustomFormRegex(newText)) {
                                errorMsg = Messages.Common_nameInvalid;
                            }
                        } else if (objectType.equals(IServerObjectRepositoryType.TYPE_ROLE)) {
                            if (!ValidateUtil.matchRoleRegex(newText)) {
                                errorMsg = Messages.Common_nameInvalid;
                            } else if (ValidateUtil.isSystemRoleName(newText)) {
                                errorMsg = Messages.shouldNotBeSystemRoleName;
                            }
                        } else {
                            if (!ValidateUtil.matchCommonRegex(newText)) {
                                errorMsg = Messages.Common_nameInvalid;
                            }
                        }
                    }

                    if (errorMsg != null) {
                        nameStatus = createStatus(IStatus.ERROR, errorMsg);
                    } else {
                        nameStatus = createOkStatus();
                    }
                }

                if (property != null && nameStatus.getSeverity() == IStatus.OK) {
                    property.setLabel(getPropertyLabel(newText.trim().isEmpty() ? null : newText.trim()));
                    property.setDisplayName(newText.trim().isEmpty() ? null : newText.trim());
                    property.setModificationDate(new Date());
                }
                updatePageStatus();
            }

            @Override
            public ERepositoryObjectType getRepositoryObjectType() {
                return object.getRepositoryObjectType();
            }
        };
        addPage(mainPage);
    }

    /**
     * DOC HHB Comment method "canEditObjectName".
     * 
     * @return
     */
    protected boolean canEditObjectName() {
        return true;
    }

    private Text addImageCatalogField(Composite container, WSResourceItem item) {

        Label nameLab = new Label(container, SWT.NONE);
        nameLab.setText(Messages.MdmPropertiesWizard_imageCatalog);

        final Text nameText = new Text(container, SWT.BORDER);
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        nameText.setEditable(!isReadOnly());
        if (item.getResource().getImageCatalog() != null) {
            nameText.setText(item.getResource().getImageCatalog());
        }
        return nameText;
    }
}
