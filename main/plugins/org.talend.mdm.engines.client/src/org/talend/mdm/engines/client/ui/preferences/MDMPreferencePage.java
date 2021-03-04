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
package org.talend.mdm.engines.client.ui.preferences;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.mdm.engines.client.Activator;
import org.talend.mdm.engines.client.i18n.Messages;

/**
 * @deprecated Preference for the MDMServer values.
 *
 * $Id: MDMPreferencePage.java 2738 2007-04-26 13:12:27Z cantoine $
 *
 */
@Deprecated
public class MDMPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    protected static Logger log = Logger.getLogger(MDMPreferencePage.class);

    // protected CheckBoxFieldEditor spagoBiCheckButton;

    protected MDMServerEditor editor;

    private Composite parent;

    /**
     * MDMPreferencePage.
     *
     * $Id: MDMPreferencePage.java 2738 2007-04-26 13:12:27Z cantoine $
     *
     */

    public MDMPreferencePage() {
        super(FLAT);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /*
     * @Override protected IPreferenceStore doGetPreferenceStore() { // SpagoPreferenceStore preferenceStore = new
     * SpagoPreferenceStore(ProxyRepositoryFactory.getInstance()); // try { // preferenceStore.load(); // } catch
     * (PersistenceException e) { // String detailError = e.getMessage(); // new ErrorDialogWidthDetailArea(new Shell(),
     * RepositoryPlugin.PLUGIN_ID, Messages // .getString("CommonWizard.persistenceException"), detailError);
     * //$NON-NLS-1$ // log.error(Messages.getString("CommonWizard.persistenceException") + "\n" + detailError);
     * //$NON-NLS-1$ // //$NON-NLS-2$ // } // return preferenceStore; return
     * Activator.getDefault().getPreferenceStore(); }
     */

    @Override
    protected void createFieldEditors() {
        parent = getFieldEditorParent();

        // spagoBiCheckButton = new CheckBoxFieldEditor(MDMPreferenceInitializer.MDM_STATUS, Messages
        //                .getString("MDMPreferencePage.spagoBiCheckButton"), parent); //$NON-NLS-1$
        editor = new MDMServerEditor(MDMPreferenceInitializer.MDM_SERVER, Messages.MDMPreferencePage_technicalStatusLabel, parent);
        addField(editor);

        updateEnableStateFromPreferences();

        // SelectionListener listener = new SelectionListener() {
        //
        // public void widgetDefaultSelected(SelectionEvent e) {
        // }
        //
        // public void widgetSelected(SelectionEvent e) {
        // IPreferenceStore preferenceStore = getPreferenceStore();
        // preferenceStore.setValue(MDMPreferenceInitializer.MDM_STATUS, ((Button) e.getSource()).getSelection());
        // updateEnableStateFromDisplay();
        // }
        // };
        // spagoBiCheckButton.getCheckbox().addSelectionListener(listener);
    }

    // private void updateEnableStateFromDisplay() {
    // boolean spago = spagoBiCheckButton.getBooleanValue();
    // editor.setEnabled(spago, parent);
    // }

    private void updateEnableStateFromPreferences() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        // boolean spago = preferenceStore.getBoolean(MDMPreferenceInitializer.MDM_STATUS);
        // spagoBiCheckButton.getCheckbox().setSelection(spago);
        // editor.setEnabled(spago, parent);
        editor.setEnabled(true, parent);
    }

    @Override
    protected void initialize() {
        try {
            super.initialize();
        } catch (RuntimeException e) {
            // e.printStackTrace();
            ExceptionHandler.process(e);
            setErrorMessage(e.getMessage());
            log.error(e);
        }
    }

    @Override
    protected void checkState() {
        if (getErrorMessage() == null) {
            super.checkState();
        } else {
            setValid(false);
        }
    }

    public void init(IWorkbench workbench) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        super.performDefaults();
        // updateEnableStateFromDisplay();
    }

}
