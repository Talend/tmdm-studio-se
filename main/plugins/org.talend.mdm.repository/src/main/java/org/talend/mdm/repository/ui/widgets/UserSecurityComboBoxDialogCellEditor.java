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
package org.talend.mdm.repository.ui.widgets;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.talend.mdm.repository.core.service.RepositoryQueryService;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmserverobject.WSDataModelE;
import org.talend.mdm.repository.ui.dialogs.xpath.XpathSelectDialog2;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.ui.dialogs.SelectServerDefDialog;

import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.exadapter.ExAdapterManager;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.widgets.celleditor.EditableComboBoxDialogCellEditor;

/**
 * created by liusongbo on 2014-3-24
 */
public class UserSecurityComboBoxDialogCellEditor extends EditableComboBoxDialogCellEditor {

    private static Logger log = Logger.getLogger(UserSecurityComboBoxDialogCellEditor.class);

    private static final List<String> SPECIAL_FIELDS = Arrays.asList("properties"); //$NON-NLS-1$
    private IWorkbenchPartSite site;

    private String dataModelName = "PROVISIONING"; //$NON-NLS-1$

    private String conceptName = "User"; //$NON-NLS-1$

    private IUserSecurityComboBoxDialogCellEditorExAdapter cellEditorAdapter;

    public UserSecurityComboBoxDialogCellEditor(Composite parent, IWorkbenchPartSite site) {
        super(parent, new String[0]);
        this.site = site;
        init();
    }

    private void init() {
        cellEditorAdapter = ExAdapterManager.getAdapter(this, IUserSecurityComboBoxDialogCellEditorExAdapter.class);
        if (cellEditorAdapter != null) {
            cellEditorAdapter.init(this);
        }
        getButton().setToolTipText(Messages.UserSecurityComboBoxDialogCellEditor_SelectXpath);
        getButton().setText("..."); //$NON-NLS-1$
    }

    @Override
    protected Control createContents(Composite cell) {
        Control createdContents = super.createContents(cell);
        return createdContents;
    }
    
    @Override
    protected FocusListener getComboFocusListener() {
        if (comboFocusListener == null) {
            comboFocusListener = new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    boolean isComboSelected = false;
                    CCombo comboBox = getComboBox();
                    int selectionIndex = comboBox.getSelectionIndex();
                    if (selectionIndex != -1 && selectionIndex < comboBox.getItemCount()) {
                        if (!comboBox.getItem(selectionIndex).isEmpty()) {
                            isComboSelected = true;
                        }
                    }

                    if (!getButton().isFocusControl() && !isComboSelected) {
                        UserSecurityComboBoxDialogCellEditor.this.focusLost();
                    }
                }

            };
        }
        return comboFocusListener;
    }

    @Override
    protected SelectionListener getComboSelectionListener() {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                doSelection();
            }
        };
    }

    private void doSelection() {
        String userVariable = getUserVariable();
        if (userVariable == null) {
            fireCancelEditor();
            return;
        }

        if (SPECIAL_FIELDS.contains(userVariable)) {
            userVariable += "[\"\"]"; //$NON-NLS-1$
        }
        userVariable = "${user_context." + userVariable + "}"; //$NON-NLS-1$ //$NON-NLS-2$
        doSetValue(userVariable);
        fireApplyEditorValue();
    }

    private String getUserVariable() {
        String schema = getSchemaFromRepository();
        if (schema == null) {
            schema = getSchemaFromServer();
        }

        if (schema != null) {
            UserVariableDialog dialog = new UserVariableDialog(site.getShell(), schema);
            if (dialog.open() == IDialogConstants.OK_ID) {
                return dialog.getResult();
            }
        }
        return null;
    }

    private String getSchemaFromRepository() {
        String schema = null;

        WSDataModelE wsDataModel = RepositoryQueryService.findDataModelByName(dataModelName);
        if (wsDataModel != null) {
            schema = wsDataModel.getXsdSchema();
        }

        return schema;
    }

    private String getSchemaFromServer() {
        String schema = null;

        MDMServerDef serverDef = getServerDef();
        if (isAccessible(serverDef)) {
            TMDMService service = getService(serverDef);
            WSDataModel wsDataModel = service.getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelName)));
            schema = wsDataModel.getXsdSchema();
        }

        return schema;
    }

    private MDMServerDef getServerDef() {
        SelectServerDefDialog dialog = new SelectServerDefDialog(site.getShell());
        if (dialog.open() == IDialogConstants.OK_ID) {
            return dialog.getSelectedServerDef();
        }

        return null;
    }

    private TMDMService getService(MDMServerDef serverDef) {
        TMDMService service = null;
        try {
            service = RepositoryWebServiceAdapter.getMDMService(serverDef);
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }

        return service;
    }

    private boolean isAccessible(MDMServerDef serverDef) {
        if (serverDef != null) {
            try {
                ServerDefService.checkMDMConnection(serverDef);
                return true;
            } catch (XtentisException e) {
                MessageDialog.openError(site.getShell(), Messages.UserSecurityComboBoxDialogCellEditor_CheckConnection,
                        Messages.UserSecurityComboBoxDialogCellEditor_UnableConnectMessage);
            } catch (MalformedURLException e) {
                MessageDialog.openError(site.getShell(), Messages.UserSecurityComboBoxDialogCellEditor_CheckConnection,
                        Messages.UserSecurityComboBoxDialogCellEditor_UnableConnectMessage);
            }
        }

        return false;
    }

    @Override
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        CCombo comboBox = getComboBox();
        if (keyEvent.character == SWT.ESC) { // Esc key
            if (comboBox != null && !comboBox.isDisposed()) {
                fireCancelEditor();
            }
        } else if (keyEvent.character == SWT.CR) { //
            applyEditorValueAndDeactivate();
        }
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        if (site == null) {
            site = MDMRepositoryView.show().getSite();
        }

        XpathSelectDialog2 dlg = new XpathSelectDialog2(site.getShell(),
                Messages.UserSecurityComboBoxDialogCellEditor_SelectXpath, site, false, dataModelName);
        dlg.setConceptName(conceptName);
        dlg.setOnlyOneDataModel(true);
        dlg.setKeepFilterContent(true);
        dlg.setSelectionFilter(new IXPathSelectionFilter() {
            
            @Override
            public FilterResult check(Object obj) {
                if(obj instanceof XSDParticle) {
                    XSDParticleContent content = ((XSDParticle)obj).getContent();
                    if(content instanceof XSDElementDeclaration) {
                        String field = ((XSDElementDeclaration)content).getName();
                        if(UserField.isValidUserField(field)) {
                            return FilterResult.ENABLE;
                        }
                    }
                }
                return FilterResult.DISABLE;
            }
        });
        if (dlg.open() == IDialogConstants.OK_ID) {
            return dlg.getXpath();
        }

        return null;
    }
    
}
