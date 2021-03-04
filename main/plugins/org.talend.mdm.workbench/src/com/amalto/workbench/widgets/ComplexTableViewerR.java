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
package com.amalto.workbench.widgets;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.editors.DataClusterDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;

/**
 * created by liusongbo on 2013-1-29
 *
 */
public class ComplexTableViewerR extends ComplexTableViewer {

    private IWorkbenchPartSite site;

    public ComplexTableViewerR(List<ComplexTableViewerColumn> columns, FormToolkit toolkit, Composite parent,
            IWorkbenchPartSite site) {
        super(columns, toolkit, parent);
        this.site = site;
    }

    @Override
    protected void creatTextPortion(Composite textParent, ComplexTableViewerColumn column) {
        Composite righComposite = toolkit.createComposite(textParent, SWT.NONE);
        righComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        righComposite.setLayout(layout);

        Button readRecordButton = toolkit.createButton(righComposite, Messages.ComplexTableViewerR_PickRecordFromContainer, SWT.PUSH | SWT.CENTER);
        readRecordButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                DataClusterDialog dialog = new DataClusterDialog(site.getShell(), new TreeObject(),
                        site);
                dialog.create();
                Point size = dialog.getShell().getSize();
                Rectangle clientArea = Display.getCurrent().getClientArea();
                int locx = clientArea.x + (clientArea.width - size.x)/2;
                int locy = clientArea.y + (clientArea.height - size.y)/2;
                dialog.getShell().setLocation(locx, locy);
                if (dialog.open() == Dialog.OK) {
                    String recordContent = dialog.getRecordContent();
                    if (recordContent != null && recordContent.trim().length() > 0)
                        text.setText(recordContent);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {//
            }
        });
        readRecordButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

        super.creatTextPortion(righComposite, column);
    }

}
