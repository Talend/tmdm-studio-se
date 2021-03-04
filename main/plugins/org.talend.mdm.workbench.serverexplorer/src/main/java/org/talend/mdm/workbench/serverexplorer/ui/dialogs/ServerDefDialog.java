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
package org.talend.mdm.workbench.serverexplorer.ui.dialogs;

import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmmetadata.MdmmetadataFactory;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.mdm.workbench.serverexplorer.i18n.Messages;
import org.talend.repository.model.IProxyRepositoryFactory;

import com.amalto.workbench.service.MissingJarsException;
import com.amalto.workbench.utils.PasswordUtil;
import com.amalto.workbench.utils.XtentisException;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 *
 */
public class ServerDefDialog extends TitleAreaDialog {

    private static final Log log = LogFactory.getLog(ServerDefDialog.class);

    private Text nameText;

    private Text urlText;

    private Text userNameText;

    private Text passwordText;

    boolean isEnterprise;

    boolean isUpdateServerDef;

    Button sharePwdBtn;

    public boolean isUpdateServerDef() {
        return this.isUpdateServerDef;
    }

    private MDMServerDef serverDef;

    private String newName = null;

    private String newPassword = null;

    private String newUserName = null;

    private String newUrl = null;

    private static final int CHECK_CONNECTION_ID = 1024;

    public MDMServerDef getServerDef() {
        return this.serverDef;
    }

    /**
     * Create the dialog.
     *
     * @param parentShell
     */
    public ServerDefDialog(Shell parentShell, MDMServerDef serverDef) {
        super(parentShell);

        //
        this.serverDef = serverDef;
        isUpdateServerDef = serverDef != null;
        if (isUpdateServerDef) {
            newName = serverDef.getName();
        } else {
            this.serverDef = MdmmetadataFactory.eINSTANCE.createMDMServerDef();
            this.serverDef.setAlgorithm(PasswordUtil.ALGORITHM_COMMON_V3);
        }
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        if (isUpdateServerDef) {
            setTitle(Messages.ServerDefDialog_UpdateServer);
        } else {
            setTitle(Messages.ServerDefDialog_AddServer);
        }
        //
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        new Label(container, SWT.NONE).setText(Messages.ServerDefDialog_Name);

        nameText = new Text(container, SWT.BORDER);

        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        nameText.setFocus();

        new Label(container, SWT.NONE).setText(Messages.ServerDefDialog_Server);

        urlText = new Text(container, SWT.BORDER);

        urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group grpAuthentication = new Group(container, SWT.NONE);
        grpAuthentication.setText(Messages.ServerDefDialog_Authentication);
        grpAuthentication.setLayout(new GridLayout(2, false));
        GridData gd_grpAuthentication = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gd_grpAuthentication.widthHint = 439;
        grpAuthentication.setLayoutData(gd_grpAuthentication);

        new Label(grpAuthentication, SWT.NONE).setText(Messages.ServerDefDialog_UserName);

        userNameText = new Text(grpAuthentication, SWT.BORDER);

        userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        new Label(grpAuthentication, SWT.NONE).setText(Messages.ServerDefDialog_Password);

        passwordText = new Text(grpAuthentication, SWT.BORDER | SWT.PASSWORD);

        passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        sharePwdBtn = new Button(grpAuthentication, SWT.CHECK);
        sharePwdBtn.setText(Messages.SharePassword);
        sharePwdBtn.setToolTipText(Messages.OnlyApplicableShared);
        // sharePwdBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        sharePwdBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO
                //                passwordText.setText(""); //$NON-NLS-1$
            }

        });

        if (isLocalMode()) {
            sharePwdBtn.setEnabled(false);
            sharePwdBtn.setText(Messages.SharePassword);
            sharePwdBtn.setToolTipText(Messages.OnlyApplicableShared);
        }

        // init value
        initValue();
        // init listener
        initListener();
        return area;
    }

    private void initListener() {
        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                newName = nameText.getText().trim();
            }
        });
        urlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                newUrl = urlText.getText().trim();
            }
        });
        userNameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                newUserName = userNameText.getText().trim();
            }
        });
        passwordText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                newPassword = passwordText.getText().trim();
            }
        });

    }

    private boolean validateInput() {

        if (newName.length() == 0) {
            setErrorMessage(Messages.ServerDefDialog_NameCanNotBeEmpty);
            nameText.setFocus();
            return false;
        }

        if (ServerDefService.isExistServerDefName(newName)) {
            if ((!isUpdateServerDef) || (isUpdateServerDef() && (!serverDef.getName().equalsIgnoreCase(newName)))) {
                setErrorMessage(Messages.ServerDefDialog_SameName);
                nameText.setFocus();
                return false;
            }
        }
        if (newUrl == null || newUrl.length() == 0) {
            setErrorMessage(Messages.ServerDefDialog_ServerCanNotBeEmpty);
            urlText.setFocus();
            return false;
        }
        if (!serverDef.validate(newUrl)) {
            setErrorMessage(Messages.ServerDefDialog_ServerInvalid);
            urlText.setFocus();
            return false;
        }
        if (newUserName == null || newUserName.length() == 0) {
            setErrorMessage(Messages.ServerDefDialog_UsernameCanNotBeEmpty);
            userNameText.setFocus();
            return false;
        }
        if (newPassword == null || newPassword.length() == 0) {
            setErrorMessage(Messages.ServerDefDialog_PasswordCanNotBeEmpty);
            passwordText.setFocus();
            return false;
        }
        setErrorMessage(null);
        return true;
    }

    private boolean isLocalMode() {
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        try {
            return factory.isLocalConnectionProvider();
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            if (!validateInput()) {
                return;
            }
            updateUI2Model(serverDef);
        }
        if (buttonId == CHECK_CONNECTION_ID) {
            if (!validateInput()) {
                return;
            }

            MDMServerDef tmpServerDef = MdmmetadataFactory.eINSTANCE.createMDMServerDef();
            tmpServerDef.setAlgorithm(PasswordUtil.ALGORITHM_COMMON_V3);
            updateUI2Model(tmpServerDef);

            try {
                ServerDefService.checkMDMConnection(tmpServerDef.getDecryptedServerDef());
                setMessage(Messages.ServerExplorer_ConnectSuccessful);
            } catch (MissingJarsException e) {
                return;
            } catch (XtentisException e) {
                setErrorMessage(Messages.ServerExplorer_ConnectFailed);
            } catch (MalformedURLException e) {
                setErrorMessage(Messages.ServerExplorer_ConnectFailed);
            }
        }
        super.buttonPressed(buttonId);
    }

    private void updateUI2Model(MDMServerDef serverDef) {
        serverDef.parse(newUrl);
        serverDef.setUrl(newUrl);
        serverDef.setName(newName);
        serverDef.setUser(newUserName);
        serverDef.setPasswd(newPassword);
        if (!sharePwdBtn.getSelection()) {
            serverDef.setPasswd(null);
            serverDef.setTempPasswd(newPassword);
        } else {
            String encryptedPassword = PasswordUtil.encryptPassword(newPassword, serverDef.getAlgorithm());
            serverDef.setPasswd(encryptedPassword);
            serverDef.setTempPasswd(null);
        }
    }

    private void initValue() {
        newName = serverDef.getName();
        nameText.setText(newName);

        newUrl = serverDef.getUrl();
        urlText.setText(newUrl);

        newUserName = serverDef.getUser();
        userNameText.setText(newUserName);

        String passwd = serverDef.getPasswd();
        if (passwd != null && passwd.length() > 0) {
            passwordText.setText(PasswordUtil.decryptPassword(passwd, serverDef.getAlgorithm()));
            sharePwdBtn.setSelection(true);
        } else {
            if (isLocalMode()) {
                passwordText.setText(""); //$NON-NLS-1$
                sharePwdBtn.setSelection(true);
            } else {
                passwd = serverDef.getTempPasswd();
                if (passwd != null && passwd.length() > 0) {
                    passwordText.setText(passwd);
                } else {
                    passwordText.setText(""); //$NON-NLS-1$
                }
                sharePwdBtn.setSelection(false);
            }
        }

        newPassword = passwordText.getText().trim();

        if (!isUpdateServerDef) {
            sharePwdBtn.setSelection(true);
        }
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CHECK_CONNECTION_ID, Messages.ServerDefDialog_CheckConnection, false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public String getNewUserName() {
        return this.newUserName;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public String getNewUrl() {
        return this.newUrl;
    }

    public Text getNameText() {
        return this.nameText;
    }

    // /**
    // * Return the initial size of the dialog.
    // */
    // @Override
    // protected Point getInitialSize() {
    // return new Point(483, 300);
    // }
}
