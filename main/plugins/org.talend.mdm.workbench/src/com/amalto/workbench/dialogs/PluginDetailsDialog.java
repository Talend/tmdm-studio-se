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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.FontUtils;

public class PluginDetailsDialog extends Dialog {

    private static Log log = LogFactory.getLog(PluginDetailsDialog.class);

    protected TabFolder tabFolder;

    private String description;

    private String documentation;

    // private String parametersSchema;

    private Collection<Listener> listeners = new ArrayList<Listener>();

    private String templetName;

    public PluginDetailsDialog(Shell parentShell, String description, String documentation, String parametersSchema,
            String templetName) {
        super(parentShell);
        this.description = description;
        this.documentation = documentation;
        this.templetName = templetName;
        // this.parametersSchema = parametersSchema;
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    protected Control createDialogArea(Composite parent) {

        try {
            // Should not really be here but well,....
            parent.getShell().setText(Messages.PluginDetailsDialog_PluginDetails);

            Composite composite = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            applyDialogFont(composite);

            tabFolder = new TabFolder(composite, SWT.TOP);
            tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            tabFolder.setLayout(new GridLayout(1, true));
            ((GridData) tabFolder.getLayoutData()).heightHint = 600;
            ((GridData) tabFolder.getLayoutData()).widthHint = 600;

            TabItem descriptionTI = new TabItem(tabFolder, SWT.NULL);
            descriptionTI.setText(Messages.PluginDetailsDialog_Description);
            descriptionTI.setToolTipText(Messages.PluginDetailsDialog_DescriptionTip);

            Composite descriptionC = new Composite(tabFolder, SWT.NULL);
            descriptionC.setLayout(new GridLayout(1, true));
            descriptionC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            /*
             * Label descriptionLabel = new Label(descriptionC, SWT.LEFT); descriptionLabel.setLayoutData( new
             * GridData(SWT.LEFT,SWT.CENTER,false,false,1,1) ); FontData fd =
             * descriptionLabel.getFont().getFontData()[0]; fd.setStyle(SWT.BOLD); descriptionLabel.setFont(new
             * Font(Display.getDefault(),fd)); descriptionLabel.setText("Description");
             */

            Label descValue = new Label(descriptionC, SWT.WRAP);
            descValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            descValue.setText(description + "\n");//$NON-NLS-1$

            Label documentationLabel = new Label(descriptionC, SWT.LEFT);
            documentationLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
            documentationLabel.setFont(FontUtils.getBoldFont(documentationLabel.getFont()));
            documentationLabel.setText(templetName);

            Text docValue = new Text(descriptionC, SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
            docValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            docValue.setBackground(documentationLabel.getBackground());
            docValue.setText(documentation);

            descriptionTI.setControl(descriptionC);

            /*
             * TabItem parametersTI = new TabItem(tabFolder,SWT.NULL); parametersTI.setText("Parameters");
             * parametersTI.setToolTipText("Display the plugin description and documentation");
             *
             * Composite parametersC = new Composite(tabFolder,SWT.NULL); parametersC.setLayout(new GridLayout(1,true));
             *
             * Label paramsValue = new Label(parametersC, SWT.WRAP); paramsValue.setLayoutData( new
             * GridData(SWT.FILL,SWT.CENTER,true,false,1,1) ); paramsValue.setText(parametersSchema);
             *
             * parametersTI.setControl(parametersC);
             */

            tabFolder.setSelection(0);

            return composite;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getShell(), Messages._Error,
                    Messages.bind(Messages.PluginDetailsDialog_ErrorMsg, e.getLocalizedMessage()));
            return null;
        }

    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, 0, Messages.PluginDetailsDialog_Close, true);
    }

    protected void buttonPressed(int buttonId) {
        Event e = new Event();
        e.button = buttonId;
        notifyListeners(e);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(Event e) {
        for (Iterator<Listener> iter = listeners.iterator(); iter.hasNext();) {
            Listener listener = iter.next();
            listener.handleEvent(e);
        }
    }

}
