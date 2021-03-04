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
package com.amalto.workbench.widgets.composites;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.dialogs.datamodel.IXPathSelectionFilter;
import com.amalto.workbench.dialogs.datamodel.SelectXPathDialog;
import com.amalto.workbench.dialogs.filter.SimpleXPathSelectionFilter;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.XSDUtil;

public class SimpleXPathComposite extends Composite {

    private int caretOffset;

    private ModifyListener xpathModifyListener;

    private Text txtXPath;

    private Button btnSelectXPath;

    private Label lblTitle;

    private String defaultDataModelForSelect = "";//$NON-NLS-1$

    private IAllDataModelHolder allDataModelHolder;

    public static final String DEFAULTTITLE = Messages.EnterXpathForeignKey;
    protected BasePropertySection section;

    private Button btnSep;

    private Boolean sepFk = false;

    private List<String> allPKXpaths;

    private ControlDecoration deco;

    public SimpleXPathComposite(Composite parent, int style, String title, IAllDataModelHolder allDataModelHolder,
            String defaultDataModelForSelect, final BasePropertySection section, boolean btnsp) {
        super(parent, style);
        this.section=section;
        this.defaultDataModelForSelect = defaultDataModelForSelect;
        this.allDataModelHolder = allDataModelHolder;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        if (title != null && !"".trim().equals(title)) {//$NON-NLS-1$
            lblTitle = new Label(this, SWT.NONE);
            final GridData gd_lblTitle = new GridData(SWT.FILL, SWT.CENTER, true, false);
            lblTitle.setLayoutData(gd_lblTitle);
            lblTitle.setText(title);
        }

        final Composite composite = new Composite(this, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.marginWidth = 0;
        gridLayout_1.marginHeight = 0;
        gridLayout_1.numColumns = 2;
        composite.setLayout(gridLayout_1);

        txtXPath = new Text(composite, SWT.BORDER);
        txtXPath.addModifyListener(getXpathModifyListener());
        final GridData gd_txtXPath = new GridData(SWT.FILL, SWT.CENTER, true, false);
        txtXPath.setLayoutData(gd_txtXPath);
        deco = new ControlDecoration(txtXPath, SWT.TOP | SWT.LEFT);
        // re-use an existing image
        Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
        deco.setImage(image);
        deco.hide();

        btnSelectXPath = new Button(composite, SWT.NONE);
        btnSelectXPath.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
        btnSelectXPath.setToolTipText(Messages.SchematronExpressBuilder_selectXPath);
        if (btnsp) {
            btnSep = new Button(composite, SWT.CHECK);
            btnSep.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
            btnSep.setText(Messages.SimpleXpathInputDialog_sepFkTabPanel);
            btnSep.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    sepFk = btnSep.getSelection();
                    section.autoCommit();
                }
            });
        }
        initUIListeners();
    }

    private ModifyListener getXpathModifyListener() {
        if (xpathModifyListener == null) {
            xpathModifyListener = new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    boolean valid = allPKXpaths.contains(txtXPath.getText());
                    if (valid) {
                        caretOffset = txtXPath.getCaretPosition();
                        if (section != null) {
                            section.autoCommit();
                        }
                    }

                    decorateXPathText(valid);
                }
            };
        }

        return xpathModifyListener;
    }

    private void addXPathModifyListener() {
        txtXPath.addModifyListener(getXpathModifyListener());
    }

    private void removeXPathModifyListener() {
        txtXPath.removeModifyListener(getXpathModifyListener());
    }

    public String getXPath() {
        return txtXPath.getText().trim().replaceAll("'|\"", "");//$NON-NLS-1$//$NON-NLS-2$
    }

    public String getFKSep() {
        return sepFk.toString();
    }

    public void setFKSep(Boolean sepFk) {
        this.sepFk = sepFk;
        if (btnSep != null) {
            btnSep.setSelection(sepFk);
        }
    }

    public void setXPath(String xpath) {
        removeXPathModifyListener();
        txtXPath.setText(xpath);
        addXPathModifyListener();

        if (xpath != null) {
            int length = xpath.length();
            if (length >= caretOffset) {
                txtXPath.setSelection(caretOffset, caretOffset);
            } else {
                txtXPath.setSelection(length, length);
            }

            if (!xpath.isEmpty()) {
                decorateXPathText(allPKXpaths.contains(txtXPath.getText()));
            }
        }
    }

    private void decorateXPathText(boolean valid) {
        if (valid) {
            deco.hide();
        } else {
            deco.show();
            deco.setDescriptionText(Messages.InvalidXpathForFK);
        }
    }

    public void setDefaultDataModelForSelect(String defaultDataModelForSelect) {
        this.defaultDataModelForSelect = defaultDataModelForSelect;
    }

    public String getDefaultDataModelForSelect() {
        return defaultDataModelForSelect;
    }

    private void initUIListeners() {

        btnSelectXPath.addSelectionListener(new SelectionAdapter() {
            private IXPathSelectionFilter xpathSelectionFilter;

            @Override
            public void widgetSelected(SelectionEvent e) {

                SelectXPathDialog dialog = new SelectXPathDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        allDataModelHolder, defaultDataModelForSelect,null, getXPathSelectionFilter());

                if (dialog.open() != Window.OK) {
                    return;
                }

                txtXPath.setText(dialog.getSelectedXPath());
                if(section!=null ){
                    section.autoCommit();
                }
            }

            private IXPathSelectionFilter getXPathSelectionFilter() {
                if(xpathSelectionFilter == null) {
                    xpathSelectionFilter = new SimpleXPathSelectionFilter();
                }

                return xpathSelectionFilter;
            }
        });
    }

    public void setXSDSchema(XSDSchema schema) {
        allPKXpaths = XSDUtil.getAllPKXpaths(schema);
        allPKXpaths.add(0, "");
    }
}
