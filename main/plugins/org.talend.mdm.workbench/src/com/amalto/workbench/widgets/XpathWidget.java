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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeParent;

public class XpathWidget implements SelectionListener {

    private Composite xpathAntionHolder;

    private Button annotationButton;

    protected Text descriptionText;

    private String descriptionValue;

    protected AMainPageV2 accommodation;

    protected String dlgTitle;

    protected TreeParent treeParent;

    protected XpathSelectDialog xpathSelectDialog;

    private boolean readOnly = false;

    protected Composite parent;

    protected IWorkbenchPartSite site;

    protected String dataModelName;

    protected boolean lock;

    protected boolean isMulti = true;

    protected String conceptName;

    private String context;

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
        if (dlg != null) {
            dlg.setConceptName(conceptName);
        }
    }

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    // Modified by hhb,to fix bug 21784
    // public XpathWidget(Composite parent, AMainPageV2 page, boolean isMulti) {
    //        this("", page.getXObject().getParent(), null, parent, page, false, false, "");//$NON-NLS-1$//$NON-NLS-2$
    // this.isMulti = isMulti;
    // }
    //
    // public XpathWidget(Composite parent, boolean isMulti) {
    //        this("", null, null, parent, null, false, false, "");//$NON-NLS-1$//$NON-NLS-2$
    // this.isMulti = isMulti;
    // }
    public XpathWidget(Composite parent, TreeParent treeParent, boolean isMulti) {
        this("", treeParent, null, parent, null, false, false, "");//$NON-NLS-1$//$NON-NLS-2$
        this.isMulti = isMulti;
    }

    // The ending| bug:21784
    public XpathWidget(String buttonName, TreeParent treeParent, FormToolkit toolkit, Composite parent, AMainPageV2 dialog,
            boolean isButtonLeft, boolean readOnly, String dataModelName) {

        this.parent = parent;
        this.treeParent = treeParent;
        if (toolkit == null) {
            toolkit = new FormToolkit(parent.getDisplay());
        }

        xpathAntionHolder = toolkit.createComposite(parent, SWT.NO_FOCUS);
        xpathAntionHolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginTop = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        xpathAntionHolder.setLayout(layout);

        dlgTitle = Messages.XpathWidget_DialogTitle;
        accommodation = dialog;
        ModifyListener listenr = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (descriptionValue != null && !descriptionValue.equals(descriptionText.getText())) {
                    // accommodation.markDirty();
                    if (accommodation != null) {
                        accommodation.markDirtyWithoutCommit();
                    }
                }
                descriptionValue = descriptionText.getText();
            }
        };
        if (isButtonLeft) {
            annotationButton = toolkit.createButton(xpathAntionHolder, "", SWT.PUSH);//$NON-NLS-1$
            annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            annotationButton.addSelectionListener(this);
            if (readOnly) {
                descriptionText = toolkit.createText(xpathAntionHolder, "", SWT.BORDER | SWT.SINGLE | SWT.LEFT | SWT.READ_ONLY);//$NON-NLS-1$
            } else {
                descriptionText = toolkit.createText(xpathAntionHolder, "", SWT.BORDER | SWT.SINGLE | SWT.LEFT);//$NON-NLS-1$
            }
            descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            descriptionText.addModifyListener(listenr);

        } else {
            if (readOnly) {
                descriptionText = toolkit.createText(xpathAntionHolder, "", SWT.BORDER | SWT.SINGLE | SWT.LEFT | SWT.READ_ONLY);//$NON-NLS-1$
            } else {
                descriptionText = toolkit.createText(xpathAntionHolder, "", SWT.BORDER | SWT.SINGLE | SWT.LEFT);//$NON-NLS-1$
            }
            descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            descriptionText.addModifyListener(listenr);
            annotationButton = toolkit.createButton(xpathAntionHolder, buttonName, SWT.PUSH);
            annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            annotationButton.addSelectionListener(this);
        }
        descriptionText.addTraverseListener(new TraverseListener() {

            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        annotationButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        annotationButton.setToolTipText(Messages.XpathWidget_DialogTitle);

    }

    protected XpathSelectDialog dlg;

    private FocusListener focusListener;

    private FocusListener bunFocusListener;

    public void widgetDefaultSelected(SelectionEvent e) {

    }

    protected void disableFocusListener() {
        if (bunFocusListener != null) {
            annotationButton.removeFocusListener(bunFocusListener);
            descriptionText.removeFocusListener(bunFocusListener);
        }
    }

    protected void enableFocusListener() {
        if (bunFocusListener != null) {
            annotationButton.addFocusListener(bunFocusListener);
            descriptionText.addFocusListener(bunFocusListener);
        }
    }

    public void widgetSelected(SelectionEvent e) {

        if (accommodation != null) {
            if (dlg == null) {
                dlg = new XpathSelectDialog(accommodation.getSite().getShell(), treeParent, dlgTitle, accommodation.getSite(),
                        isMulti, dataModelName

                );
                dlg.setConceptName(conceptName);
            }
        } else {
            if (dlg == null) {
                dlg = new XpathSelectDialog(parent.getShell(), treeParent, dlgTitle, site, false, dataModelName);
                dlg.setConceptName(conceptName);
            }
        }
        dlg.setLock(lock);
        dlg.setBlockOnOpen(true);
        disableFocusListener();
        dlg.open();
        enableFocusListener();
        if (dlg.getReturnCode() == Window.OK) {
            descriptionText.setText(dlg.getXpath());
            dataModelName = dlg.getDataModelName();
            dlg.close();
            setOutFocus();
        } else {
            lostFocus();
        }
    }

    public Composite getComposite() {
        return xpathAntionHolder;
    }

    public String getText() {
        // return descriptionText.getText().replaceAll("\\s+", "").trim();
        return descriptionText.getText().trim();
    }

    public void setText(String text) {
        // descriptionText.setText(text.replaceAll("\\s+", "").trim());
        descriptionText.setText(text == null ? "" : text.trim());//$NON-NLS-1$
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Text getTextWidget() {
        return descriptionText;
    }

    public Button getButton() {
        return annotationButton;
    }

    public void addButtonFocusListener(FocusListener bunFocusListener) {
        this.bunFocusListener = bunFocusListener;
        enableFocusListener();
    }

    public void setOutFocus() {
        descriptionText.setFocus();
        descriptionText.setText(descriptionText.getText().trim());
        int start = descriptionText.getText().length();
        // int end = descriptionText.getSelection().y;
        descriptionText.setSelection(start);
        if (focusListener != null) {
            focusListener.focusLost(null);
        }

    }

    public void lostFocus() {
        if (focusListener != null) {
            focusListener.focusLost(null);
        }
    }

    /**
     * only need implement focusLost method
     *
     * @param focusListener
     */
    public void setAfterFocusAction(FocusListener focusListener) {
        this.focusListener = focusListener;
    }
}
