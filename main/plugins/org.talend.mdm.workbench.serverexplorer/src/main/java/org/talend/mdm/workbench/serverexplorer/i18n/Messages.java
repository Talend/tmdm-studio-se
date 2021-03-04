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
package org.talend.mdm.workbench.serverexplorer.i18n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.talend.mdm.workbench.serverexplorer.i18n.messages"; //$NON-NLS-1$

    // //////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    // //////////////////////////////////////////////////////////////////////////
    private Messages() {
        // do not instantiate
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Class initialization
    //
    // //////////////////////////////////////////////////////////////////////////
    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    public static String bind(String message, String... bindings) {
        return NLS.bind(message, bindings);
    }

    public static String DownloadLogDialog_BroswerButton_Text;

    public static String DownloadLogDialog_InputError_Message;

    public static String DownloadLogDialog_Message;

    public static String DownloadLogDialog_OpenButton_Text;

    public static String DownloadLogDialog_PathLabel_Text;

    public static String DownloadLogDialog_Title;

    public static String MDMServerConsolePrefsPage_Display_Buffer_Text;

    public static String MDMServerConsolePrefsPage_Error_Message;

    public static String MDMServerConsolePrefsPage_Error_Message1;

    public static String MDMServerConsolePrefsPage_RefreshLabel_1;

    public static String MDMServerConsolePrefsPage_RefreshLabel_2;

    public static String MDMServerMessageConsole_closeActionLabel;

    public static String MDMServerMessageConsole_DownloadFailed_Message;

    public static String MDMServerMessageConsole_DownloadTask_Name;

    public static String MDMServerMessageConsole_DownloadAction_Text;

    public static String MDMServerMessageConsole_error;

    public static String MDMServerMessageConsole_Forbidden_Message;

    public static String MDMServerMessageConsole_mdmFlag;

    public static String MDMServerMessageConsole_MonitorAction_PauseText;

    public static String MDMServerMessageConsole_MonitorAction_ResumeText;

    public static String MDMServerMessageConsole_Name;

    public static String MDMServerMessageConsole_No_Acess_Message;

    public static String MDMServerMessageConsole_NotConnected_Message;

    public static String MDMServerMessageConsole_ReloadAction_Text;

    public static String SelectServerDefDialog_selectServerLocation;

    public static String ServerDefDialog_AddServer;

    public static String ServerDefDialog_Authentication;

    public static String ServerDefDialog_CheckConnection;

    public static String ServerDefDialog_ConnectFailedWithDetail;

    public static String ServerDefDialog_Name;

    public static String ServerDefDialog_NameCanNotBeEmpty;

    public static String ServerDefDialog_Password;

    public static String ServerDefDialog_PasswordCanNotBeEmpty;

    public static String ServerDefDialog_SameName;

    public static String ServerDefDialog_Server;

    public static String ServerDefDialog_ServerCanNotBeEmpty;

    public static String ServerDefDialog_ServerInvalid;

    public static String ServerDefDialog_UpdateServer;

    public static String ServerDefDialog_UserName;

    public static String ServerDefDialog_UsernameCanNotBeEmpty;

    public static String ServerExplorer_AddServer;

    public static String ServerExplorer_CheckConnection;

    public static String ServerExplorer_ConnectFailed;

    public static String ServerExplorer_ConnectSSLFailed;

    public static String ServerExplorer_ConnectSuccessful;

    public static String ServerExplorer_Disable;

    public static String ServerExplorer_EditServer;

    public static String ServerExplorer_Enable;

    public static String ServerExplorer_RemoveConfirm;

    public static String ServerExplorer_RemoveServer;

    public static String ServerExplorer_ViewLogAction_Text;

    public static String ServerExplorer_EventManager;

    public static String ServerExplorer_WarningText;

    public static String Reinput_Password;

    public static String SharePassword;

    public static String ShowMDMServerConsole_InfoDialog_Message;

    public static String ShowMDMServerConsole_InfoDialog_Title;

    public static String OnlyApplicableShared;

}
