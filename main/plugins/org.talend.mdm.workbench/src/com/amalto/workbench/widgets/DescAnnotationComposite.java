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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.AnnotationLanguageLabelsDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * This class is meant to work out a composite populated with label, button, text which stand next to each other the
 * composite will in the parent composite holder
 *
 * @author fliu
 *
 */

public class DescAnnotationComposite implements SelectionListener {

    private Button annotationButton;

    private Text descriptionText;

    private Composite descAntionHolder;

    private AMainPageV2 accommodation;

    private String descriptionValue;

    private String dlgTitle;

    private Map<String, String> dataStore = new LinkedHashMap<String, String>();

    private TableColumn column;

    private GridData textGD;

    private static final String REGEXP_METACHARACTERS = "\\[([\\w]+)[\\s]*:[\\s]*([^]]*)\\]";//$NON-NLS-1$

    private static final Pattern DESC_PATTERN = Pattern.compile(REGEXP_METACHARACTERS);

    public DescAnnotationComposite(String labelName, String buttonName, FormToolkit toolkit, Composite parent,
            AMainPageV2 dialog, boolean isBtnRight) {
        descAntionHolder = toolkit.createComposite(parent);
        descAntionHolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginTop = 0;
        layout.marginHeight = 0;
        layout.marginBottom = 0;
        descAntionHolder.setLayout(layout);
        if (labelName != null && labelName.length() > 0) {
            Label descriptionLabel = toolkit.createLabel(descAntionHolder, labelName, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        }
        if (isBtnRight) {
            descriptionText = toolkit.createText(descAntionHolder, "", SWT.BORDER);//$NON-NLS-1$
            textGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            descriptionText.setLayoutData(textGD);

            descriptionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (accommodation != null && descriptionValue != null && !descriptionValue.equals(descriptionText.getText())) {
                        accommodation.markDirtyWithoutCommit();
                    }
                    descriptionValue = descriptionText.getText();
                    fillDataStore(descriptionText.getText());
                }
            });
            annotationButton = toolkit.createButton(descAntionHolder, "", SWT.PUSH);//$NON-NLS-1$
            annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            annotationButton.addSelectionListener(this);
            annotationButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
            annotationButton.setToolTipText(Messages.DescAnnotationComposite_AnnoBtnDesc);

        } else {
            annotationButton = toolkit.createButton(descAntionHolder, "", SWT.PUSH);//$NON-NLS-1$
            annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            annotationButton.addSelectionListener(this);
            annotationButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
            annotationButton.setToolTipText(Messages.DescAnnotationComposite_AnnoBtnDesc);

            descriptionText = toolkit.createText(descAntionHolder, "", SWT.BORDER);//$NON-NLS-1$
            textGD = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
            descriptionText.setLayoutData(textGD);
            descriptionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (accommodation != null && descriptionValue != null && !descriptionValue.equals(descriptionText.getText())) {
                        accommodation.markDirtyWithoutCommit();
                    }
                    descriptionValue = descriptionText.getText();
                    fillDataStore(descriptionText.getText());
                }
            });
        }
        // Util.createCompDropTarget(descriptionText);
        accommodation = dialog;
        dlgTitle = Messages.DescAnnotationComposite_AnnoBtnDesc;
    }

    public void setAnnotationDialogTitle(String title) {
        dlgTitle = title;
    }

    public void setText(String text) {
        descriptionText.setText(text);
        descriptionValue = text;

        fillDataStore(text);
    }

    private void fillDataStore(String text) {
        dataStore.clear();
        MultilingualDescParser.parseMultiLanguageString(text, dataStore);
    }

    public String getText() {
        return descriptionText.getText();
    }

    public Text getTextWidget() {
        return descriptionText;
    }

    public void widgetSelected(SelectionEvent e) {
        AnnotationLanguageLabelsDialog dlg = new AnnotationLanguageLabelsDialog(dataStore, new DescAnnotationListener(),
                descriptionText.getShell(), dlgTitle);

        dlg.setBlockOnOpen(true);
        dlg.open();

        if (dlg.getReturnCode() == Window.OK) {

            dlg.close();
        }
    }

    public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
    };

    public String commitChange() {
        return descriptionText.getText();
    }

    public Composite getComposite() {
        return descAntionHolder;
    }

    private class DescAnnotationListener implements SelectionListener {

        public DescAnnotationListener() {
            super();
        }

        public void widgetSelected(SelectionEvent e) {
            AnnotationLanguageLabelsDialog dlg = (AnnotationLanguageLabelsDialog) ((Widget) e.getSource()).getData("dialog");//$NON-NLS-1$
            if (dlg.getReturnCode() == Window.OK) {
                String outPut = "";//$NON-NLS-1$
                outPut = MultilingualDescParser.escapeMultiLanguageString(dataStore);

                if (!outPut.equals(descriptionText.getText())) {
                    descriptionText.setText(outPut);
                    if (accommodation != null) {
                        accommodation.markDirtyWithoutCommit();
                    }
                }

            }
            dlg.close();

        }

        public void widgetDefaultSelected(SelectionEvent e) {
        };
    }

    public TableColumn getColumn() {
        return column;
    }

    public void setColumn(TableColumn column) {
        this.column = column;
        resetWidth();
    }

    public void resetWidth() {
        if (column != null) {
            textGD.widthHint = column.getWidth() - 35;
        }
    }

    public void setEnable(boolean enable) {
        annotationButton.setEnabled(enable);
        descriptionText.setEnabled(enable);
    }
}
