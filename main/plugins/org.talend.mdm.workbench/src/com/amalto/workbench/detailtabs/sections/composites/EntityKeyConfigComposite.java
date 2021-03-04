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
package com.amalto.workbench.detailtabs.sections.composites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDXPathDefinition;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.model.entity.EntityWrapper;
import com.amalto.workbench.detailtabs.sections.model.entity.FieldWrapper;
import com.amalto.workbench.detailtabs.sections.model.entity.KeyWrapper;
import com.amalto.workbench.detailtabs.sections.providers.FieldWrapperLabelProvider;
import com.amalto.workbench.detailtabs.sections.providers.KeyWrapperLabelProvider;
import com.amalto.workbench.dialogs.IdentityConstraintInputDialog;
import com.amalto.workbench.dialogs.SelectFieldDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.ListContentProvider;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.inputvalidator.EditXSDIdentityConstraintNameValidator;
import com.amalto.workbench.utils.inputvalidator.NewXSDIndentityConstraintValidator;

public class EntityKeyConfigComposite extends Composite {

    private static Log log = LogFactory.getLog(EntityKeyConfigComposite.class);

    private EntityWrapper entityWrapper;

    private Combo comboSelector;

    private Text txtKeyName;

    private Label lblKeyNameErrIndicator;

    private TreeViewer tvKeys;

    private TreeViewer tvFields;

    private Button btnAddKey;

    private Button btnDeleteKey;

    private Button btnAddField;

    private Button btnDeleteField;

    private Button btnEditField;

    private Image addKeyIcon = ImageCache.getImage(EImage.ADD_OBJ.getPath()).createImage();

    private Image deleteKeyIcon = ImageCache.getImage(EImage.DELETE_OBJ.getPath()).createImage();

    private Image addFieldIcon = ImageCache.getImage(EImage.ADD_OBJ.getPath()).createImage();

    private Image deleteFieldIcon = ImageCache.getImage(EImage.DELETE_OBJ.getPath()).createImage();

    private Image editFieldIcon = ImageCache.getImage(EImage.EDIT_OBJ.getPath()).createImage();

    private Image errIcon = ImageCache.getImage(EImage.ERROR.getPath()).createImage();

    private ISelectionChangedListener lTvKeysSelectionListener;

    private ISelectionChangedListener lTvFieldsSelectionListener;

    private ModifyListener lTxtKeyNameModifyListener;

    private ModifyListener lComboSelectorModifyListner;

    private MouseListener lComboSelectorMouseListener;

    private SelectionListener lBtnRemoveKeySelectionListner;

    private SelectionListener lBtnRemoveFieldListner;

    private SelectionListener lBtnAddKeyListener;

    private SelectionListener lBtnAddFieldListener;

    private SelectionListener lBtnEditFieldListener;

    private XSDElementDeclaration xsdElementDeclaration;
    private BasePropertySection section;

    public EntityKeyConfigComposite(Composite parent, int style, BasePropertySection section,XSDElementDeclaration xsdElementDeclaration) {
    	this(parent,style);
    	this.section=section;
    	this.xsdElementDeclaration=xsdElementDeclaration;
    }
    public EntityKeyConfigComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());

        final SashForm sashForm = new SashForm(this, SWT.NONE);
        sashForm.setSashWidth(2);
        sashForm.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Composite compKeys = new Composite(sashForm, SWT.NONE);
        compKeys.setLayout(new GridLayout());
        compKeys.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        tvKeys = new TreeViewer(compKeys, SWT.BORDER | SWT.FULL_SELECTION);
        Tree trKeys = tvKeys.getTree();
        trKeys.setLinesVisible(true);
        final GridData gd_trKeys = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_trKeys.widthHint = 165;
        trKeys.setLayoutData(gd_trKeys);

        final Composite compKeyBtns = new Composite(compKeys, SWT.NONE);
        compKeyBtns.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        final GridData gd_compKeyBtns = new GridData(SWT.FILL, SWT.CENTER, true, false);
        compKeyBtns.setLayoutData(gd_compKeyBtns);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        compKeyBtns.setLayout(gridLayout);

        btnAddKey = new Button(compKeyBtns, SWT.NONE);
        btnAddKey.setToolTipText(Messages.EntityKeyConfigComposite_0);
        btnAddKey.setImage(addKeyIcon);

        btnDeleteKey = new Button(compKeyBtns, SWT.NONE);
        btnDeleteKey.setToolTipText(Messages.EntityKeyConfigComposite_1);
        btnDeleteKey.setImage(deleteKeyIcon);

        final Composite compKeyInfo = new Composite(sashForm, SWT.NONE);
        compKeyInfo.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        final GridLayout gridLayout_2 = new GridLayout();
        compKeyInfo.setLayout(gridLayout_2);

        final Composite composite = new Composite(compKeyInfo, SWT.NONE);
        composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.marginWidth = 0;
        gridLayout_3.marginHeight = 0;
        gridLayout_3.numColumns = 2;
        composite.setLayout(gridLayout_3);

        final Label lblKeyName = new Label(composite, SWT.NONE);
        lblKeyName.setText(Messages.EntityKeyConfigComposite_2);
        lblKeyName.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        lblKeyNameErrIndicator = new Label(composite, SWT.NONE);
        lblKeyNameErrIndicator.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        lblKeyNameErrIndicator.setImage(errIcon);
        lblKeyNameErrIndicator.setVisible(false);

        txtKeyName = new Text(compKeyInfo, SWT.BORDER);
        final GridData gd_txtKeyName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        txtKeyName.setLayoutData(gd_txtKeyName);

        final Label lblSelector = new Label(compKeyInfo, SWT.NONE);
        lblSelector.setText(Messages.EntityKeyConfigComposite_Selector);
        lblSelector.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        comboSelector = new Combo(compKeyInfo, SWT.BORDER);
        final GridData gd_comboSelector = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboSelector.setLayoutData(gd_comboSelector);

        final Label lblFields = new Label(compKeyInfo, SWT.NONE);
        lblFields.setText(Messages.EntityKeyConfigComposite_Fields);
        lblFields.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        tvFields = new TreeViewer(compKeyInfo, SWT.BORDER | SWT.FULL_SELECTION);
        Tree trFields = tvFields.getTree();
        trFields.setLinesVisible(true);
        final GridData gd_trFields = new GridData(SWT.FILL, SWT.FILL, true, true);
        trFields.setLayoutData(gd_trFields);

        final Composite compFieldBtns = new Composite(compKeyInfo, SWT.NONE);
        compFieldBtns.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        final GridData gd_compFieldBtns = new GridData(SWT.FILL, SWT.CENTER, true, false);
        compFieldBtns.setLayoutData(gd_compFieldBtns);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 3;
        compFieldBtns.setLayout(gridLayout_1);

        btnAddField = new Button(compFieldBtns, SWT.NONE);
        btnAddField.setToolTipText(Messages.EntityKeyConfigComposite_AddField);
        btnAddField.setImage(addFieldIcon);

        btnEditField = new Button(compFieldBtns, SWT.NONE);
        btnEditField.setToolTipText(Messages.EntityKeyConfigComposite_EditFields);
        btnEditField.setImage(editFieldIcon);

        btnDeleteField = new Button(compFieldBtns, SWT.NONE);
        btnDeleteField.setToolTipText(Messages.EntityKeyConfigComposite_DelField);
        btnDeleteField.setImage(deleteFieldIcon);

        tvKeys.setContentProvider(new ListContentProvider());
        tvKeys.setLabelProvider(new KeyWrapperLabelProvider());

        tvFields.setContentProvider(new ListContentProvider());
        tvFields.setLabelProvider(new FieldWrapperLabelProvider());

        sashForm.setWeights(new int[] { 190, 308 });

        initUIListeners();

        addUIListeners();
    }

    public void setXSDElement(EntityWrapper sourceEntityWrapper) {

        entityWrapper = sourceEntityWrapper;

        removeUIListeners();

        tvKeys.setInput(new ArrayList<KeyWrapper>());
        if (entityWrapper != null) {
            tvKeys.setInput(Arrays.asList(entityWrapper.getKeys()));
        }

        initUIContents();

        addUIListeners();

        selectFirstKeyInCurKeyList();

        refreshUI();
    }

    private void initUIContents() {
        txtKeyName.setText("");//$NON-NLS-1$
        comboSelector.setText("");//$NON-NLS-1$
        tvFields.setInput(new ArrayList<FieldWrapper>());

        initComboSelectorContents();
    }

    private void initComboSelectorContents() {
        if (entityWrapper != null) {
            try {
                List<String> list = new ArrayList<String>();//Util.getChildElementNames("", entityWrapper.getSourceEntity());
            	list.add(0,".");//$NON-NLS-1$
                comboSelector.setItems(list.toArray(new String[0]));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                comboSelector.setItems(new String[0]);
            }
        }
    }

    private void refreshUI() {

        comboSelector.setEnabled(isKeySelected());
        txtKeyName.setEnabled(isKeySelected());
        tvFields.getControl().setEnabled(isKeySelected());
        btnDeleteKey.setEnabled(isKeySelected());
        btnAddField.setEnabled(isKeySelected());
        btnDeleteField.setEnabled(isFieldSelected() && isKeySelected());
        btnEditField.setEnabled(isFieldSelected() && isKeySelected());

        lblKeyNameErrIndicator.setVisible(false);
        lblKeyNameErrIndicator.setToolTipText("");//$NON-NLS-1$
        KeyWrapper[] selectedKeys = getSelectedKeys();
        if (selectedKeys.length > 0) {
            String errMsg = isKeyNameValid(txtKeyName.getText().trim(), selectedKeys[0]);
            lblKeyNameErrIndicator.setVisible(errMsg != null);
            lblKeyNameErrIndicator.setToolTipText(errMsg == null ? "" : errMsg);//$NON-NLS-1$
        }

        tvKeys.refresh();
        tvFields.refresh();
    }

    private boolean isFieldSelected() {
        return !tvFields.getSelection().isEmpty();
    }

    private boolean isKeySelected() {
        return !tvKeys.getSelection().isEmpty();
    }

    private KeyWrapper[] getSelectedKeys() {

        if (!isKeySelected()) {
            return new KeyWrapper[0];
        }

        List<KeyWrapper> results = new ArrayList<KeyWrapper>();
        for (Object eachSelectedObj : ((IStructuredSelection) tvKeys.getSelection()).toArray()) {
            if (eachSelectedObj instanceof KeyWrapper) {
                results.add((KeyWrapper) eachSelectedObj);
            }
        }

        return results.toArray(new KeyWrapper[0]);
    }

    private FieldWrapper[] getAllFields() {
        return ((Collection<?>) tvFields.getInput()).toArray(new FieldWrapper[0]);
    }

    private FieldWrapper[] getSelectedFields() {
        if (!isFieldSelected()) {
            return new FieldWrapper[0];
        }

        List<FieldWrapper> results = new ArrayList<FieldWrapper>();
        for (Object eachSelectedObj : ((IStructuredSelection) tvFields.getSelection()).toArray()) {
            if (eachSelectedObj instanceof FieldWrapper) {
                results.add((FieldWrapper) eachSelectedObj);
            }
        }

        return results.toArray(new FieldWrapper[0]);
    }

    private void initUIListeners() {
        initListener2TvKeys();
        initListener2TvFields();
        initListener2TxtKeyName();
        initListener2ComboSelector();
        initListener2BtnRemoveKey();
        initListener2BtnRemoveField();
        initListener2BtnEditField();
        initListener2BtnAddField();
        initListener2BtnAddKey();
    }

    private void addUIListeners() {
        tvKeys.addSelectionChangedListener(lTvKeysSelectionListener);
        tvFields.addSelectionChangedListener(lTvFieldsSelectionListener);
        txtKeyName.addModifyListener(lTxtKeyNameModifyListener);
        comboSelector.addModifyListener(lComboSelectorModifyListner);
        comboSelector.addMouseListener(lComboSelectorMouseListener);
        btnDeleteKey.addSelectionListener(lBtnRemoveKeySelectionListner);
        btnDeleteField.addSelectionListener(lBtnRemoveFieldListner);
        btnEditField.addSelectionListener(lBtnEditFieldListener);
        btnAddField.addSelectionListener(lBtnAddFieldListener);
        btnAddKey.addSelectionListener(lBtnAddKeyListener);
    }

    private void removeUIListeners() {
        tvKeys.removeSelectionChangedListener(lTvKeysSelectionListener);
        tvFields.removeSelectionChangedListener(lTvFieldsSelectionListener);
        txtKeyName.removeModifyListener(lTxtKeyNameModifyListener);
        comboSelector.removeModifyListener(lComboSelectorModifyListner);
        btnDeleteKey.removeSelectionListener(lBtnRemoveKeySelectionListner);
        btnDeleteField.removeSelectionListener(lBtnRemoveFieldListner);
        btnEditField.removeSelectionListener(lBtnEditFieldListener);
        btnAddField.removeSelectionListener(lBtnAddFieldListener);
        btnAddKey.removeSelectionListener(lBtnAddKeyListener);
    }

    private void initListener2TvKeys() {

        lTvKeysSelectionListener = new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {

                Object selectedObj = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selectedObj instanceof KeyWrapper) {
                    txtKeyName.setText(((KeyWrapper) selectedObj).getName());
                    txtKeyName.setEditable(!((KeyWrapper) selectedObj).isUniqueKey());
                    comboSelector.setText(((KeyWrapper) selectedObj).getSelector());
                    tvFields.setInput(Arrays.asList(((KeyWrapper) selectedObj).getFields()));
                }

                refreshUI();
            }
        };
    }

    private void initListener2TvFields() {

        lTvFieldsSelectionListener = new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                refreshUI();
            }
        };
    }

    private void initListener2TxtKeyName() {

        lTxtKeyNameModifyListener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                KeyWrapper[] selectedKeys = getSelectedKeys();
                if (selectedKeys.length > 0 && selectedKeys[0].getName().equals(txtKeyName.getText().trim())) {
                    return;
                }

                String errMsg = isKeyNameValid(txtKeyName.getText().trim(), selectedKeys[0]);
                lblKeyNameErrIndicator.setVisible(errMsg != null);
                lblKeyNameErrIndicator.setToolTipText(errMsg == null ? "" : errMsg);//$NON-NLS-1$

                getSelectedKeys()[0].setName(txtKeyName.getText().trim());
                tvKeys.refresh();

		        if(section!=null) {
                    section.autoCommit();
                }
            }
        };
    }

    private void initListener2ComboSelector() {

        lComboSelectorMouseListener = new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                initComboSelectorContents();
            }

        };

        lComboSelectorModifyListner = new ModifyListener() {

            public void modifyText(ModifyEvent e) {

                KeyWrapper[] selectedKeys = getSelectedKeys();
                if (selectedKeys.length > 0 && selectedKeys[0].getSelector().equals(comboSelector.getText().trim())) {
                    return;
                }

                if (!isSelectorValid(comboSelector.getText().trim())) {
                    if (selectedKeys.length > 0) {
                        comboSelector.setText(selectedKeys[0].getSelector());
                    }
                    return;
                }

                getSelectedKeys()[0].setSelector(comboSelector.getText().trim());

                if(section!=null) {
                    section.autoCommit();
                }
            }
        };
    }

    private void initListener2BtnRemoveKey() {

        lBtnRemoveKeySelectionListner = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                KeyWrapper[] selectedKeys = getSelectedKeys();
                if (selectedKeys.length == 0) {
                    return;
                }

                for (KeyWrapper eachSelectedKey : selectedKeys) {
                    entityWrapper.removeKey(eachSelectedKey);
                }

                tvKeys.setInput(Arrays.asList(entityWrapper.getKeys()));

                selectFirstKeyInCurKeyList();

                if(section!=null) {
                    section.autoCommit();
                }
            }

        };
    }

    private void initListener2BtnRemoveField() {

        lBtnRemoveFieldListner = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                KeyWrapper[] selectedKeys = getSelectedKeys();
                if (selectedKeys.length == 0) {
                    return;
                }

                FieldWrapper[] selectedFields = getSelectedFields();

                if (selectedFields.length == 0) {
                    return;
                }

                if (getAllFields().length == selectedFields.length) {
                    MessageDialog.openWarning(getShell(), Messages.Warning, Messages.EntityKeyConfigComposite_WarningMsg);
                    return;
                }

                for (FieldWrapper eachRemovedField : selectedFields) {
                    selectedKeys[0].removeField(eachRemovedField);
                }

                tvFields.setInput(Arrays.asList(selectedKeys[0].getFields()));

                if(section!=null) {
                    section.autoCommit();
                }
            }

        };
    }

    private void initListener2BtnEditField() {

        lBtnEditFieldListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                FieldWrapper[] selectedFields = getSelectedFields();
                if (selectedFields.length == 0) {
                    return;
                }

                try {
                    KeyWrapper[] selectedKeys = getSelectedKeys();
                    List<String> topChilds = getTopChildrenNames();

                    // filter already exist fields
                    List<String> fieldNames = getFieldNames(selectedKeys[0].getSourceKey());
                    for (String fdv : fieldNames) {
                        if (topChilds.contains(fdv)) {
                            topChilds.remove(fdv);
                        }
                    }

                    SelectFieldDialog selectFieldDlg = new SelectFieldDialog(getShell(),
                            "Select one field", topChilds, selectedFields[0].getXPath());//$NON-NLS-1$

                    if (selectFieldDlg.open() != Window.OK) {
                        return;
                    }

                    selectedFields[0].setXPath(selectFieldDlg.getField().trim());

                    tvFields.refresh();
                    if(section!=null) {
                        section.autoCommit();
                    }
                } catch (Exception exp) {
                    log.error(exp.getMessage(), exp);
                }
            }
        };

    }

    private void initListener2BtnAddField() {

        lBtnAddFieldListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                try {

                    KeyWrapper[] selectedKeys = getSelectedKeys();
                    if (selectedKeys.length == 0) {
                        return;
                    }

                    List<String> topChilds = getTopChildrenNames();

                    // filter already exist fields
                    List<String> fieldNames = getFieldNames(selectedKeys[0].getSourceKey());
                    for (String fdv : fieldNames) {
                        if (topChilds.contains(fdv)) {
                            topChilds.remove(fdv);
                        }
                    }


                    SelectFieldDialog selectFieldDlg = new SelectFieldDialog(getShell(),
                            Messages.EntityKeyConfigComposite_SelectOneField, topChilds, null);

                    if (selectFieldDlg.open() != Window.OK) {
                        return;
                    }

                    if ("".equals(selectFieldDlg.getField().trim())) {
                        return;
                    }

                    FieldWrapper newFieldWrapper = new FieldWrapper(selectFieldDlg.getField().trim());

                    selectedKeys[0].addField(newFieldWrapper);

                    tvFields.setInput(Arrays.asList(selectedKeys[0].getFields()));

                    tvFields.setSelection(new StructuredSelection(newFieldWrapper));

                    if(section!=null) {
                        section.autoCommit();
                    }
                } catch (Exception exp) {
                    log.error(exp.getMessage(), exp);
                }

            }
        };
    }

    private List<String> getTopChildrenNames() throws Exception {
        Map<String, XSDParticle> childElements = Util.getChildElements("", entityWrapper.getSourceEntity(), //$NON-NLS-1$
                true, new HashSet<Object>());
        List<String> names = new ArrayList<String>();
        Iterator<String> iterator = childElements.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            XSDParticle next = childElements.get(key);
            if (next.getMinOccurs() == 1 && next.getMaxOccurs() == 1) {
                names.add(key);
            }
        }
        // filter the non top level fields
        List<String> topChilds = new ArrayList<String>();
        for (String child : names) {
            if (child.indexOf('/') == -1) {
                topChilds.add(child);
            }
        }

        return topChilds;
    }

    private List<String> getFieldNames(XSDIdentityConstraintDefinition xsdIdentityConstraintDefinition) {
        List<String> fieldNames = new ArrayList<String>();
        if (xsdIdentityConstraintDefinition == null) {
            EList<XSDIdentityConstraintDefinition> identityConstraintDefinitions = entityWrapper.getSourceEntity()
                    .getIdentityConstraintDefinitions();
            for (XSDIdentityConstraintDefinition idc : identityConstraintDefinitions) {
                EList<XSDXPathDefinition> fields = idc.getFields();
                for (XSDXPathDefinition fd : fields) {
                    fieldNames.add(fd.getValue());
                }
            }
        } else {
            XSDIdentityConstraintDefinition idc = xsdIdentityConstraintDefinition;
            EList<XSDXPathDefinition> fields = idc.getFields();
            for (XSDXPathDefinition fd : fields) {
                fieldNames.add(fd.getValue());
            }
        }

        return fieldNames;
    }

    private void initListener2BtnAddKey() {

        lBtnAddKeyListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                try {
                    List<String> topChilds = getTopChildrenNames();

                    IdentityConstraintInputDialog dialog = new IdentityConstraintInputDialog(entityWrapper.getSourceEntity(),
                            getShell(), Messages.EntityKeyConfigComposite_AddANewKey, topChilds,
                            entityWrapper.getSourceEntityName());

                    dialog.setInputValidator(new NewKeyWrapperValidator(entityWrapper));
                    if (dialog.open() != Window.OK) {
                        return;
                    }

                    KeyWrapper newKeyWrapper = new KeyWrapper(dialog.getKeyName(), ".", dialog.getType(),//$NON-NLS-1$
                            new FieldWrapper[] { new FieldWrapper(dialog.getFieldName()) });

                    entityWrapper.addKey(newKeyWrapper);

                    tvKeys.setInput(Arrays.asList(entityWrapper.getKeys()));

                    tvKeys.setSelection(new StructuredSelection(newKeyWrapper));
                    if(section!=null) {
                        section.autoCommit();
                    }

                } catch (Exception exp) {
                    log.error(exp.getMessage(), exp);
                }

            }
        };

    }

    private void selectFirstKeyInCurKeyList() {

        if (tvKeys == null || entityWrapper == null) {
            return;
        }

        if (entityWrapper.hasKey()) {
            tvKeys.setSelection(new StructuredSelection(entityWrapper.getKeys()[0]));
            return;
        }

        removeUIListeners();
        initUIContents();
        addUIListeners();
    }

    private boolean isSelectorValid(String selector) {

        if (selector == null || "".equals(selector.trim())) { //$NON-NLS-1$
            return false;
        }

        return true;
    }

    private String isKeyNameValid(String keyName, KeyWrapper keyBeforeModified) {

        if (entityWrapper == null) {
            return Messages.EntityKeyConfigComposite_EntityCannotbeNull;
        }

        return new EditKeyWrapperNameValidator(entityWrapper, keyBeforeModified).isValid(keyName);
    }

    @Override
    public void dispose() {
        super.dispose();

        addKeyIcon.dispose();
        deleteKeyIcon.dispose();
        addFieldIcon.dispose();
        deleteFieldIcon.dispose();
        editFieldIcon.dispose();
        errIcon.dispose();
    }

    class EditKeyWrapperNameValidator extends EditXSDIdentityConstraintNameValidator {

        private EntityWrapper entityWrapper;

        private KeyWrapper targetKeyWrapper;

        public EditKeyWrapperNameValidator(EntityWrapper entityWrapper, KeyWrapper targetKeyWrapper) {
            super(targetKeyWrapper.getSourceKey());

            this.entityWrapper = entityWrapper;
            this.targetKeyWrapper = targetKeyWrapper;
        }

        @Override
        public String isValid(String newText) {

            if (newText == null || "".equals(newText.trim())) { //$NON-NLS-1$
                return Messages.EntityKeyConfigComposite_KeyNamecannotbeEmpty;
            }

            if (XSDIdentityConstraintCategory.UNIQUE_LITERAL.equals(targetKeyWrapper.getType())
                    && !entityWrapper.getName().equals(targetKeyWrapper.getName())) {
                return Messages.EntityKeyConfigComposite_ValidInfo1;
            }

            if (getSchema() != null && getSchema().getIdentityConstraintDefinitions() != null) {
                for (XSDIdentityConstraintDefinition eachKey : getSchema().getIdentityConstraintDefinitions()) {

                    if (eachKey.getContainer().equals(entityWrapper.getSourceEntity())) {
                        continue;
                    }

                    if (eachKey.getName().equals(newText)) {
                        return Messages.bind(Messages.EntityKeyConfigComposite_ValidInfo2, newText);
                    }
                }
            }

            for (KeyWrapper eachKeyWrapper : entityWrapper.getKeys()) {

                if (eachKeyWrapper.equals(targetKeyWrapper)) {
                    continue;
                }

                if (eachKeyWrapper.getName().equals(newText)) {
                    return Messages.bind(Messages.EntityKeyConfigComposite_ValidInfo3, newText);
                }
            }

            return null;
        }

        @Override
        public XSDSchema getSchema() {
            return entityWrapper.getSchema();
        }
    }

    class NewKeyWrapperValidator extends NewXSDIndentityConstraintValidator {

        private EntityWrapper entityWrapper;

        public NewKeyWrapperValidator(EntityWrapper entityWrapper) {
            super(entityWrapper.getSchema());

            this.entityWrapper = entityWrapper;
        }

        @Override
        public String isValid(String keyName, XSDIdentityConstraintCategory type, XSDElementDeclaration element) {

            if (keyName == null || "".equals(keyName.trim())) { //$NON-NLS-1$
                return Messages.EntityKeyConfigComposite_ValidInfo4;
            }

            for (XSDIdentityConstraintDefinition eachKey : getSchema().getIdentityConstraintDefinitions()) {

                if (eachKey.getContainer().equals(element)) {
                    continue;
                }

                if (eachKey.getName().equals(keyName)) {
                    return Messages.bind(Messages.EntityKeyConfigComposite_ValidInfo5, keyName);
                }

            }

            for (KeyWrapper eachKeyWrapper : entityWrapper.getKeys()) {

                if (type.equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)
                        && eachKeyWrapper.getType().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
                    return Messages.EntityKeyConfigComposite_ValidInfo6;
                }

                if (eachKeyWrapper.getName().equals(keyName)) {
                    return Messages.bind(Messages.EntityKeyConfigComposite_ValidInfo7, keyName);
                }
            }

            return null;
        }
    }
}
